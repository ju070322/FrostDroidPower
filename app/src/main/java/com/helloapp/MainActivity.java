package com.helloapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_ADMIN = 1001;

    private View shutdownButton, rebootButton, adminStatusBar;
    private TextView statusText, versionText;
    private TextView tabPower, tabTools;
    private ScrollView powerPage, toolsPage;
    private GridLayout toolsGrid;

    private DevicePolicyManager dpm;
    private ComponentName adminComponent;
    private boolean isPowerTab = true;

    // 工具数据
    private static final String[][] TOOLS = {
        {"水平仪",      "\uD83D\uDCF0"},
        {"尺子",        "\uD83D\uDCCF"},
        {"剪贴板管理",  "\uD83D\uDCCB"},
        {"悬浮球",      "\uD83D\uDD35"},
        {"应用锁",      "\uD83D\uDD12"},
        {"私密相册",    "\uD83D\uDCF8"},
        {"垃圾清理",    "\uD83D\uDDD1\uFE0F"},
        {"CPU监控",     "\u2699\uFE0F"},
        {"网络测速",     "\uD83D\uDCE1"},
        {"WiFi分析仪",  "\uD83D\uDCF6"},
        {"单位换算",    "\uD83D\uDCCA"},
        {"倒计时/纪念日","\u23F3"},
        {"白噪音",      "\uD83C\uDFB5"},
        {"GIF制作",     "\uD83C\uDFAC"},
        {"OCR识别",     "\uD83D\uDD0D"},
        {"铃声制作",    "\uD83D\uDD14"},
        {"拼图工具",    "\uD83D\uDDBC\uFE0F"},
        {"手机换机",    "\uD83D\uDCE1"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, PowerAdminReceiver.class);

        initViews();
        setupTabs();
        setupPowerButtons();
        buildToolsGrid();
        updateAdminStatus();
    }

    private void initViews() {
        adminStatusBar = findViewById(R.id.adminStatusBar);
        tabPower = findViewById(R.id.tabPower);
        tabTools = findViewById(R.id.tabTools);
        powerPage = findViewById(R.id.powerPage);
        toolsPage = findViewById(R.id.toolsPage);
        toolsGrid = findViewById(R.id.toolsGrid);
        shutdownButton = findViewById(R.id.shutdownButton);
        rebootButton = findViewById(R.id.rebootButton);
        statusText = findViewById(R.id.statusText);
        versionText = findViewById(R.id.versionText);
    }

    // ======== Tab 切换 ========

    private void setupTabs() {
        tabPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(true);
            }
        });
        tabTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(false);
            }
        });
    }

    private void switchTab(boolean toPower) {
        isPowerTab = toPower;
        powerPage.setVisibility(toPower ? View.VISIBLE : View.GONE);
        toolsPage.setVisibility(toPower ? View.GONE : View.VISIBLE);
        adminStatusBar.setVisibility(toPower ? View.VISIBLE : View.GONE);

        tabPower.setBackgroundResource(toPower ? R.drawable.tab_active : R.drawable.tab_inactive);
        tabPower.setTextColor(toPower ? 0xFFFFFFFF : 0xB0FFFFFF);
        tabTools.setBackgroundResource(toPower ? R.drawable.tab_inactive : R.drawable.tab_active);
        tabTools.setTextColor(toPower ? 0xB0FFFFFF : 0xFFFFFFFF);

        if (toPower) updateAdminStatus();
    }

    // ======== 电源控制 ========

    private void setupPowerButtons() {
        if (adminStatusBar != null) {
            adminStatusBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestAdmin();
                }
            });
        }

        shutdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirm("关机", "确定要关闭设备吗？", new Runnable() {
                    @Override
                    public void run() {
                        doShutdown();
                    }
                });
            }
        });

        rebootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirm("重启", "确定要重新启动设备吗？", new Runnable() {
                    @Override
                    public void run() {
                        doReboot();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdminStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADMIN) {
            updateAdminStatus();
            if (isAdminActive()) {
                Toast.makeText(this, "设备管理员已激活。免 Root 重启仍需要设备所有者模式。", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateAdminStatus() {
        if (adminStatusBar == null) return;
        TextView t = findViewById(android.R.id.text1);
        if (t != null) {
            if (isAdminActive()) {
                if (isDeviceOwner()) {
                    t.setText("✓ 设备所有者已激活 · 可免 Root 重启");
                    t.setTextColor(0xFFA5D6A7);
                } else {
                    t.setText("⚠ 设备管理员已激活 · 重启仍需设备所有者或 Root");
                    t.setTextColor(0xFFFFCC80);
                }
            } else {
                t.setText("⚙ 点击激活设备管理员（普通安装不能直接免 Root 重启）");
                t.setTextColor(0xFFFFCC80);
            }
        }
    }

    private boolean isAdminActive() {
        return dpm != null && dpm.isAdminActive(adminComponent);
    }

    private boolean isDeviceOwner() {
        return dpm != null && Build.VERSION.SDK_INT >= 21 && dpm.isDeviceOwnerApp(getPackageName());
    }

    private void requestAdmin() {
        if (isAdminActive()) return;
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后可使用设备管理功能。免 Root 重启需要设备所有者模式。");
        startActivityForResult(intent, REQUEST_CODE_ADMIN);
    }

    private void doReboot() {
        if (Build.VERSION.SDK_INT >= 24 && isAdminActive() && isDeviceOwner()) {
            try { dpm.reboot(adminComponent); showStatus("正在重启…"); return; }
            catch (Exception ignored) {}
        }
        tryShell(new String[]{"su", "-c", "reboot"}, "正在重启…");
    }

    private void doShutdown() {
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (pm != null) {
                    pm.getClass().getMethod("shutdown", boolean.class, String.class)
                            .invoke(pm, false, "user_request");
                    showStatus("正在关机…"); return;
                }
            } catch (Exception ignored) {}
        }
        tryShell(new String[]{"su", "-c", "reboot -p"}, "正在关机…");
    }

    private void tryShell(final String cmd, final String msg) {
        tryShell(new String[]{cmd}, msg);
    }

    private void tryShell(final String[] cmd, final String msg) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process p = Runtime.getRuntime().exec(cmd);
                    if (p.waitFor() == 0) { showStatus(msg); return; }
                } catch (Exception ignored) {}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNoPermDialog();
                    }
                });
            }
        });
        t.start();
    }

    // ======== 工具网格 ========

    private void buildToolsGrid() {
        toolsGrid.removeAllViews();
        int cols = toolsGrid.getColumnCount();
        if (cols < 1) cols = 3;

        for (int i = 0; i < TOOLS.length; i++) {
            final int index = i;
            View item = getLayoutInflater().inflate(R.layout.tool_item, null);
            TextView icon = item.findViewById(R.id.toolIcon);
            TextView name = item.findViewById(R.id.toolName);
            icon.setText(TOOLS[i][1]);
            name.setText(TOOLS[i][0]);

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(4, 4, 4, 4);
            item.setLayoutParams(lp);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTool(index);
                }
            });

            toolsGrid.addView(item);
        }
    }

    private void openTool(int index) {
        String[] toolIds = {"level","ruler","clipboard","floatball","applock","album","cleaner","cpu","network","wifi","converter","countdown","noise","gif","ocr","ringtone","collage","clone"};
        String id = (index < toolIds.length) ? toolIds[index] : "";
        if ("clipboard".equals(id)) {
            showClipboard();
        } else {
            startActivity(new Intent(this, ToolsActivity.class).putExtra("tool", id));
        }
    }

    private void showComingSoon(String name) {
        new AlertDialog.Builder(this)
                .setTitle(name)
                .setMessage("\u5f00\u53d1\u4e2d...")
                .setPositiveButton("知道了", null)
                .show();
    }

    private void showClipboard() {
        final android.content.ClipboardManager cm =
                (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm == null) return;

        android.content.ClipData clip = cm.getPrimaryClip();
        String current = "";
        if (clip != null && clip.getItemCount() > 0) {
            current = clip.getItemAt(0).coerceToText(this).toString();
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final TextView edit = new TextView(this);
        edit.setText(current.isEmpty() ? "(剪贴板为空)" : current);
        edit.setTextColor(0xFFFFFFFF);
        edit.setTextSize(16);
        edit.setBackgroundColor(0x20FFFFFF);
        edit.setPadding(16, 16, 16, 16);
        edit.setGravity(Gravity.CENTER_VERTICAL);
        edit.setMinHeight(80);

        layout.addView(edit);

        new AlertDialog.Builder(this)
                .setTitle("剪贴板管理")
                .setView(layout)
                .setPositiveButton("复制到剪贴板", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = edit.getText().toString();
                        if (!text.isEmpty() && !text.equals("(剪贴板为空)")) {
                            android.content.ClipData clip =
                                    android.content.ClipData.newPlainText("label", text);
                            cm.setPrimaryClip(clip);
                            Toast.makeText(MainActivity.this, "已复制 ✓", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("关闭", null)
                .show();
    }

    // ======== UI 辅助 ========

    private void showConfirm(String title, String msg, final Runnable action) {
        new AlertDialog.Builder(this)
                .setTitle(title).setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int w) { action.run(); }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showAdminDialog(String action) {
        new AlertDialog.Builder(this)
                .setTitle("需要设备管理员权限")
                .setMessage(action + "需要设备所有者模式或 Root 权限。\n普通设备管理员激活后仍可能无法执行。")
                .setPositiveButton("去激活", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int w) { requestAdmin(); }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showNoPermDialog() {
        new AlertDialog.Builder(this)
                .setTitle("权限不足")
                .setMessage("需要 Root 权限或系统级签名。\n\n" +
                        "💡 重启可在 Android 9+ 上免 Root（需激活设备管理员）。")
                .setPositiveButton("知道了", null)
                .show();
    }

    private void showStatus(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (statusText != null) {
                    statusText.setText(msg);
                    statusText.setVisibility(View.VISIBLE);
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
