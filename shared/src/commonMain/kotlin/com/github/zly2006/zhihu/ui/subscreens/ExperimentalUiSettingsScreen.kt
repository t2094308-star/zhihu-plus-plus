/*
 * Zhihu++ - Free & Ad-Free Zhihu client for all platforms.
 * Copyright (C) 2024-2026, zly2006 <i@zly2006.me>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation (version 3 only).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.zly2006.zhihu.ui.subscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.zly2006.zhihu.navigation.LocalNavigator
import com.github.zly2006.zhihu.platform.DisplayFrameRateMode
import com.github.zly2006.zhihu.platform.isDisplayFrameRateControlSupported
import com.github.zly2006.zhihu.platform.isPredictiveBackSupported
import com.github.zly2006.zhihu.platform.rememberSettingsStore
import com.github.zly2006.zhihu.platform.rememberUserMessageSink
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_BLUR_DP
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_CORNER_DP
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_HIGHLIGHT_ALPHA
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_REFRACTION_DP
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_SURFACE_ALPHA
import com.github.zly2006.zhihu.theme.isLiquidGlassSupported
import com.github.zly2006.zhihu.ui.article.ARTICLE_OPEN_TRANSITION_ENABLED_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.article.ARTICLE_OPEN_TRANSITION_SPEED_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.article.DEFAULT_ARTICLE_OPEN_TRANSITION_ENABLED
import com.github.zly2006.zhihu.ui.article.DEFAULT_ARTICLE_OPEN_TRANSITION_SPEED
import com.github.zly2006.zhihu.ui.article.MAX_ARTICLE_OPEN_TRANSITION_SPEED
import com.github.zly2006.zhihu.ui.article.MIN_ARTICLE_OPEN_TRANSITION_SPEED
import com.github.zly2006.zhihu.ui.article.normalizedArticleOpenTransitionSpeed
import com.github.zly2006.zhihu.ui.components.SettingItem
import com.github.zly2006.zhihu.ui.components.SettingItemGroup
import com.github.zly2006.zhihu.ui.components.SettingItemOverall
import com.github.zly2006.zhihu.ui.components.SettingItemWithSwitch
import com.github.zly2006.zhihu.ui.components.pageTurnViewportWithGuide
import com.github.zly2006.zhihu.ui.components.rememberPageTurnTarget
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

/** 测试版 UI 改造页的滚动容器测试 tag喵。 */
const val EXPERIMENTAL_UI_SETTINGS_SCROLL_TAG = "experimentalUiSettings.scroll"

