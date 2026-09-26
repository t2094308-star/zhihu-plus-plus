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

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight

// 折射与轮廓光依赖 AGSL（RuntimeShader），Android 13 起才可用；
// 更早的系统只声明不支持，不画一个假的玻璃外观喵。
actual val isLiquidGlassSupported: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

/** 官方组件需要拿到 Compose 的 backdrop 对象，这里把它藏在实现里喵。 */
internal interface AndroidGlassBackdrop : GlassBackdrop {
    val nativeBackdrop: LayerBackdrop
}

@Composable
actual fun rememberGlassBackdrop(): GlassBackdrop {
    val backdrop = rememberLayerBackdrop()
    return remember(backdrop) {
        object : AndroidGlassBackdrop {
            override val nativeBackdrop = backdrop
            override val source = Modifier.layerBackdrop(backdrop)
        }
    }
}

@Composable
actual fun rememberGlassSurface(
    backdrop: GlassBackdrop?,
    shape: Shape,
    settings: LiquidGlassSettings,
    tint: Color?,
): Modifier {
    val nativeBackdrop = (backdrop as? AndroidGlassBackdrop)?.nativeBackdrop
    // 与官方底栏取同一组容器色，只把透明度交给设置喵。
    val surfaceColor = tint ?: if (ThemeManager.isDarkTheme()) {
        Color(0xFF121212).copy(settings.surfaceAlpha)
    } else {
        Color(0xFFFAFAFA).copy(settings.surfaceAlpha)
    }
    return remember(nativeBackdrop, shape, settings, surfaceColor) {
        if (nativeBackdrop == null) {
            Modifier
        } else {
            Modifier.drawBackdrop(
                backdrop = nativeBackdrop,
                shape = { shape },
                effects = {
                    vibrancy()
                    blur(settings.blurDp.dp.toPx())
                    lens(settings.refractionDp.dp.toPx(), settings.refractionDp.dp.toPx())
                },
                highlight = { Highlight.Default.copy(alpha = settings.highlightAlpha) },
                onDrawSurface = { drawRect(surfaceColor) },
            )
        }
    }
}
