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

import android.content.res.Configuration
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.zly2006.zhihu.theme.AndroidGlassBackdrop
import com.github.zly2006.zhihu.theme.LiquidGlassSettings
import com.github.zly2006.zhihu.theme.LocalGlassBackdrop
import com.github.zly2006.zhihu.theme.ThemeManager
import com.kyant.backdrop.catalog.components.LiquidBottomTab
import com.kyant.backdrop.catalog.components.LiquidBottomTabs

@Composable
actual fun LiquidNavigationBar(
    items: List<LiquidNavigationItem>,
    settings: LiquidGlassSettings,
) {
    if (items.isEmpty()) return
    val backdrop = checkNotNull(LocalGlassBackdrop.current as? AndroidGlassBackdrop).nativeBackdrop
    val currentItems by rememberUpdatedState(items)
    val selectedIndex by rememberUpdatedState(items.indexOfFirst { it.selected }.coerceAtLeast(0))
    val readSelection = remember { { selectedIndex } }
    val onDragSelection = remember {
        { index: Int ->
            // 上游也会回报程序驱动的选中变化，不能把它转成再次点击当前页喵。
            currentItems
                .getOrNull(index)
                ?.takeUnless { it.selected }
                ?.onClick
                ?.invoke()
            Unit
        }
    }
    OfficialLiquidAppearance {
        LiquidBottomTabs(
            selectedTabIndex = readSelection,
            onTabSelected = onDragSelection,
            backdrop = backdrop,
            tabsCount = items.size,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(
                    top = 12.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 8.dp,
                ),
            highlightAlpha = settings.highlightAlpha,
            blurRadius = settings.blurDp.dp,
            cornerRadius = settings.cornerDp.dp,
            refractionRadius = settings.refractionDp.dp,
            surfaceAlpha = settings.surfaceAlpha,
        ) {
            items.forEach { item ->
                LiquidBottomTab(
                    onClick = item.onClick,
                    modifier = Modifier.testTag(item.tag).semantics { selected = item.selected },
                ) {
                    Icon(
                        item.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(26.dp),
                    )
                    Text(
                        item.label,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/** 官方组件读取系统明暗；仅为这一子树传入应用选定的外观，不修改设备配置喵。 */
@Composable
private fun OfficialLiquidAppearance(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val dark = ThemeManager.isDarkTheme()
    val appearance = remember(configuration, dark) {
        Configuration(configuration).apply {
            uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                (if (dark) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO)
        }
    }
    CompositionLocalProvider(LocalConfiguration provides appearance, content = content)
}
