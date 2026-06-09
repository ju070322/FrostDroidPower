# ❄️ FrostDroidPower

> 仿 iOS 液态玻璃风格的 Android 电源控制 + 工具箱应用。

<p align="center">
  <img src="https://img.shields.io/badge/Android-14%2B-3DDC84?logo=android" />
  <img src="https://img.shields.io/badge/API-26%2B-brightgreen" />
  <img src="https://img.shields.io/badge/License-MIT-blue" />
  <img src="https://img.shields.io/badge/style-iOS%2026%20Liquid%20Glass-8E5CB8" />
  <img src="https://img.shields.io/badge/reboot-No%20Root%20Required-success" />
  <img src="https://img.shields.io/badge/tools-17%20in%201-FF6B6B" />
</p>

<p align="center">
  <b>Frost</b> ❄️ 磨砂玻璃 · <b>Droid</b> 🤖 Android · <b>Power</b> ⚡ 电源控制
</p>

---

## ✨ 特性

- 🎨 **iOS 26 液态玻璃 UI** — 超透磨砂玻璃，蓝紫渐变，32dp 大圆角
- 🔘 **关机 & 重启按钮** — 红蓝玻璃风格按钮，带确认对话框
- 🛡️ **免 Root 重启** — Android 9+ 激活设备管理员即可
- 🛠️ **17 个小工具** — 水平仪、尺子、单位换算、倒计时、白噪音等
- ⏻ **自适应图标** — 磨砂玻璃圆底电源符号
- ⚡ **极致轻量** — APK 仅 ~40KB

## 📋 更新日志

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

### 重启（免 Root）

Android 9+ 只需激活设备管理员：

1. 打开应用 → 点击顶部「激活设备管理员」
2. 在系统设置中确认
3. 返回应用 → 点击「重启」

### 关机 / 旧版 Android

需要 Root 权限或系统级签名。

## 🛠️ 技术栈

- **语言**: Java
- **UI**: Android XML + Drawable 自定义渲染
- **构建**: Gradle + AGP 8.2.2
- **最低 SDK**: API 26 (Android 8.0)

## 📄 开源协议

MIT License