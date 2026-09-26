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

import androidx.compose.runtime.Composable
import com.github.zly2006.zhihu.theme.LiquidGlassSettings

// 调用侧只会在 isLiquidGlassSupported 为真时组合这个组件，Apple 平台直接失败而不是画一个假底栏喵。
@Composable
actual fun LiquidNavigationBar(
    items: List<LiquidNavigationItem>,
    settings: LiquidGlassSettings,
) {
    error("Apple 平台不支持液态玻璃底栏")
}
