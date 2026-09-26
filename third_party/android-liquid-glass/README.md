# Android Liquid Glass（Kyant0/AndroidLiquidGlass）内置副本

来源：https://github.com/Kyant0/AndroidLiquidGlass
版本：2.0.1，commit `65ab177`
授权：Apache-2.0，见同目录 `LICENSE`。

## 内置范围

上游仓库是 Kotlin Multiplatform 结构（`backdrop/` 库模块 + `app/` 示例模块）。本项目只内置 Android 玻璃底栏真正需要的部分：

| 上游路径 | 本目录路径 | 说明 |
| --- | --- | --- |
| `backdrop/src/commonMain/kotlin/com/kyant/backdrop/` | `src/commonMain/kotlin/com/kyant/backdrop/` | 库核心：特效 DSL、渲染节点、AGSL 着色器 |
| `backdrop/src/androidMain/kotlin/com/kyant/backdrop/` | `src/androidMain/kotlin/com/kyant/backdrop/` | Android 实现：`RenderEffect`、`RuntimeShader`、`Paint` |
| `app/src/commonMain/.../catalog/components/LiquidBottomTab.kt`、`LiquidBottomTabs.kt` | `src/catalog/...` | 官方底栏组件，本项目的接入目标 |
| `app/src/commonMain/.../catalog/utils/DampedDragAnimation.kt`、`InteractiveHighlight.kt` | `src/catalog/...` | 底栏直接依赖的两个工具类 |

未内置：`backdrop/src/skikoMain/`（桌面与 Apple 平台实现）、`catalog` 的其余组件（`LiquidButton`、`LiquidSlider`、`LiquidToggle`）及其工具类、示例 `app` 的页面代码。本项目只做 Android 玻璃底栏，不为此扩大内置面。

## 依赖

`com.kyant.shapes.Capsule` 来自独立库 `io.github.kyant0:shapes`（不在上游仓库源码内），因此继续以 Maven 依赖引入。它提供的是连续曲率的类 squircle 轮廓，不等于 `RoundedCornerShape`，是底栏默认外观的一部分。

## 本项目追加的适配文件

以下文件不是上游源码，是本项目为了在 KMP 结构里编译和运行而写的适配，放在应用自己的源码树下（不在本目录），修改本项目时不要当成上游内容：

- `shared/src/jvmMain/kotlin/com/kyant/backdrop/`、`shared/src/nativeMain/kotlin/com/kyant/backdrop/`：桌面与 Apple 目标不实现玻璃渲染，因此提供 `error(...)` 的 fail-fast `actual`，而不是静默 no-op。放在 `shared/src` 下是因为本目录被排除在项目 ktlint 之外，而这些适配文件应当接受项目的格式检查。

## 本项目对上游源码的修改

`src/catalog/com/kyant/backdrop/catalog/components/LiquidBottomTabs.kt`：把 3 处 `drawBackdrop` 里原本硬编码的绘制参数提升为带默认值的参数，供设置页调节喵。默认值逐项等于上游原本的字面量，因此默认档外观与上游一致喵。

| 参数 | 上游原值 | 默认值 |
| --- | --- | --- |
| `highlightAlpha`（轮廓光，作为按压进度的乘数） | `Highlight.Default.copy(alpha = progress)` | `1f` |
| `blurRadius`（模糊半径） | `blur(8f.dp.toPx())` | `8f.dp` |
| `cornerRadius`（圆角） | `shape = { Capsule() }` | `32f.dp`（≥32dp 时仍用上游的 `Capsule()`） |
| `refractionRadius`（折射半径） | `lens(24f.dp.toPx(), 24f.dp.toPx())` | `24f.dp` |
| `surfaceAlpha`（表面透明度） | `containerColor` 的 `copy(0.4f)` | `0.4f` |

圆角用分段映射而不是直接替换：上游的 `Capsule()` 来自 `com.kyant.shapes`，是连续曲率的类 squircle 轮廓，与 `RoundedCornerShape` 观感不同；只有用户把圆角调小到失去胶囊形态时才切换到圆角矩形，从而保证默认档与上游逐字等价喵。

另有 `src/catalog/com/kyant/backdrop/catalog/utils/Coroutines.kt`：上游在示例 app 模块用 expect/actual 声明 `awaitFrame`，本项目只把 catalog 组件接入 Android，所以这里直接给出 Android 实现喵。

