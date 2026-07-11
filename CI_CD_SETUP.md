# 🚀 CI/CD 配置指南

## 概览

本项目已配置完整的 GitHub Actions CI/CD 工作流，支持：

✅ 代码质量检查（TypeScript、ESLint、Tests）  
✅ 自动构建（Web、iOS、Android）  
✅ 自动部署（Vercel Web 应用）  
✅ 自动发布（GitHub Releases）  
✅ 构建通知（Telegram）

---

## 📋 工作流配置

### `.github/workflows/build.yml`

**触发条件：**
- 推送到 `main` 分支
- 创建版本标签（`v*`）
- Pull Request 到 `main` 分支
- 手动触发（Workflow Dispatch）

**工作流步骤：**

```yaml
quality          # 代码质量检查
  ├── build-web  # Web 构建
  ├── build-ios  # iOS 构建（可选）
  └── build-android # Android 构建（可选）
       ├── deploy-web # Web 部署到 Vercel
       ├── release     # 创建 GitHub Release
       └── notify      # 发送通知
```

---

## 🔧 必需的 GitHub Secrets

在仓库设置中添加以下 Secrets（**Settings → Secrets and variables → Actions**）：

### 必需

| Secret | 说明 | 获取方式 |
|--------|------|--------|
| `EXPO_TOKEN` | Expo CLI 认证令牌 | `eas login` 后查看 `~/.expo/credentials.json` |

### 可选（Web 部署）

| Secret | 说明 | 获取方式 |
|--------|------|--------|
| `VERCEL_TOKEN` | Vercel API 令牌 | [Vercel Settings](https://vercel.com/account/tokens) |
| `VERCEL_ORG_ID` | Vercel 组织 ID | 项目设置中查看 |
| `VERCEL_PROJECT_ID` | Vercel 项目 ID | 项目设置中查看 |

### 可选（Telegram 通知）

| Secret | 说明 | 获取方式 |
|--------|------|--------|
| `TELEGRAM_BOT_TOKEN` | Telegram Bot Token | [@BotFather](https://t.me/botfather) |
| `TELEGRAM_CHAT_ID` | Telegram 群组/频道 ID | 向机器人发送消息后从 updates 中查看 |

---

## 📝 设置步骤

### 1. 配置 Expo Token

```bash
# 本地登录 Expo
npm install -g eas-cli
eas login

# 查看 credentials
cat ~/.expo/credentials.json | grep accessToken
```

复制 `accessToken` 值，在 GitHub 仓库中添加 Secret `EXPO_TOKEN`。

### 2. 配置 Vercel（可选）

```bash
# 如果要自动部署 Web 到 Vercel
# 1. 在 Vercel 中创建项目
# 2. 获取 Token、Org ID、Project ID
# 3. 在 GitHub Secrets 中添加
```

### 3. 配置 Telegram 通知（可选）

```bash
# 1. 在 Telegram 中创建 Bot（@BotFather）
# 2. 创建或获取 Chat ID
# 3. 在 GitHub Secrets 中添加 TELEGRAM_BOT_TOKEN 和 TELEGRAM_CHAT_ID
```

---

## 🎯 使用指南

### 自动执行（推送到 main）

```bash
git push origin main
```

✅ 自动执行：质量检查 → Web 构建 → 可选部署

### 手动触发构建

进入 **Actions → Build and Release → Run workflow**

选择需要构建的平台：
- ✅ Build Web（默认）
- ✅ Build iOS
- ✅ Build Android

### 发布版本

```bash
# 1. 更新版本号（package.json 和 app.config.ts）
vim package.json
vim app.config.ts

# 2. 提交更改
git add .
git commit -m "chore: bump version to 1.3.0"

# 3. 创建标签并推送
git tag v1.3.0
git push origin main --tags
```

✅ 自动执行：完整构建 → 创建 Release

---

## 📦 构建输出

### Web 构建

- **输出目录：** `dist/`
- **工件保留时间：** 90 天
- **部署目标：** Vercel（如配置）

### iOS 构建

- **方式：** EAS Build
- **输出：** `.ipa` 文件
- **部署目标：** App Store Connect（手动）

### Android 构建

- **方式：** EAS Build
- **输出：** `.apk` 或 `.aab` 文件
- **部署目标：** Google Play（手动）

---

## 🔍 监控和调试

### 查看工作流日志

1. 进入 **Actions** 标签
2. 选择相应的工作流
3. 点击运行查看详细日志

### 常见问题

#### ❌ "Expo token is invalid"

```bash
# 重新获取 token
eas logout
eas login
cat ~/.expo/credentials.json | grep accessToken
```

然后更新 GitHub Secret `EXPO_TOKEN`。

#### ❌ "Build failed: Out of memory"

这是 GitHub Actions 的环保机制。解决方案：
- 本地构建并手动上传
- 或使用 EAS 的更高级计划

#### ❌ "Permission denied for deployment"

检查 Vercel/App Store Connect 凭证和权限。

---

## 📊 工作流状态

在 README.md 中添加状态徽章：

```markdown
[![Build and Release](https://github.com/xiaoluoshen/GreenWall-Mobile/actions/workflows/build.yml/badge.svg)](https://github.com/xiaoluoshen/GreenWall-Mobile/actions/workflows/build.yml)
```

---

## 🎨 自定义工作流

### 修改触发条件

编辑 `.github/workflows/build.yml`：

```yaml
on:
  push:
    branches:
      - main
    tags:
      - 'v*'
  schedule:
    - cron: '0 0 * * 0'  # 每周日自动构建
```

### 跳过某个工作流

在提交信息中包含关键词：

```bash
git commit -m "docs: update README [skip ci]"
```

### 条件构建

```yaml
if: github.event_name == 'push' && startsWith(github.ref, 'refs/tags/v')
```

---

## 📚 参考资源

- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Expo EAS Build](https://docs.expo.dev/build/introduction/)
- [Vercel GitHub Integration](https://vercel.com/docs/git)
- [softprops/action-gh-release](https://github.com/softprops/action-gh-release)

---

## ✅ 检查清单

- [ ] 配置 `EXPO_TOKEN` Secret
- [ ] （可选）配置 Vercel Secrets
- [ ] （可选）配置 Telegram 通知 Secrets
- [ ] 本地测试 `pnpm build`
- [ ] 创建第一个版本标签 `v1.0.0`
- [ ] 验证 GitHub Actions 执行成功

---

## 🎯 下一步

1. **本地测试**：运行 `pnpm build` 确保构建成功
2. **第一次推送**：推送到 `main` 分支，验证工作流
3. **配置部署**：根据需要配置 Vercel 或 App Store Connect
4. **版本发布**：创建标签并推送，测试发布流程

祝构建顺利！🚀
