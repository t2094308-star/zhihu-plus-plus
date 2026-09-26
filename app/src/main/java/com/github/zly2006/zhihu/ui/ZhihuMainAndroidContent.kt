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

package com.github.zly2006.zhihu.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.github.zly2006.zhihu.MainActivity
import com.github.zly2006.zhihu.platform.androidUserMessageSink
import com.github.zly2006.zhihu.platform.rememberSettingsStore
import com.github.zly2006.zhihu.ui.article.ARTICLE_OPEN_TRANSITION_ENABLED_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.article.ARTICLE_OPEN_TRANSITION_SPEED_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.article.DEFAULT_ARTICLE_OPEN_TRANSITION_ENABLED
import com.github.zly2006.zhihu.ui.article.DEFAULT_ARTICLE_OPEN_TRANSITION_SPEED
import com.github.zly2006.zhihu.ui.article.articleOpenTransitionDurationMillis
import com.github.zly2006.zhihu.ui.article.resolveArticleEnterTransition
import com.github.zly2006.zhihu.ui.article.resolveArticleExitTransition
import com.github.zly2006.zhihu.viewmodel.ArticleViewModel
import com.github.zly2006.zhihu.viewmodel.sharedArticleAnswerSwitchState

/**
 * Android 平台的 Zhihu++ 主界面入口。
 *
 * 这里把 [MainActivity] 持有的导航、偏好设置、文章页 ViewModel、回答切换转场和 NLP 页面适配到共享 [ZhihuMain]。
 * UI 结构仍由 common 主壳负责，Android 只提供生命周期、Activity、ViewModel 和平台专属页面实现。
 */
@Composable
fun AndroidZhihuMain(navController: NavHostController) {
    val activity = LocalActivity.current as MainActivity
    val isTablet = LocalConfiguration.current.smallestScreenWidthDp >= 600
    val settings = rememberSettingsStore()
    ZhihuMain(
        navController = navController,
        mainTabNavigationTarget = activity.mainTabNavigationTarget,
        navigate = activity::navigate,
        navigateContent = activity::navigateIn,
        // 关闭横屏列表-详情分栏：回答页改为落在主 NavHost，返回手势才能预览真实的上一页（列表），
        // 而不是分栏栈里那个空态页喵。
        enableLandscapeListDetail = false,
        isListDetailCapable = isTablet,
        setCurrentMainTabOpenFrom = activity::setCurrentMainTabOpenFrom,
        consumeMainTabNavigationTarget = activity::consumeMainTabNavigationTarget,
        preferenceState = rememberAndroidZhihuMainPreferenceState(),
        isDarkTheme = com.github.zly2006.zhihu.theme.ThemeManager.isDarkTheme,
        articleEnterTransition = {
            // 这个 lambda 每次导航都会执行，所以直接读设置即可，不需要额外的响应式状态喵。
            resolveArticleEnterTransition(
                sharedArticleAnswerSwitchState.answerTransitionDirection,
                settings.getBoolean(
                    ARTICLE_OPEN_TRANSITION_ENABLED_PREFERENCE_KEY,
                    DEFAULT_ARTICLE_OPEN_TRANSITION_ENABLED,
                ),
                articleOpenTransitionDurationMillis(
                    settings.getFloat(
                        ARTICLE_OPEN_TRANSITION_SPEED_PREFERENCE_KEY,
                        DEFAULT_ARTICLE_OPEN_TRANSITION_SPEED,
                    ),
                ),
            )
        },
        articleExitTransition = {
            resolveArticleExitTransition(sharedArticleAnswerSwitchState.answerTransitionDirection)
        },
        articleContent = { article, navEntry ->
            val viewModel: ArticleViewModel = viewModel(navEntry) {
                ArticleViewModel(article, activity.httpClient, androidUserMessageSink(activity)) { onPause ->
                    navEntry.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onPause(owner: LifecycleOwner) {
                            onPause()
                        }
                    })
                }
            }
            ArticleScreen(article, viewModel)
        },
        sentenceSimilarityContent = {
            SentenceSimilarityTestScreen()
        },
        blocklistSettingsNlpContent = { onNavigateBack ->
            NLPKeywordManagementScreen(
                innerPadding = PaddingValues(),
                onNavigateBack = onNavigateBack,
            )
        },
    )
}
