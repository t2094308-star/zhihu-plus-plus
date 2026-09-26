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

package com.github.zly2006.zhihu.ui.article

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import com.github.zly2006.zhihu.ui.ArticleAnswerTransitionDirection

/**
 * 最近一次从列表打开回答时，来源卡片的中心位置（相对屏幕 0..1）喵。
 *
 * 由卡片在点击时写入，回答页的转场读它作为"从卡片扩散"的缩放原点；
 * 为 null 时退回中心缩放，例如从通知、外链等没有卡片来源的入口喵。
 */
var cardOpenOriginFraction: Offset? = null

/** 从列表打开回答时的上浮扩散转场开关喵。 */
const val ARTICLE_OPEN_TRANSITION_ENABLED_PREFERENCE_KEY = "articleOpenTransition"
const val DEFAULT_ARTICLE_OPEN_TRANSITION_ENABLED = true

/** 上浮扩散转场的速度倍率；数值越大越快喵。 */
const val ARTICLE_OPEN_TRANSITION_SPEED_PREFERENCE_KEY = "articleOpenTransitionSpeed"
const val DEFAULT_ARTICLE_OPEN_TRANSITION_SPEED = 1f
const val MIN_ARTICLE_OPEN_TRANSITION_SPEED = 0.5f
const val MAX_ARTICLE_OPEN_TRANSITION_SPEED = 2f

/** 1.0x 对应的动画时长喵。 */
private const val BASE_ARTICLE_OPEN_TRANSITION_MILLIS = 820

/** 回答之间切换沿用既有时长，不受上浮转场的速度设置影响喵。 */
private const val ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS = 300

/** 高倍率下的时长下限，避免动画短到看不见喵。 */
private const val MIN_ARTICLE_OPEN_TRANSITION_MILLIS = 80

/** NaN 与越界一律夹到合法区间，与 `normalizedAnswerSwitchSensitivity` 同风格喵。 */
internal fun normalizedArticleOpenTransitionSpeed(value: Float): Float = when {
    value.isNaN() -> DEFAULT_ARTICLE_OPEN_TRANSITION_SPEED
    else -> value.coerceIn(MIN_ARTICLE_OPEN_TRANSITION_SPEED, MAX_ARTICLE_OPEN_TRANSITION_SPEED)
}

/** 把速度倍率换算成动画时长喵。 */
fun articleOpenTransitionDurationMillis(speed: Float): Int =
    (BASE_ARTICLE_OPEN_TRANSITION_MILLIS / normalizedArticleOpenTransitionSpeed(speed))
        .toInt()
        .coerceAtLeast(MIN_ARTICLE_OPEN_TRANSITION_MILLIS)

/**
 * 回答页的进入转场喵。
 *
 * 回答之间的切换沿用原来的方向滑入；从列表或主壳打开回答时，上浮扩散由页面根节点自己播，
 * 所以这里返回 [EnterTransition.None] 避免叠加；开关关闭时逐字回到原来的横向滑入喵。
 */
fun resolveArticleEnterTransition(
    direction: ArticleAnswerTransitionDirection,
    openTransitionEnabled: Boolean,
    openTransitionMillis: Int,
): EnterTransition = when (direction) {
    ArticleAnswerTransitionDirection.VERTICAL_NEXT ->
        slideInVertically(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { it } +
            fadeIn(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS))

    ArticleAnswerTransitionDirection.VERTICAL_PREVIOUS ->
        slideInVertically(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { -it } +
            fadeIn(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS))

    ArticleAnswerTransitionDirection.HORIZONTAL_NEXT ->
        slideInHorizontally(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { it } +
            fadeIn(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS))

    ArticleAnswerTransitionDirection.HORIZONTAL_PREVIOUS ->
        slideInHorizontally(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { -it } +
            fadeIn(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS))

    ArticleAnswerTransitionDirection.DEFAULT ->
        if (openTransitionEnabled) {
            // 用缩放做"扩散"：纯变换、不需要离屏层，
            // 同时保留可动画的转场——预测返回的预览依赖它喵。
            scaleIn(
                tween(openTransitionMillis),
                initialScale = 0.86f,
                // 从卡片位置扩散；没有来源时退回中心喵。
                transformOrigin = cardOpenOriginFraction
                    ?.let { TransformOrigin(it.x, it.y) }
                    ?: TransformOrigin.Center,
            ) +
                slideInVertically(tween(openTransitionMillis)) { it / 8 }
        } else {
            slideInHorizontally(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { it }
        }
}

/** 回答页的退出转场；回答间切换按方向滑出，从列表打开回答时沿用原来的无转场喵。 */
fun resolveArticleExitTransition(
    direction: ArticleAnswerTransitionDirection,
): ExitTransition = when (direction) {
    ArticleAnswerTransitionDirection.VERTICAL_NEXT ->
        slideOutVertically(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { -it } +
            fadeOut(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS))

    ArticleAnswerTransitionDirection.VERTICAL_PREVIOUS ->
        slideOutVertically(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { it } +
            fadeOut(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS))

    ArticleAnswerTransitionDirection.HORIZONTAL_NEXT ->
        slideOutHorizontally(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { -it } +
            fadeOut(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS))

    ArticleAnswerTransitionDirection.HORIZONTAL_PREVIOUS ->
        slideOutHorizontally(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { it } +
            fadeOut(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS))

    ArticleAnswerTransitionDirection.DEFAULT ->
        // 返回时反向缩回卡片位置，与进入时的展开对称，落实"从哪来到哪去"喵。
        cardOpenOriginFraction?.let { origin ->
            scaleOut(
                tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS),
                targetScale = 0.86f,
                transformOrigin = TransformOrigin(origin.x, origin.y),
            )
        } ?: ExitTransition.None
}
