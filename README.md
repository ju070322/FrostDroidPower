# ❄️ FrostDroidPower

> 仿 iOS 液态玻璃风格的 Android 电源控制 + 工具箱应用。

<p align="center">
  <img src="https://img.shields.io/badge/Android-14%2B-3DDC84?logo=android" />
  <img src="https://img.shields.io/badge/API-26%2B-brightgreen" />
  <img src="https://img.shields.io/badge/License-MIT-blue" />
  <img src="https://img.shields.io/badge/style-iOS%2026%20Liquid%20Glass-8E5CB8" />
  <img src="https://img.shields.io/badge/reboot-Device%20Owner%20or%20Root-orange" />
  <img src="https://img.shields.io/badge/tools-18%20entries-FF6B6B" />
</p>

<p align="center">
  <b>Frost</b> ❄️ 磨砂玻璃 · <b>Droid</b> 🤖 Android · <b>Power</b> ⚡ 电源控制
</p>

---

## ✨ 特性

- 🎨 **iOS 26 液态玻璃 UI** — 超透磨砂玻璃，蓝紫渐变，32dp 大圆角
- 🔘 **关机 & 重启按钮** — 红蓝玻璃风格按钮，带确认对话框
- 🛡️ **重启权限更准确** — 免 Root 重启需要设备所有者模式；普通设备可使用 Root 回退
- 🛠️ **18 个工具入口** — 水平仪、尺子、网络测速、WiFi 分析、单位换算、倒计时等
- ⏻ **自适应图标** — 磨砂玻璃圆底电源符号
- ⚡ **极致轻量** — APK 仅 ~40KB

## 📋 更新日志

### v1.3.1 (2026-06-10)

- ✅ **修复网络测速入口缺失** — 工具箱现在可以直接打开 Network Speed 页面
- ✅ **修正重启权限判断** — `DevicePolicyManager.reboot()` 只在设备所有者模式下调用，普通设备回退到 Root 命令
- ✅ **修复后台资源泄漏** — 离开工具页时注销水平仪传感器，并停止 CPU/网络定时刷新
- ✅ **改善 WiFi 分析提示** — 增加 Android 9+ 位置服务检查，扫描失败时给出更明确原因
- ✅ **同步版本号** — `versionCode = 4`，`versionName = "1.3.1"`
- 📝 **文档校准** — README 不再承诺普通设备管理员即可免 Root 重启，工具数量更新为 18 个入口

### v1.3.0 (2026-06-09)

- 🧰 **全部17个工具集成** — 电源控制 + 工具箱双标签页
- 📊 **CPU 监控** — 实时读取 /proc/stat，2秒自动刷新
- 📡 **网络速度** — 实时上传/下载流量统计（TrafficStats）
- 🗂️ **存储信息** — 总容量/已用/剩余空间详情
- 📶 **WiFi 分析仪** — 扫描附近网络，按信号强度排序
- 🖼️ **私密相册** — 读取媒体库，统计照片数量
- 📋 **剪贴板管理** — 查看/编辑/复制剪贴板内容
- 🔧 **权限请求机制** — 每个工具按需动态申请权限
- 🎯 **悬浮球 & 应用锁** — 跳转系统设置页手动开启
- ❌ **5项标注不可用** — GIF制作/OCR识别/铃声制作/拼图工具/手机换机（需外部库）
- 🐛 **错误处理增强** — 所有工具增加空值和异常保护
- ⚡ **APK 仅 ~42KB** — 极致轻量

### v1.2.0 (2026-06-09)

- 🛠️ **新增工具箱** — 17 个小工具入口，标签导航切换
- 📐 **水平仪** — 加速度传感器，气泡实时移动
- 📏 **尺子** — 屏幕划动测量长度（最多 15cm）
- 📋 **剪贴板管理** — 查看/编辑/复制剪贴板
- 📊 **单位换算** — 长度、重量、温度实时换算
- ⏳ **倒计时/纪念日** — 快速预设 + 手动输入
- 🌊 **白噪音** — 海浪、雨声、篝火等 5 种音效
- 📱 **标签导航** — 电源控制 / 工具箱 双标签切换

### v1.1.1 (2026-06-09)

- 🆕 **全新应用图标** — 磨砂玻璃圆底 + 霜晶装饰
- 🏷️ **底部版本号** — 主界面显示 v1.1.1

### v1.1.0 (2026-06-09)

- 🎨 **iOS 26 液态玻璃重设计** — 更轻透、更扁平
- 🧊 **全面扁平化** — 0.5dp 细边框，32dp 大圆角，圆形图标容器
- ⚙️ **设备管理员支持** — Android 9+ 免 Root 重启
- 🌐 **全屏沉浸** — 透明状态栏和导航栏

### v1.0.0 (2026-06-07)

- 🎨 首个版本，iOS 液态玻璃 UI
- 🔘 关机和重启按钮
- ⚡ 极简轻量 APK

## ⚙️ 构建要求

| 工具 | 版本 |
|------|------|
| Android SDK | 34+ |
| Build Tools | 35.0.0 |
| Gradle | 8.5 |
| JDK | 21+ |

## 🚀 快速构建

```bash
./gradlew assembleDebug
```

APK 生成：`app/build/outputs/apk/debug/app-debug.apk`

## 📦 安装

1. 下载 APK
2. 传输到 Android 手机
3. 打开文件管理器安装（需开启"未知来源"）

## ⚠️ 权限说明

### 重启

普通安装的应用不能直接重启手机。当前版本按以下顺序尝试：

1. 设备所有者模式：使用 Android `DevicePolicyManager.reboot()` 免 Root 重启
2. Root 设备：使用 `su -c reboot` 重启
3. 其他设备：显示权限不足提示

> 注意：仅激活普通「设备管理员」不等于设备所有者模式，因此不能保证免 Root 重启。

### 关机

关机通常需要 Root 权限或系统级签名；普通用户安装版会显示权限不足提示。

## 🛠️ 技术栈

- **语言**: Java
- **UI**: Android XML + Drawable 自定义渲染
- **构建**: Gradle + AGP 8.2.2
- **最低 SDK**: API 26 (Android 8.0)

## 📄 开源协议

MIT License
