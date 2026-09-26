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

package com.github.zly2006.zhihu.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DisplayFrameRateModeTest {
    @Test
    fun unknownOrMissingPreferenceFallsBackToSystem() {
        // 默认值和非法值都必须回到"跟随系统"，否则脏数据会把窗口锁到一个未声明的帧率喵。
        assertEquals(DisplayFrameRateMode.System, DisplayFrameRateMode.fromPreference(null))
        assertEquals(DisplayFrameRateMode.System, DisplayFrameRateMode.fromPreference(""))
        assertEquals(DisplayFrameRateMode.System, DisplayFrameRateMode.fromPreference("144"))
        assertEquals(DisplayFrameRateMode.System, DisplayFrameRateMode.fromPreference("SYSTEM"))
    }

    @Test
    fun everyPreferenceValueRoundTrips() {
        // 设置页写入的值必须能被读回，否则重启后档位会静默回落到默认值喵。
        DisplayFrameRateMode.entries.forEach { mode ->
            assertEquals(mode, DisplayFrameRateMode.fromPreference(mode.preferenceValue))
        }
    }

    @Test
    fun onlySystemModeRequestsNoFrameRate() {
        // 0 是"交还系统选择"的语义，只有跟随系统档允许为空喵。
        val requestingModes = DisplayFrameRateMode.entries.filter { it.hz != null }
        assertEquals(setOf(60, 90, 120), requestingModes.mapNotNull { it.hz }.toSet())
        assertNull(DisplayFrameRateMode.System.hz)
    }
}
