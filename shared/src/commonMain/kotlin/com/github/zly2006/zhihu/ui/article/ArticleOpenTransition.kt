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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import com.github.zly2006.zhihu.ui.ArticleAnswerTransitionDirection

/**
 * 最近一次从列表打开回答时，来源卡片的几何信息（相对屏幕 0..1）喵。
 *
 * 回答页据此把整页从"卡片那块扁长方形"铺满全屏：宽高分别取卡片自身的宽高比例，
 * 配合以卡片中心为原点的非等比缩放，观感上就是卡片从原地向上下沿长开喵。
 * 为 null 表示入口没有卡片来源（通知、外链等），退回不做展开动画喵。
 */
data class CardOpenGeometry(
    /** 卡片中心的纵向位置 0..1，展开时用来定位起始的裁切窗口喵。 */
    val centerY: Float,
    /** 卡片高度占屏幕高度的比例 0..1，决定起始裁切窗口的高度喵。 */
    val heightFraction: Float,
)

var cardOpenGeometry: CardOpenGeometry? = null

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
 * 从列表卡片展开到全屏的容器喵。
 *
 * 回答页和想法页共用：点击卡片后新页面从卡片那块区域"长"开成全屏喵。
 * 用上下两条"裁切线"展开，而不是缩放页面：起始只露出卡片所在高度的那一条，
 * 上裁切线一路移到屏幕顶端、下裁切线移到屏幕底端，页面内容本身尺寸和位置都不变喵。
 * 所以文字不会被压扁，也不会因为图层尺寸每帧变化而反复重新光栅化喵
 * （2026-10-01 实测：用 graphicsLayer 非等比缩放时文字被压扁，且 1260x357 的
 * hardware layer 每帧换尺寸、整页重新光栅化导致卡顿）喵。
 *
 * 另外特意等第一帧渲染后才启动动画：新页面首次组合要几十毫秒，
 * 若从转场开始就计时，开头那段会被组合耗时吃掉喵。
 */
@Composable
fun CardOpenTransition(
    enabled: Boolean,
    durationMillis: Int,
    exiting: Boolean = false,
    content: @Composable () -> Unit,
) {
    // 进入时把来源卡片固定下来，之后导航再改它也不影响本次动画喵。
    val geometry = remember { cardOpenGeometry }
    val shouldAnimate = enabled && geometry != null
    val progress = remember { Animatable(if (shouldAnimate) 0f else 1f) }
    LaunchedEffect(shouldAnimate, durationMillis) {
        if (shouldAnimate) {
            // 等到第一帧真正画出来再起跑喵。
            withFrameNanos { }
            progress.animateTo(1f, tween(durationMillis))
        }
    }
    LaunchedEffect(exiting) {
        // 退出就是反向播一遍：下裁切线先上移、上裁切线再下移，收成卡片那块区域喵。
        // 导航层的退出动画只负责把页面留到这时候，本身不做可见变化喵。
        if (exiting && shouldAnimate && progress.value > 0f) {
            progress.animateTo(0f, tween(durationMillis))
        }
    }
    // 在组合阶段读进度，确保它变化时一定会走重组→重绘；
    // 只在 drawWithContent 里读的话依赖登记不到绘制阶段，动画会退化成几帧跳变喵。
    val revealProgress = progress.value
    Box(
        modifier = Modifier.drawWithContent {
            val card = geometry
            if (card == null) {
                drawContent()
                return@drawWithContent
            }
            val fullHeight = size.height
            val cardTop = (card.centerY - card.heightFraction / 2f) * fullHeight
            val cardBottom = (card.centerY + card.heightFraction / 2f) * fullHeight
            val p = revealProgress
            // 上裁切线往屏幕顶端移、下裁切线往屏幕底端移喵。
            val revealTop = cardTop * (1f - p)
            val revealBottom = cardBottom + (fullHeight - cardBottom) * p
            clipRect(top = revealTop, bottom = revealBottom) {
                // 内容原地绘制、不做位移：只让上下裁切线移动喵。
                // 早期版本在这里把内容整体上移过（想让窗口露出页面顶部），
                // 结果是窗口展开的同时内容在往下滑，看起来像"从上往下落"喵。
                this@drawWithContent.drawContent()
            }
        },
    ) {
        content()
    }
}

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
            // 展开动画由回答页根节点用 graphicsLayer 自己播（见 ArticleCardOpenTransition）喵。
            // 那边是非等比纯绘制变换、不触发布局重排；这里必须返回 None，
            // 否则会和页面内的变换叠加（2026-10-01 试过 nav 层尺寸动画：从顶部展开且逐帧重排掉帧）喵。
            EnterTransition.None
        } else {
            slideInHorizontally(tween(ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS)) { it }
        }
}

/** 回答页的退出转场；回答间切换按方向滑出，从列表打开回答时沿用原来的无转场喵。 */
fun resolveArticleExitTransition(
    direction: ArticleAnswerTransitionDirection,
    openTransitionMillis: Int = ARTICLE_ANSWER_SWITCH_TRANSITION_MILLIS,
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
        // 退出的反向裁切由页面自己播（见 CardOpenTransition 的 exiting）喵。
        // 这里只负责把页面留到那时候播完：targetScale 特意取 0.999f 而不是 1f，
        // 因为目标值和起始值相同时 Compose 会判定动画无需执行、立即完成，
        // 页面会被瞬间销毁，反向裁切根本来不及播喵。
        cardOpenGeometry?.let {
            scaleOut(
                tween(openTransitionMillis),
                targetScale = 0.999f,
            )
        } ?: ExitTransition.None
}
