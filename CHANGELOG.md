# GreenWall Mobile 更新日志 (Changelog)

本日志记录了 GreenWall Mobile 从 v1.1.0 到 v1.1.2 的所有关键改进、修复与重构历程。

---

## [v1.1.2] - 2026-03-13
### 🏗 全面重构与生产级优化
*   **重构 i18n 核心逻辑**：
    *   引入异步加载机制，确保语言配置在 UI 渲染前正确初始化，彻底解决汉化失效问题。
    *   集成 `expo-localization` 插件，支持根据系统语言自动识别并切换（中文/英文）。
*   **清理硬编码文本**：
    *   对设置页面（Settings）、字符预览（Characters）及图例（Legend）中的硬编码文本进行了全面清理，所有文本均通过国际化框架管理。
*   **优化项目配置与依赖**：
    *   修复了 GitHub Actions 构建时缺失 `expo-localization` 依赖的问题。
    *   同步更新 `app.config.ts` 插件配置，提升 Android 构建产物的稳定性。
*   **移除冗余代码**：
    *   清理了冗余的 Manus 运行时初始化代码，优化了应用启动性能。

---

## [v1.1.1] - 2026-03-13
### 🔧 汉化修复与版本号校正
*   **初步修复汉化逻辑**：
    *   尝试修复 `lib/i18n.tsx` 中的翻译加载逻辑。
    *   在 `Characters` 页面增加对分段控制器选项的更新逻辑，确保汉化生效。
*   **版本号同步**：
    *   将 `package.json` 和 `app.config.ts` 中的版本号统一更新为 v1.1.1。
*   **构建系统调整**：
    *   更新了 GitHub Actions 配置文件 `android-build.yml`，尝试触发自动化构建。

---

## [v1.1.0] - 2026-03-13
### ✨ 功能增强与基础架构
*   **新增字符放置功能**：
    *   支持在日历画布上直接放置大写字母、小写字母、数字及符号。
*   **UI/UX 优化**：
    *   引入了 Miuix 组件库，优化了按钮、卡片及分段控制器的视觉表现。
    *   增加了字符预览功能，支持在放置前查看图案。
*   **基础国际化框架**：
    *   初步搭建了基于 `I18nProvider` 的国际化框架，支持中英文切换。
*   **GitHub 集成**：
    *   完善了 GitHub API 调用逻辑，支持创建远程仓库并推送贡献数据。

---

> **Powered by Manus**
> 
> 本项目由 Manus 自动化重构与维护。
> GitHub 仓库: [xiaoluoshen/GreenWall-Mobile](https://github.com/xiaoluoshen/GreenWall-Mobile)
