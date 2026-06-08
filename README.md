# ⏻ 电源控制 (PowerControl)

仿 iOS 液态玻璃风格的 Android 电源控制应用。

<p align="center">
  <img src="https://img.shields.io/badge/Android-14%2B-3DDC84?logo=android" />
  <img src="https://img.shields.io/badge/API-26%2B-brightgreen" />
  <img src="https://img.shields.io/badge/License-MIT-blue" />
</p>

---

## ✨ 特性

- 🎨 **iOS 液态玻璃 UI** — 磨砂玻璃效果，蓝紫渐变背景
- 🔘 **关机 & 重启按钮** — 红色/蓝色玻璃风格按钮
- 🛡️ **确认对话框** — 防止误操作
- 📱 **自适应图标** — 电源符号图标
- ⚡ **轻量** — APK 仅 ~20KB

## 📸 预览

```
┌─────────────────────┐
│   ═══ 电源控制 ═══   │
│   仿 iOS 液态玻璃    │
│                     │
│   ┌─────────────┐   │
│   │     ⏻      │   │
│   │ 请选择操作   │   │
│   │─────────────│   │
│   │ ⏻ │ 关机    │   │
│   │    │ 关闭设备 │   │
│   │─────────────│   │
│   │ ⟳ │ 重启    │   │
│   │    │ 重新启动 │   │
│   └─────────────┘   │
│                     │
└─────────────────────┘
```

## ⚙️ 构建要求

| 工具 | 版本 |
|------|------|
| Android SDK | 34+ |
| Build Tools | 35.0.0 |
| Gradle | 8.5 |
| JDK | 21+ |

## 🚀 快速构建

### 方式一：Gradle（推荐）

```bash
./gradlew assembleDebug
```

APK 生成位置：`app/build/outputs/apk/debug/app-debug.apk`

### 方式二：一键脚本

```bash
build_apk.bat
```

## 📦 安装

1. 下载 `app-debug.apk`
2. 传输到 Android 手机
3. 打开文件管理器安装（需开启"未知来源"）

## ⚠️ 权限说明

关机/重启需要 **系统级权限**，普通应用无法直接执行：

| 方式 | 条件 | 说明 |
|------|------|------|
| PowerManager API | 系统签名应用 | 需要与系统相同的签名 |
| Shell 命令 | Root 权限 | 设备已 root 时自动调用 `su -c reboot` |
| 普通模式 | 无 | UI 展示，操作时提示权限不足 |

## 🛠️ 技术栈

- **语言**: Java
- **UI**: Android XML + Drawable
- **构建**: Gradle + AGP 8.2.2
- **最低 SDK**: API 26 (Android 8.0)
- **目标 SDK**: API 34

## 📄 开源协议

MIT License