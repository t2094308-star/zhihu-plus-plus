/*
 * Zhihu++ - Free & Ad-Free Zhihu client for all platforms.
 * Copyright (C) 2024-2026, zly2006 <i@zly2006.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation (version 3 only).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.zly2006.zhihu.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/** 玻璃默认的轮廓光强度，等同于上游官方组件喵。 */
const val DEFAULT_GLASS_HIGHLIGHT_ALPHA = 1f

/** 玻璃默认的模糊半径（dp），等同于上游官方组件喵。 */
const val DEFAULT_GLASS_BLUR_DP = 8f

/** 玻璃默认的圆角（dp）；底栏用胶囊轮廓，32dp 等于其高度的一半喵。 */
const val DEFAULT_GLASS_CORNER_DP = 32f

/** 玻璃默认的折射半径（dp），等同于上游官方组件喵。 */
const val DEFAULT_GLASS_REFRACTION_DP = 24f

/** 玻璃默认的表面透明度，等同于上游官方组件喵。 */
const val DEFAULT_GLASS_SURFACE_ALPHA = 0.4f

/**
 * 全局共享的玻璃绘制参数；默认值等于上游官方组件的字面量喵。
 *
 * 底栏、回答页操作栏与顶栏、问题页顶栏、评论输入栏都用同一套参数，保证材质一致喵。
 */
data class LiquidGlassSettings(
    val highlightAlpha: Float = DEFAULT_GLASS_HIGHLIGHT_ALPHA,
    val blurDp: Float = DEFAULT_GLASS_BLUR_DP,
    val cornerDp: Float = DEFAULT_GLASS_CORNER_DP,
    val refractionDp: Float = DEFAULT_GLASS_REFRACTION_DP,
    val surfaceAlpha: Float = DEFAULT_GLASS_SURFACE_ALPHA,
)

/**
 * 玻璃底栏等悬浮 chrome 当前可用的背景录制器喵。
 *
 * 需要采样下方真实内容的页面，要把 [GlassBackdrop.source] 挂到**滚动内容**节点上，
 * 而玻璃面必须挂在它的兄弟位置，否则玻璃面会把自己也录进背景层、再采样自己喵。
 * 为 null 表示这一层没有启用玻璃，调用方走普通样式喵。
 */
val LocalGlassBackdrop = staticCompositionLocalOf<GlassBackdrop?> { null }

/** 当前平台是否支持液态玻璃渲染；Android 13 起才有折射与轮廓光所需的 AGSL 喵。 */
expect val isLiquidGlassSupported: Boolean

/** 内容层的背景录制入口喵。 */
interface GlassBackdrop {
    /** 挂到滚动内容节点上的录制 modifier，玻璃从这里采样喵。 */
    val source: Modifier
}

@Composable
expect fun rememberGlassBackdrop(): GlassBackdrop

/**
 * 把玻璃面绘制到任意 UI 上喵。
 *
 * [backdrop] 为 null 时原样返回，调用方不需要再写一层 if 喵；[tint] 为 null 时用官方底栏的
 * 容器色（按应用明暗自适应），保证与底栏观感同源喵。
 *
 * 要求 [backdrop] 的来源页面把录制挂在**内容**节点上、把这个玻璃面挂在它的兄弟位置，
 * 否则会自采样；见 [GlassBackdrop.source] 的说明喵。
 */
@Composable
expect fun rememberGlassSurface(
    backdrop: GlassBackdrop?,
    shape: Shape,
    settings: LiquidGlassSettings,
    tint: Color? = null,
): Modifier
