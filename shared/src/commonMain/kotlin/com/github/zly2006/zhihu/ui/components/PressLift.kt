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

package com.github.zly2006.zhihu.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 按下时微微上浮、并可放大的触感反馈喵。
 *
 * 只在 `PointerEventPass.Initial` 观察指针、不消费事件，所以可以安全地叠在任何 `clickable` 之上，
 * 不会影响原有的点击、长按和滚动手势喵。位移与缩放都在 layer 阶段应用，不触发重组喵。
 *
 * [pressedScale] 传大于 1 的值即可得到"按住变大"的手感，与底栏导航项一致喵。
 */
fun Modifier.pressLift(
    lift: Dp = 5.dp,
    pressedScale: Float = 1f,
    pressedColor: Color? = null,
    pressedGlow: Color? = null,
): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    val offset by animateDpAsState(
        targetValue = if (pressed) -lift else 0.dp,
        animationSpec = tween(120),
        label = "pressLift",
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = tween(140),
        label = "pressScale",
    )
    this
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val isDown = event.changes.any { it.pressed }
                    if (isDown != pressed) {
                        pressed = isDown
                    }
                }
            }
        }.drawBehind {
            // 按下时在中心叠一圈径向光晕，模仿底栏导航项的按下高光喵。
            if (pressed && pressedGlow != null) {
                val glowRadius = size.maxDimension * 0.75f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(pressedGlow.copy(alpha = 0.45f), Color.Transparent),
                        center = center,
                        radius = glowRadius,
                    ),
                    radius = glowRadius,
                    center = center,
                )
            }
        }.graphicsLayer {
            // 按住时把整个按钮内容染成主题色：比逐个按钮接色更省事，也不会漏掉图标与文字喵。
            colorFilter = if (pressed && pressedColor != null) {
                ColorFilter.tint(pressedColor, BlendMode.SrcIn)
            } else {
                null
            }
            translationY = offset.toPx()
            scaleX = scale
            scaleY = scale
        }
}
