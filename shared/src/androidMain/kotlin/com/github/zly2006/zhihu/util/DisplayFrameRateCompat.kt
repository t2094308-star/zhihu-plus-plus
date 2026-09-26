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

package com.github.zly2006.zhihu.util

import android.view.Window
import com.github.zly2006.zhihu.platform.DisplayFrameRateMode
import kotlin.math.abs

/**
 * 把窗口的首选刷新率设成 [mode] 对应的档位喵。
 *
 * 这里用 `WindowManager.LayoutParams.preferredRefreshRate` 而不是 `Window.setFrameRate`：
 * 后者在 compileSdk 37 的 android.jar 里已经不存在（只剩 `Surface.setFrameRate`），
 * 而 Compose 的渲染 surface 由宿主持有，应用侧拿不到稳定的 `Surface` 引用喵。
 * `preferredRefreshRate` 从 API 21 起就在，兼容本项目更低的 minSdk喵。
 *
 * 值必须命中该显示真正支持的刷新率，所以先在 `supportedModes` 里取最接近用户所选的一档；
 * 取不到时退回用户选择的值，让系统自行决定是否接受喵。`0f` 表示"无偏好"，用于跟随系统档喵。
 */
@Suppress("DEPRECATION")
fun Window.applyDisplayFrameRate(mode: DisplayFrameRateMode) {
    val targetHz = mode.hz
    val preferredHz = if (targetHz == null) {
        0f
    } else {
        windowManager
            ?.defaultDisplay
            ?.supportedModes
            ?.map { it.refreshRate }
            ?.filter { it > 0f }
            ?.minByOrNull { abs(it - targetHz) }
            ?: targetHz.toFloat()
    }
    attributes = attributes.apply { preferredRefreshRate = preferredHz }
}
