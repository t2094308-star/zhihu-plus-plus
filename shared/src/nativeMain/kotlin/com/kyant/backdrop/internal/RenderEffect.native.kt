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

// RuntimeShaderEffect 与 ColorFilterEffect 是上游 expect 声明的名字，actual 不能改名喵。
@file:Suppress("ktlint:standard:function-naming")

package com.kyant.backdrop.internal

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RenderEffect
import com.kyant.backdrop.RuntimeShader

/*
 * Apple 平台不实现玻璃渲染，这几个 actual 只为满足编译契约喵。
 * 上游在调用它们之前都会先检查 isRenderEffectSupported()/isRuntimeShaderSupported()，
 * 因此这里的 error 只在平台接线出错时才会触发喵。
 */

internal actual fun Paint.blur(radius: Float) {
    error("Apple 平台不支持 Liquid Glass 模糊效果")
}

internal actual fun Paint.setRuntimeShader(runtimeShader: RuntimeShader?) {
    error("Apple 平台不支持 Liquid Glass 运行时着色器")
}

internal actual fun RenderEffect?.chain(other: RenderEffect): RenderEffect {
    error("Apple 平台不支持 Liquid Glass 渲染效果链")
}

internal actual fun RuntimeShaderEffect(runtimeShader: RuntimeShader, uniformShaderName: String): RenderEffect {
    error("Apple 平台不支持 Liquid Glass 运行时着色器")
}

internal actual fun ColorFilterEffect(renderEffect: RenderEffect?, colorFilter: ColorFilter): RenderEffect {
    error("Apple 平台不支持 Liquid Glass 渲染效果链")
}
