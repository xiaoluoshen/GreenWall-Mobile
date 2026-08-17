# GreenWall Mobile

**GreenWall Mobile** 是一款原生 Android 应用，用于绘制 GitHub 风格的年度贡献热力图，并将结果同步为 GitHub 仓库提交。项目现已完全采用 **Kotlin** 与 **Jetpack Compose** 重构，不再依赖 React Native、Expo 或 JavaScript 运行时。

## 功能

应用提供年度贡献画布，支持点击或拖动绘制、四档贡献强度、橡皮擦、全绿填充、重置以及撤销和重做。字符页面保留大小写字母、数字和常用符号的点阵图案，可居中应用到当前画布。所有贡献数据按年份保存于本机 DataStore 中；GitHub 令牌使用 Android 加密偏好存储保存。

| 模块 | 实现 |
|---|---|
| 界面与导航 | Kotlin、Jetpack Compose、Material 3、Navigation Compose |
| 贡献数据 | 纯 Kotlin 领域模型、撤销重做状态、DataStore Preferences |
| GitHub 同步 | GitHub REST API、令牌验证、仓库创建、贡献提交生成 |
| 安全性 | EncryptedSharedPreferences 加密保存 GitHub 令牌 |
| 兼容性 | Android 7.0（API 24）及以上 |

> GitHub 同步需要具有仓库读写权限的个人访问令牌。令牌仅加密保存在安装该应用的设备中，不会上传至任何第三方服务。

## 构建

项目使用 Gradle Wrapper。请安装 Android SDK Platform 35、Build Tools 35.0.0 和 Java 17 JDK，然后在仓库根目录运行：

```bash
printf 'sdk.dir=/path/to/android-sdk\n' > local.properties
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

生成的调试安装包位于：

```text
app/build/outputs/apk/debug/app-debug.apk
```

如需构建发布版本，请配置自己的签名证书并运行：

```bash
./gradlew :app:assembleRelease
```

## 代码结构

```text
app/
├── src/main/java/com/xiaoluoshen/greenwall/mobile/
│   ├── data/       # DataStore 与加密会话存储
│   ├── domain/     # 贡献日历、字符图案与纯业务规则
│   ├── github/     # GitHub REST API 服务
│   └── ui/         # Compose 页面、ViewModel 与 Material 3 主题
└── src/test/       # Kotlin 单元测试
```

## 许可与致谢

本项目基于 [GreenWall](https://github.com/zmrlft/GreenWall) 的功能理念进行移动端原生实现。请在使用 GitHub API 时遵守 GitHub 的服务条款与访问令牌权限原则。
