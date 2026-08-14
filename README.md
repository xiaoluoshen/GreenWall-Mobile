# GreenWall Mobile

> 在 Android 手机上设计并同步 GitHub 贡献热力图。

[![最新发布](https://img.shields.io/github/v/release/xiaoluoshen/GreenWall-Mobile?style=flat-square)](https://github.com/xiaoluoshen/GreenWall-Mobile/releases/latest)
[![许可证](https://img.shields.io/github/license/xiaoluoshen/GreenWall-Mobile?style=flat-square)](LICENSE)

GreenWall Mobile 是一个开源的 GitHub 贡献热力图绘制工具。你可以在手机上选择年份、手绘或盖印字符图案，然后直接通过 GitHub API 将对应的提交写入自己的仓库。应用采用 Material 3 界面，并支持系统浅色和深色模式。

## 下载与兼容性

请从 [最新 Release](https://github.com/xiaoluoshen/GreenWall-Mobile/releases/latest) 下载 Android APK。当前发布包仅包含 **arm64-v8a** 原生库，适用于绝大多数近年的 64 位 Android 设备；32 位 `armeabi-v7a` 设备不受支持。

| 项目 | 当前信息 |
| --- | --- |
| 最新版本 | v1.3.1 |
| 安装包 | Android APK |
| CPU 架构 | arm64-v8a |
| Android 最低版本 | Android 7.0（API 24） |
| 界面语言 | 简体中文、English |

## 主要功能

| 功能 | 说明 |
| --- | --- |
| 贡献日历绘制 | 在 52 周 × 7 天网格中通过触摸拖动编辑贡献强度。 |
| 画笔与橡皮擦 | 快速添加、降低或清除指定日期的贡献。 |
| 字符图案 | 选择字母、数字或符号图案，并自动居中盖印到当前日历。 |
| 年份与历史操作 | 可切换年份，并支持撤销、重做、全绿和重置。 |
| GitHub 同步 | 创建指定仓库并按日期写入提交，生成对应的贡献记录。 |
| 本地持久化 | 草稿、设置和登录状态仅保存在设备本地。 |
| 主题与动效 | 自动适配系统明暗主题；卡片和按钮提供轻量反馈，并尊重系统“减少动态效果”偏好。 |

## 使用方法

首先在“画布”页面选择年份，并使用画笔、橡皮擦或强度选项编辑贡献格。需要快速制作图案时，可前往“字符”页面选择字符；选中的图案会被带回画布并自动盖印。

完成设计后，进入“设置”页面填入 GitHub Personal Access Token。验证成功后，返回画布点击“推送”，输入目标仓库名称并选择公开或私有。应用会创建仓库（如有需要），再按已绘制的日期写入提交。

### GitHub Token 权限

应用会调用 GitHub API 读取账户信息、获取提交邮箱、创建仓库及写入仓库内容。使用经典 Personal Access Token 时，请授予 `repo` 权限；如使用细粒度 Token，请确保其具备账户仓库创建权限，以及目标仓库的 Contents 读写权限。

> Token 和账户信息只写入设备本地存储，应用不会通过自建服务转发 Token。请妥善保管 Token，并在不再使用时于应用设置页清除，或前往 GitHub 撤销它。

## 从源码运行

本项目使用 Expo SDK 54、React Native、TypeScript、Expo Router 和 React Native Paper。请先安装 Node.js 与 pnpm，然后在项目根目录执行以下命令：

```bash
pnpm install
pnpm start
```

开发期间可以使用 Android 模拟器或已连接的设备运行应用。常用质量检查命令如下：

```bash
pnpm lint --max-warnings=0
pnpm check
pnpm test
```

如需导出 Web 静态文件，可执行：

```bash
pnpm build
```

## 项目结构

```text
app/
  (tabs)/             # 画布、字符和设置页面
components/           # Material 3 组件与贡献日历组件
lib/
  contribution-store.ts   # 贡献数据、历史记录与本地持久化
  pattern-stamp.ts        # 字符图案解析与盖印逻辑
  github-api.ts           # GitHub API 调用
  i18n.tsx                # 中英文国际化
```

## 贡献与许可证

欢迎提交 Issue 和 Pull Request。请在提交前运行代码检查与测试，确保改动保持稳定。

本项目基于 [MIT 许可证](LICENSE) 开源。项目的核心创意参考自 [GreenWall](https://github.com/zmrlft/GreenWall)。
