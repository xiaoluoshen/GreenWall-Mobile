# GreenWall Mobile

<p align="center">
  <img src="assets/images/icon.png" width="120" height="120" alt="GreenWall Logo" />
</p>

<p align="center">
  <strong>在手机上给你的 GitHub 贡献墙画画</strong>
</p>

<p align="center">
  <a href="https://github.com/xiaoluoshen/GreenWall-Mobile/releases/latest">
    <img src="https://img.shields.io/github/v/release/xiaoluoshen/GreenWall-Mobile?style=flat-square" alt="Release" />
  </a>
  <a href="https://github.com/xiaoluoshen/GreenWall-Mobile/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/xiaoluoshen/GreenWall-Mobile?style=flat-square" alt="License" />
  </a>
  <a href="https://github.com/xiaoluoshen/GreenWall-Mobile/releases/latest">
    <img src="https://img.shields.io/github/downloads/xiaoluoshen/GreenWall-Mobile/total?style=flat-square" alt="Downloads" />
  </a>
  <a href="https://t.me/lsposed0">
    <img src="https://img.shields.io/badge/Telegram-频道-blue?style=flat-square&logo=telegram" alt="Telegram" />
  </a>
</p>

---

## 简介

GreenWall Mobile 是 [GreenWall](https://github.com/zmrlft/GreenWall) 的移动端版本，基于 React Native（Expo）开发。它可以让你在手机上自由地在 GitHub 贡献热力图上绘画，然后通过 GitHub API 一键同步到你的 GitHub 账号，打造个性化的贡献墙。

UI 设计参考了 [MIUIX](https://github.com/compose-miuix-ui/miuix) 风格，大圆角卡片搭配简洁列表布局，支持深色 / 浅色主题自动切换。

## 功能特性

| 功能 | 说明 |
|------|------|
| **贡献日历绘制** | 完整的 52 周 x 7 天网格，支持触摸拖拽绘制 |
| **画笔 / 橡皮擦** | 自由切换绘制和擦除模式 |
| **4 档强度** | 对应 GitHub 贡献的 4 种绿色深度（1 / 3 / 6 / 9） |
| **字符图案** | 内置 A-Z、a-z、0-9 及常用符号图案，一键盖章到日历 |
| **年份选择** | 支持近 10 年的年份切换，想画哪年画哪年 |
| **撤销 / 重做** | 操作可回退，不怕画错 |
| **全绿 / 重置** | 一键填满或清空整个日历 |
| **GitHub 集成** | 通过 Personal Access Token 登录，创建仓库并推送贡献数据 |
| **中英文切换** | 支持简体中文和英文界面 |
| **深色模式** | 自动适配系统深色 / 浅色主题 |
| **本地存储** | 绘制数据和设置自动保存到本地 |

## 下载安装

前往 [Releases](https://github.com/xiaoluoshen/GreenWall-Mobile/releases/latest) 页面下载最新版 APK 安装包。

## 使用说明

### 1. 绘制贡献图

打开应用后进入「画布」页面，选择年份，然后用手指在日历网格上绘制。底部工具栏可以切换画笔 / 橡皮擦，以及调节绿色强度。

### 2. 使用字符图案

切换到「字符」页面，选择想要的字符（支持大写字母、小写字母、数字和符号），点击即可将图案放置到当前日历上。

### 3. 推送到 GitHub

进入「设置」页面，输入你的 GitHub Personal Access Token 完成登录。登录后回到画布页面，点击「推送」按钮，填写仓库名称，应用会自动创建仓库并将贡献数据推送到 GitHub。

> **关于 Token 安全：** Token 仅保存在你的设备本地（AsyncStorage），不会经过任何第三方服务器。项目代码完全开源，欢迎审查。

### 如何获取 GitHub Token

1. 打开 [GitHub Token 设置页面](https://github.com/settings/tokens)
2. 点击 **Generate new token (classic)**
3. 勾选 `repo` 权限
4. 生成并复制 Token

## 技术栈

| 技术 | 版本 |
|------|------|
| React Native | 0.81 |
| Expo SDK | 54 |
| TypeScript | 5.9 |
| NativeWind (Tailwind CSS) | 4.x |
| Expo Router | 6 |

## 从源码构建

```bash
# 克隆仓库
git clone https://github.com/xiaoluoshen/GreenWall-Mobile.git
cd GreenWall-Mobile

# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev

# 构建 Android APK
npx expo prebuild --platform android
cd android
./gradlew assembleRelease
```

## 项目结构

```
app/
  (tabs)/
    index.tsx          ← 画布页面（贡献日历绘制）
    characters.tsx     ← 字符图案选择页面
    settings.tsx       ← 设置页面
components/
  contribution-calendar.tsx  ← 贡献日历网格组件
  miuix/                     ← MIUIX 风格 UI 组件库
lib/
  contribution-store.ts      ← 贡献数据状态管理
  character-patterns.ts      ← 字符图案数据
  github-api.ts              ← GitHub API 服务
  i18n.tsx                   ← 国际化
```

## 致谢

- [GreenWall](https://github.com/zmrlft/GreenWall) — 原始项目，提供了核心创意和功能设计
- [MIUIX](https://github.com/compose-miuix-ui/miuix) — UI 设计参考

## 联系

- Telegram 频道：[@lsposed0](https://t.me/lsposed0)
- GitHub Issues：[提交反馈](https://github.com/xiaoluoshen/GreenWall-Mobile/issues)

## 许可证

本项目基于 MIT 许可证开源，详见 [LICENSE](LICENSE) 文件。
