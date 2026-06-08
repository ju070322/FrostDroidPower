package com.helloapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView statusText;
    private View shutdownButton;
    private View rebootButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        shutdownButton = findViewById(R.id.shutdownButton);
        rebootButton = findViewById(R.id.rebootButton);

        shutdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog("关机", "确定要关闭设备吗？", new Runnable() {
                    @Override
                    public void run() {
                        performAction(false);
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
                        performAction(true);
                    }
                });
            }
        });
    }

    private void showConfirmDialog(String title, String message, final Runnable onConfirm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
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

    private void performAction(boolean isReboot) {
        String actionName = isReboot ? "重启" : "关机";

        try {
            // Method 1: Try PowerManager (requires system-level REBOOT permission)
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

            if (isReboot) {
                if (pm != null) {
                    try {
                        pm.reboot(null);
                        return;
                    } catch (SecurityException e) {
                        // Fall through to next method
                    } catch (Exception e) {
                        // Fall through
                    }
                }
            } else {
                // For shutdown, there's no direct PowerManager API in non-system apps
                // Try shell command with root
            }

            // Method 2: Try shell command with root
            String command = isReboot ? "reboot" : "reboot -p";
            try {
                Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    showStatus("正在" + actionName + "…");
                    return;
                }
            } catch (Exception e) {
                // Not rooted
            }

            // Method 3: Try without root (unlikely to work)
            try {
                Process process = Runtime.getRuntime().exec(command);
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    showStatus("正在" + actionName + "…");
                    return;
                }
            } catch (Exception e) {
                // No permission
            }

            // All methods failed
            showNoPermissionDialog();

        } catch (Exception e) {
            showStatus("操作失败: " + e.getMessage());
        }
    }

    private void showNoPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限不足")
                .setMessage("关机/重启需要系统级权限。\n\n" +
                        "需要以下条件之一：\n" +
                        "• 设备已获取 Root 权限\n" +
                        "• 应用为系统级应用\n\n" +
                        "当前仅为普通应用，无法执行系统操作。")
                .setPositiveButton("知道了", null)
                .show();
    }

    private void showStatus(String msg) {
        statusText.setText(msg);
        statusText.setVisibility(View.VISIBLE);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}