/**
 * 测试版 UI 改造设置页喵。
 *
 * 这里收纳仍在打磨中的界面改动：液态玻璃、从卡片展开的转场、返回手势预览和刷新率锁定喵。
 * 它们相对不稳定、还会继续调整，所以从"外观与阅读体验"里分出来，
 * 免得和主题、字号这类长期稳定的设置混在一起喵。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentalUiSettingsScreen(
    setting: String?,
    onExit: () -> Unit,
) {
    val settingKey = setting.orEmpty()
    val settings = rememberSettingsStore()
    val userMessages = rememberUserMessageSink()
    val scrollState = rememberScrollState()
    val pageTurnTarget = rememberPageTurnTarget(scrollState, enabled = true)
    val navigator = LocalNavigator.current

    val bringIntoViewRequesters = remember { mutableStateMapOf<String, BringIntoViewRequester>() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var scrolledSetting by remember { mutableStateOf<String?>(null) }

    fun requesterFor(settingKey: String): BringIntoViewRequester =
        bringIntoViewRequesters.getOrPut(settingKey) { BringIntoViewRequester() }

    DisposableEffect(Unit) {
        onDispose {
            onExit()
        }
    }

    LaunchedEffect(settingKey, bringIntoViewRequesters[settingKey]) {
        if (settingKey.isNotEmpty() && scrolledSetting != settingKey) {
            bringIntoViewRequesters[settingKey]?.let { requester ->
                scrolledSetting = settingKey
                delay(200.milliseconds)
                // 收缩 LargeTopAppBar（programmatic scroll 不触发 nestedScroll）
                scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffsetLimit
                requester.bringIntoView()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeTopAppBar(
                title = { Text("测试版 UI 改造") },
                navigationIcon = {
                    IconButton(
                        onClick = navigator.onNavigateBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pageTurnViewportWithGuide(pageTurnTarget)
                .verticalScroll(scrollState)
                .testTag(EXPERIMENTAL_UI_SETTINGS_SCROLL_TAG)
                .padding(innerPadding)
                .padding(vertical = 16.dp),
        ) {
            // ── 液态玻璃底栏 ─────────────────────────────────────────────────────
            if (isLiquidGlassSupported) {
                val glassBottomBarEnabled = remember {
                    mutableStateOf(settings.getBoolean(GLASS_ENABLED_PREFERENCE_KEY, false))
                }
                SettingItemGroup(
                    title = "液态玻璃（beta）",
                    settingKey = APPEARANCE_SETTINGS_GLASS_SECTION_KEY,
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor(APPEARANCE_SETTINGS_GLASS_SECTION_KEY),
                    header = {
                        SettingItemOverall(
                            title = { Text("启用液态玻璃") },
                            description = { Text("底部导航栏、回答页顶底栏、问题页顶栏与评论输入栏改用液态玻璃材质，采样并折射它们下方滚过的真实内容。") },
                            checked = glassBottomBarEnabled.value,
                            onCheckedChange = {
                                glassBottomBarEnabled.value = it
                                settings.putBoolean(GLASS_ENABLED_PREFERENCE_KEY, it)
                            },
                            settingKey = APPEARANCE_SETTINGS_GLASS_SECTION_KEY,
                            highlightedKey = settingKey,
                            bringIntoViewRequester = requesterFor(APPEARANCE_SETTINGS_GLASS_SECTION_KEY),
                        )
                    },
                ) {
                    AnimatedVisibility(glassBottomBarEnabled.value) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            var glassHighlight by remember {
                                mutableStateOf(
                                    settings.getFloat(
                                        GLASS_HIGHLIGHT_PREFERENCE_KEY,
                                        DEFAULT_GLASS_HIGHLIGHT_ALPHA,
                                    ),
                                )
                            }
                            var glassBlur by remember {
                                mutableStateOf(
                                    settings.getFloat(
                                        GLASS_BLUR_PREFERENCE_KEY,
                                        DEFAULT_GLASS_BLUR_DP,
                                    ),
                                )
                            }
                            var glassCorner by remember {
                                mutableStateOf(
                                    settings.getFloat(
                                        GLASS_CORNER_PREFERENCE_KEY,
                                        DEFAULT_GLASS_CORNER_DP,
                                    ),
                                )
                            }
                            var glassRefraction by remember {
                                mutableStateOf(
                                    settings.getFloat(
                                        GLASS_REFRACTION_PREFERENCE_KEY,
                                        DEFAULT_GLASS_REFRACTION_DP,
                                    ),
                                )
                            }
                            var glassSurfaceAlpha by remember {
                                mutableStateOf(
                                    settings.getFloat(
                                        GLASS_SURFACE_ALPHA_PREFERENCE_KEY,
                                        DEFAULT_GLASS_SURFACE_ALPHA,
                                    ),
                                )
                            }

                            GlassBottomBarSlider(
                                title = "轮廓光",
                                currentValueText = "当前 ${(glassHighlight * 100).roundToInt()}%",
                                value = glassHighlight,
                                onValueChange = {
                                    glassHighlight = it
                                    settings.putFloat(GLASS_HIGHLIGHT_PREFERENCE_KEY, it)
                                },
                                valueRange = 0f..1f,
                                steps = 9,
                                tag = APPEARANCE_SETTINGS_GLASS_HIGHLIGHT_TAG,
                            )
                            GlassBottomBarSlider(
                                title = "模糊程度",
                                currentValueText = "当前 ${glassBlur.roundToInt()} dp",
                                value = glassBlur,
                                onValueChange = {
                                    glassBlur = it
                                    settings.putFloat(GLASS_BLUR_PREFERENCE_KEY, it)
                                },
                                valueRange = 0f..40f,
                                steps = 19,
                                tag = APPEARANCE_SETTINGS_GLASS_BLUR_TAG,
                            )
                            GlassBottomBarSlider(
                                title = "圆角",
                                currentValueText = "当前 ${glassCorner.roundToInt()} dp",
                                value = glassCorner,
                                onValueChange = {
                                    glassCorner = it
                                    settings.putFloat(GLASS_CORNER_PREFERENCE_KEY, it)
                                },
                                valueRange = 0f..32f,
                                steps = 15,
                                tag = APPEARANCE_SETTINGS_GLASS_CORNER_TAG,
                            )
                            GlassBottomBarSlider(
                                title = "折射强度",
                                currentValueText = "当前 ${glassRefraction.roundToInt()} dp",
                                value = glassRefraction,
                                onValueChange = {
                                    glassRefraction = it
                                    settings.putFloat(GLASS_REFRACTION_PREFERENCE_KEY, it)
                                },
                                valueRange = 0f..48f,
                                steps = 23,
                                tag = APPEARANCE_SETTINGS_GLASS_REFRACTION_TAG,
                            )
                            GlassBottomBarSlider(
                                title = "透明度",
                                currentValueText = "当前 ${(glassSurfaceAlpha * 100).roundToInt()}%",
                                value = glassSurfaceAlpha,
                                onValueChange = {
                                    glassSurfaceAlpha = it
                                    settings.putFloat(GLASS_SURFACE_ALPHA_PREFERENCE_KEY, it)
                                },
                                valueRange = 0f..1f,
                                steps = 19,
                                tag = APPEARANCE_SETTINGS_GLASS_SURFACE_ALPHA_TAG,
                            )
                            SettingItem(
                                modifier = Modifier.testTag(APPEARANCE_SETTINGS_GLASS_RESET_TAG),
                                title = { Text("恢复官方默认") },
                                description = { Text("只恢复上面 5 个绘制参数，不改变总开关。") },
                                onClick = {
                                    glassHighlight = DEFAULT_GLASS_HIGHLIGHT_ALPHA
                                    glassBlur = DEFAULT_GLASS_BLUR_DP
                                    glassCorner = DEFAULT_GLASS_CORNER_DP
                                    glassRefraction = DEFAULT_GLASS_REFRACTION_DP
                                    glassSurfaceAlpha = DEFAULT_GLASS_SURFACE_ALPHA
                                    settings.putFloat(GLASS_HIGHLIGHT_PREFERENCE_KEY, glassHighlight)
                                    settings.putFloat(GLASS_BLUR_PREFERENCE_KEY, glassBlur)
                                    settings.putFloat(GLASS_CORNER_PREFERENCE_KEY, glassCorner)
                                    settings.putFloat(GLASS_REFRACTION_PREFERENCE_KEY, glassRefraction)
                                    settings.putFloat(GLASS_SURFACE_ALPHA_PREFERENCE_KEY, glassSurfaceAlpha)
                                },
                            )
                        }
                    }
                }
            }
                // 上浮扩散转场与滑动手势无关，所以放在 isAnswerSwipeSupported 的可见性判断之外喵。
                val articleOpenTransitionEnabled = remember {
                    mutableStateOf(
                        settings.getBoolean(
                            ARTICLE_OPEN_TRANSITION_ENABLED_PREFERENCE_KEY,
                            DEFAULT_ARTICLE_OPEN_TRANSITION_ENABLED,
                        ),
                    )
                }
                SettingItemWithSwitch(
                    modifier = Modifier.testTag(APPEARANCE_SETTINGS_ARTICLE_OPEN_TRANSITION_TAG),
                    title = { Text("打开回答的上浮转场") },
                    description = { Text("从列表打开回答时，页面微微上浮并扩散到全屏；关闭后沿用原来的横向滑入。") },
                    checked = articleOpenTransitionEnabled.value,
                    onCheckedChange = {
                        articleOpenTransitionEnabled.value = it
                        settings.putBoolean(ARTICLE_OPEN_TRANSITION_ENABLED_PREFERENCE_KEY, it)
                    },
                    settingKey = ARTICLE_OPEN_TRANSITION_ENABLED_PREFERENCE_KEY,
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor(ARTICLE_OPEN_TRANSITION_ENABLED_PREFERENCE_KEY),
                )
                var articleOpenTransitionSpeed by remember {
                    mutableStateOf(
                        normalizedArticleOpenTransitionSpeed(
                            settings.getFloat(
                                ARTICLE_OPEN_TRANSITION_SPEED_PREFERENCE_KEY,
                                DEFAULT_ARTICLE_OPEN_TRANSITION_SPEED,
                            ),
                        ),
                    )
                }
                AnimatedVisibility(articleOpenTransitionEnabled.value) {
                    SettingItem(
                        title = { Text("转场速度") },
                        description = {
                            Text("当前 ${(articleOpenTransitionSpeed * 10).roundToInt() / 10f}x，数值越大越快。")
                        },
                        settingKey = ARTICLE_OPEN_TRANSITION_SPEED_PREFERENCE_KEY,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(ARTICLE_OPEN_TRANSITION_SPEED_PREFERENCE_KEY),
                        bottomAction = {
                            Slider(
                                value = articleOpenTransitionSpeed,
                                onValueChange = {
                                    val speed = (it * 10).roundToInt() / 10f
                                    articleOpenTransitionSpeed = speed
                                    settings.putFloat(ARTICLE_OPEN_TRANSITION_SPEED_PREFERENCE_KEY, speed)
                                },
                                valueRange = MIN_ARTICLE_OPEN_TRANSITION_SPEED..MAX_ARTICLE_OPEN_TRANSITION_SPEED,
                                steps = 14,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .testTag(APPEARANCE_SETTINGS_ARTICLE_OPEN_TRANSITION_SPEED_TAG),
                            )
                        },
                    )
                }
                if (isPredictiveBackSupported) {
                    val enablePredictiveBack = remember {
                        mutableStateOf(settings.getBoolean(PREDICTIVE_BACK_PREFERENCE_KEY, true))
                    }
                    SettingItemWithSwitch(
                        title = { Text("启用预测性返回") },
                        description = {
                            Text("拖动返回手势时预览应用自管的返回动作；页面之间的转场动画由导航组件提供，不受此开关控制。")
                        },
                        checked = enablePredictiveBack.value,
                        onCheckedChange = {
                            enablePredictiveBack.value = it
                            settings.putBoolean(PREDICTIVE_BACK_PREFERENCE_KEY, it)
                        },
                        settingKey = PREDICTIVE_BACK_PREFERENCE_KEY,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(PREDICTIVE_BACK_PREFERENCE_KEY),
                    )
                }
            // ── 显示与性能 ──────────────────────────────────────────────────────
            if (isDisplayFrameRateControlSupported) {
                SettingItemGroup(
                    title = "显示与性能",
                    settingKey = DISPLAY_FRAME_RATE_PREFERENCE_KEY,
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor(DISPLAY_FRAME_RATE_PREFERENCE_KEY),
                ) {
                    var frameRateMenuExpanded by remember { mutableStateOf(false) }
                    val frameRateMode = remember {
                        mutableStateOf(
                            DisplayFrameRateMode.fromPreference(
                                settings.getString(
                                    DISPLAY_FRAME_RATE_PREFERENCE_KEY,
                                    DisplayFrameRateMode.System.preferenceValue,
                                ),
                            ),
                        )
                    }
                    SettingItem(
                        title = { Text("显示刷新率") },
                        description = {
                            Text("把窗口锁定到指定刷新率；高刷更跟手也更耗电，系统省电模式或厂商的智能刷新率策略可能覆盖此设置。")
                        },
                        endAction = {
                            ExposedDropdownMenuBox(
                                expanded = frameRateMenuExpanded,
                                onExpandedChange = { frameRateMenuExpanded = it },
                            ) {
                                OutlinedTextField(
                                    value = frameRateMode.value.label,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = frameRateMenuExpanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                        .width(140.dp)
                                        .testTag(APPEARANCE_SETTINGS_DISPLAY_FRAME_RATE_TAG),
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                )
                                ExposedDropdownMenu(
                                    expanded = frameRateMenuExpanded,
                                    onDismissRequest = { frameRateMenuExpanded = false },
                                ) {
                                    DisplayFrameRateMode.entries.forEach { mode ->
                                        DropdownMenuItem(
                                            modifier = Modifier.testTag(
                                                "appearanceSettings.displayFrameRate:option:${mode.preferenceValue}",
                                            ),
                                            text = { Text(mode.label) },
                                            onClick = {
                                                frameRateMode.value = mode
                                                settings.putString(
                                                    DISPLAY_FRAME_RATE_PREFERENCE_KEY,
                                                    mode.preferenceValue,
                                                )
                                                frameRateMenuExpanded = false
                                            },
                                        )
                                    }
                                }
                            }
                        },
                        settingKey = DISPLAY_FRAME_RATE_PREFERENCE_KEY,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(DISPLAY_FRAME_RATE_PREFERENCE_KEY),
                    )
                }
            }
        }
    }
}

/**
 * 玻璃底栏的一个可调参数行喵。
 *
 * 5 个参数共用同一套标题、当前值和滑块布局，避免把同一段布局抄 5 遍喵。
 */
@Composable
private fun GlassBottomBarSlider(
    title: String,
    currentValueText: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    tag: String,
) {
    SettingItem(
        title = { Text(title) },
        description = { Text(currentValueText) },
        bottomAction = {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .testTag(tag),
            )
        },
    )
}
