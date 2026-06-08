package com.helloapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_ADMIN = 1001;

    private TextView statusText;
    private View shutdownButton;
    private View rebootButton;
    private View adminStatusBar;

    private DevicePolicyManager dpm;
    private ComponentName adminComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, PowerAdminReceiver.class);

        statusText = findViewById(R.id.statusText);
        shutdownButton = findViewById(R.id.shutdownButton);
        rebootButton = findViewById(R.id.rebootButton);
        adminStatusBar = findViewById(R.id.adminStatusBar);

        // 点击设备管理员状态栏可激活
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
                showConfirmDialog("关机", "确定要关闭设备吗？", new Runnable() {
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
                showConfirmDialog("重启", "确定要重新启动设备吗？", new Runnable() {
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
                Toast.makeText(this, "设备管理员已激活 ✓ 重启功能可免 Root 使用", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateAdminStatus() {
        if (adminStatusBar == null) return;
        TextView adminText = adminStatusBar.findViewById(android.R.id.text1);
        if (adminText != null) {
            if (isAdminActive()) {
                adminText.setText("✓ 设备管理员已激活 · 重启可免 Root");
                adminText.setTextColor(0xFFA5D6A7);
            } else {
                adminText.setText("⚙ 点击激活设备管理员（免 Root 重启）");
                adminText.setTextColor(0xFFFFCC80);
            }
        }
    }

    private boolean isAdminActive() {
        return dpm != null && dpm.isAdminActive(adminComponent);
    }

    private void requestAdmin() {
        if (isAdminActive()) return;
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "激活后 FrostDroidPower 可使用免 Root 重启功能");
        startActivityForResult(intent, REQUEST_CODE_ADMIN);
    }

    // ========== 重启 ==========

    private void doReboot() {
        // Method 1: Device Admin (API 28+, no root required)
        if (Build.VERSION.SDK_INT >= 28 && isAdminActive()) {
            try {
                dpm.reboot(adminComponent);
                showStatus("正在重启…");
                return;
            } catch (SecurityException e) {
                // Fall through
            } catch (Exception e) {
                // Fall through
            }
        }

        // Method 2: Device Admin not active - guide user to activate it
        if (Build.VERSION.SDK_INT >= 28 && !isAdminActive()) {
            showActivateAdminDialog("重启");
            return;
        }

        // Method 3: Root shell (Android 8.x, API 26-27)
        tryShellReboot();
    }

    private void tryShellReboot() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
            if (process.waitFor() == 0) { showStatus("正在重启…"); return; }
        } catch (Exception ignored) {}

        try {
            Process process = Runtime.getRuntime().exec("reboot");
            if (process.waitFor() == 0) { showStatus("正在重启…"); return; }
        } catch (Exception ignored) {}

        showNoPermissionDialog("重启", false);
    }

    // ========== 关机 ==========

    private void doShutdown() {
        // Shutdown via PowerManager (API 30+, hidden API - may not work)
        if (Build.VERSION.SDK_INT >= 30) {
            try {
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (pm != null) {
                    // Attempt shutdown via reflection (undocumented, may fail)
                    pm.getClass().getMethod("shutdown", boolean.class, String.class)
                            .invoke(pm, false, "user_request");
                    showStatus("正在关机…");
                    return;
                }
            } catch (Exception ignored) {}
        }

        // Root shell
        tryShellShutdown();
    }

    private void tryShellShutdown() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot -p"});
            if (process.waitFor() == 0) { showStatus("正在关机…"); return; }
        } catch (Exception ignored) {}

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "svc power shutdown"});
            if (process.waitFor() == 0) { showStatus("正在关机…"); return; }
        } catch (Exception ignored) {}

        showNoPermissionDialog("关机", true);
    }

    // ========== UI 辅助 ==========

    private void showConfirmDialog(String title, String message, final Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showActivateAdminDialog(String action) {
        new AlertDialog.Builder(this)
                .setTitle("需要设备管理员权限")
                .setMessage("Android 9+ 无需 Root 即可重启，\n" +
                        "只需激活 FrostDroidPower 为设备管理员。\n\n" +
                        "点击「去激活」→ 按步骤开启即可。")
                .setPositiveButton("去激活", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestAdmin();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showNoPermissionDialog(String action, boolean isShutdown) {
        String extra = "";
        if (isShutdown) {
            extra = "\n\n💡 关机始终需要 Root 权限。\n重启可在 Android 9+ 上免 Root（需激活设备管理员）。";
        }
        new AlertDialog.Builder(this)
                .setTitle("权限不足")
                .setMessage("无法执行「" + action + "」。\n\n" +
                        "需要以下条件之一：\n" +
                        "• 激活设备管理员（重启专用，无需 Root）\n" +
                        "• 设备已获取 Root 权限\n" +
                        "• 应用为系统级应用" + extra)
                .setPositiveButton("知道了", null)
                .show();
    }

    private void showStatus(String msg) {
        if (statusText != null) {
            statusText.setText(msg);
            statusText.setVisibility(View.VISIBLE);
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}