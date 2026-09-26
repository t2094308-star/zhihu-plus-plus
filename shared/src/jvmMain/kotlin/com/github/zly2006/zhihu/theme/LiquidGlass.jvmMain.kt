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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

// 桌面端没有与之等价的渲染能力，直接声明不支持，让设置项与各处 chrome 回落到原有样式喵。
actual val isLiquidGlassSupported: Boolean = false

@Composable
actual fun rememberGlassBackdrop(): GlassBackdrop {
    error("桌面端不支持液态玻璃渲染")
}

@Composable
actual fun rememberGlassSurface(
    backdrop: GlassBackdrop?,
    shape: Shape,
    settings: LiquidGlassSettings,
    tint: Color?,
): Modifier {
    // 调用方在 isLiquidGlassSupported 为假时不会给出 backdrop，所以这里只会在接线错误时失败喵。
    if (backdrop == null) return Modifier
    error("桌面端不支持液态玻璃渲染")
}
