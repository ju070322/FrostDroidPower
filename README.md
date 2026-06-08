# ❄️ FrostDroidPower

> 仿 iOS 液态玻璃风格的 Android 电源控制应用。

<p align="center">
  <img src="https://img.shields.io/badge/Android-14%2B-3DDC84?logo=android" />
  <img src="https://img.shields.io/badge/API-26%2B-brightgreen" />
  <img src="https://img.shields.io/badge/License-MIT-blue" />
  <img src="https://img.shields.io/badge/style-iOS%2026%20Liquid%20Glass-8E5CB8" />
  <img src="https://img.shields.io/badge/reboot-No%20Root%20Required-success" />
</p>

<p align="center">
  <b>Frost</b> ❄️ 磨砂玻璃 · <b>Droid</b> 🤖 Android · <b>Power</b> ⚡ 电源控制
</p>

---

## ✨ 特性

- 🎨 **iOS 26 液态玻璃 UI** — 超透磨砂玻璃，蓝紫渐变，32dp 大圆角
- 🔘 **关机 & 重启按钮** — 红蓝玻璃风格按钮，带确认对话框
- 🛡️ **免 Root 重启** — Android 9+ 激活设备管理员即可
- ⏻ **自适应图标** — 电源符号
- ⚡ **极致轻量** — APK 仅 ~23KB

## 📸 预览

```
┌─────────────────────────────┐
│  ⚙ 激活设备管理员 · 免 Root  │
│                             │
│     ┌─────────────────┐     │
│     │ FrostDroidPower │     │
│     │ ❄️ Frost·Droid·Power│   │
│     │      ⏻          │     │
│     │      ───        │     │
│     │ ⏻ │ 关机        │     │
│     │    │ 关闭设备    │     │
│     │                │     │
│     │ ⟳ │ 重启        │     │
│     │    │ 重新启动    │     │
│     └─────────────────┘     │
│                             │
└─────────────────────────────┘
```

## 📋 更新日志

### v1.1.0 (2026-06-09)

- 🎨 **全新 iOS 26 液态玻璃风格** — 更轻透的渐变背景、0.5dp 细边框、32dp 大圆角、圆形图标容器
- 🧊 **全面扁平化** — 降低玻璃透明度，更轻薄通透
- 🌐 **全屏沉浸** — 透明状态栏和导航栏
- ⚙️ **设备管理员支持** — Android 9+ 可免 Root 重启
- 🔄 **智能降级** — 自动尝试 PowerManager → Root Shell → 提示引导
- 🏷️ **更名 FrostDroidPower** — 应用名和项目名统一

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