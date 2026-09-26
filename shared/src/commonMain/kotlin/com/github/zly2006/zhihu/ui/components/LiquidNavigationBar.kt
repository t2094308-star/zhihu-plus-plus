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
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.zly2006.zhihu.theme.LiquidGlassSettings

/**
 * 玻璃底栏的一项喵。
 *
 * 标题、图标、选中态和点击动作都由调用侧投影进来，组件内部不写死任何产品目的地喵。
 */
class LiquidNavigationItem(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean,
    val tag: String,
    val onClick: () -> Unit,
)

@Composable
expect fun LiquidNavigationBar(
    items: List<LiquidNavigationItem>,
    settings: LiquidGlassSettings,
)
