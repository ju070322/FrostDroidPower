# FrostDroidPower v1.3.1

发布日期：2026-06-10

## 修复内容

- 修复工具箱中“网络测速”功能已经实现但没有入口的问题。
- 修正重启权限逻辑：只有设备所有者模式才调用 `DevicePolicyManager.reboot()`，普通设备管理员不再被误判为可免 Root 重启。
- 重启失败时改为 Root 命令回退，并在无权限时显示更准确提示。
- 修复水平仪传感器退出页面后未注销的问题。
- 修复 CPU 监控、网络测速退出页面后定时刷新仍可能继续执行的问题。
- 改善 WiFi 分析仪：增加 Android 9+ 位置服务检查，并在系统限制扫描时显示明确说明。

## 文档与版本

- README 已同步 v1.3.1 更新日志。
- README 已将“普通设备管理员即可免 Root 重启”的说明修正为“设备所有者模式或 Root”。
- 工具数量说明更新为 18 个入口。
- 版本号更新为 `versionCode = 4` / `versionName = 1.3.1`。

## APK

- Debug APK 构建产物：`app/build/outputs/apk/debug/app-debug.apk`
- 发布用 APK 可重命名为：`FrostDroidPower-v1.3.1.apk`
