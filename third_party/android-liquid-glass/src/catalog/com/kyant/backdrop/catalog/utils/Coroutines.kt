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

package com.kyant.backdrop.catalog.utils

import kotlinx.coroutines.android.awaitFrame as awaitPlatformFrame

/*
 * 上游在示例 app 模块里用 expect/actual 声明 awaitFrame，本项目只把 catalog 组件接入 Android，
 * 所以这里直接给出 Android 实现，不再保留 expect/actual 两套文件喵。
 */

/** 等待下一帧，供阻尼动画按帧推进喵。 */
suspend fun awaitFrame() = awaitPlatformFrame()
