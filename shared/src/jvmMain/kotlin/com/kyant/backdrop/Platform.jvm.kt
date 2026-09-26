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

package com.kyant.backdrop

import androidx.compose.ui.graphics.Shader

/*
 * 本项目内置的 Liquid Glass 只面向 Android：桌面端的 Compose 渲染后端没有等价的
 * RenderEffect 与 AGSL 能力。这里全部 fail-fast 而不是静默 no-op，
 * 以免错误的平台接线被当成正常渲染喵。上游调用这些入口前都会先检查能力声明喵。
 */

actual fun isRenderEffectSupported(): Boolean = false

actual fun isRuntimeShaderSupported(): Boolean = false

actual fun RuntimeShader(shaderString: String): RuntimeShader {
    error("桌面端不支持 Liquid Glass 运行时着色器")
}

actual fun RuntimeShader.asComposeShader(): Shader {
    error("桌面端不支持 Liquid Glass 运行时着色器")
}
