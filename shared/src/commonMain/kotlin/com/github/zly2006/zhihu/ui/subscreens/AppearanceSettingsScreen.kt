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

package com.github.zly2006.zhihu.ui.subscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.zly2006.zhihu.markdown.TiqianBrandTitle
import com.github.zly2006.zhihu.markdown.isTiqianMarkdownRendererAvailable
import com.github.zly2006.zhihu.navigation.Account
import com.github.zly2006.zhihu.navigation.Daily
import com.github.zly2006.zhihu.navigation.Follow
import com.github.zly2006.zhihu.navigation.Home
import com.github.zly2006.zhihu.navigation.HotList
import com.github.zly2006.zhihu.navigation.LocalNavigator
import com.github.zly2006.zhihu.navigation.MyCollections
import com.github.zly2006.zhihu.navigation.OnlineHistory
import com.github.zly2006.zhihu.navigation.TopLevelDestination
import com.github.zly2006.zhihu.platform.DisplayFrameRateMode
import com.github.zly2006.zhihu.platform.SettingsStore
import com.github.zly2006.zhihu.platform.isAnswerSwipeSupported
import com.github.zly2006.zhihu.platform.isDisplayFrameRateControlSupported
import com.github.zly2006.zhihu.platform.isPageTurnSupported
import com.github.zly2006.zhihu.platform.isPredictiveBackSupported
import com.github.zly2006.zhihu.platform.platformBottomBarItemLimit
import com.github.zly2006.zhihu.platform.rememberSettingsStore
import com.github.zly2006.zhihu.platform.rememberUserMessageSink
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_BLUR_DP
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_CORNER_DP
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_HIGHLIGHT_ALPHA
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_REFRACTION_DP
import com.github.zly2006.zhihu.theme.DEFAULT_GLASS_SURFACE_ALPHA
import com.github.zly2006.zhihu.theme.LiquidGlassSettings
import com.github.zly2006.zhihu.theme.ThemeManager
import com.github.zly2006.zhihu.theme.ThemeMode
import com.github.zly2006.zhihu.theme.isLiquidGlassSupported
import com.github.zly2006.zhihu.ui.ANSWER_DOUBLE_TAP_ACTION_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.ARTICLE_USE_WEBVIEW_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.AnswerDoubleTapAction
import com.github.zly2006.zhihu.ui.article.ARTICLE_OPEN_TRANSITION_ENABLED_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.article.ARTICLE_OPEN_TRANSITION_SPEED_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.article.DEFAULT_ARTICLE_OPEN_TRANSITION_ENABLED
import com.github.zly2006.zhihu.ui.article.DEFAULT_ARTICLE_OPEN_TRANSITION_SPEED
import com.github.zly2006.zhihu.ui.article.MAX_ARTICLE_OPEN_TRANSITION_SPEED
import com.github.zly2006.zhihu.ui.article.MIN_ARTICLE_OPEN_TRANSITION_SPEED
import com.github.zly2006.zhihu.ui.article.normalizedArticleOpenTransitionSpeed
import com.github.zly2006.zhihu.ui.components.ANSWER_SWITCH_SENSITIVITY_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.components.ColorPickerDialog
import com.github.zly2006.zhihu.ui.components.DEFAULT_ANSWER_SWITCH_SENSITIVITY
import com.github.zly2006.zhihu.ui.components.DISABLE_BOTTOM_SHEET_ROUNDED_CORNERS_PREFERENCE_KEY
import com.github.zly2006.zhihu.ui.components.MAX_ANSWER_SWITCH_SENSITIVITY
import com.github.zly2006.zhihu.ui.components.MIN_ANSWER_SWITCH_SENSITIVITY
import com.github.zly2006.zhihu.ui.components.SettingItem
import com.github.zly2006.zhihu.ui.components.SettingItemGroup
import com.github.zly2006.zhihu.ui.components.SettingItemOverall
import com.github.zly2006.zhihu.ui.components.SettingItemWithSwitch
import com.github.zly2006.zhihu.ui.components.normalizedAnswerSwitchSensitivity
import com.github.zly2006.zhihu.ui.components.pageTurnViewportWithGuide
import com.github.zly2006.zhihu.ui.components.rememberPageTurnTarget
import com.github.zly2006.zhihu.ui.isLegacyWebViewSupported
import com.github.zly2006.zhihu.ui.rememberObservedSetting
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

const val DUO3_CARD_LARGE_TITLE_PREFERENCE_KEY = "duo3_card_large_title"
const val DUO3_TIQIAN_MARKDOWN_PREFERENCE_KEY = "duo3_tiqian_markdown"
const val DUO3_TIQIAN_MATH_FONT_PREFERENCE_KEY = "duo3_tiqian_math_font"
const val PREF_FONT_SIZE = "contentFontSize"
const val PREF_LINE_HEIGHT = "contentLineHeight"
const val PREF_BLOCK_SPACING = "contentBlockSpacing"
const val PREF_FAB_OPACITY = "fabOpacity"
const val DEFAULT_FAB_OPACITY = 100
const val PREF_PAGE_TURN_PERCENT = "pageTurnPercent"
const val DEFAULT_PAGE_TURN_PERCENT = 90
const val PREF_PAGE_TURN_SWITCH_ANSWER = "pageTurnSwitchAnswer"
const val DEFAULT_PAGE_TURN_SWITCH_ANSWER = true
const val PREF_SHOW_PAGE_TURN_FAB = "showPageTurnFab"
const val PREF_SHOW_PAGE_TURN_GUIDE = "showPageTurnGuide"
const val DEFAULT_SHOW_PAGE_TURN_GUIDE = false
const val PREF_SHOW_CONTENT_END_MARKER = "showContentEndMarker"
const val DEFAULT_SHOW_CONTENT_END_MARKER = false
const val PREF_VOLUME_KEY_PAGE_TURN = "volumeKeyPageTurn"
const val APPEARANCE_SETTINGS_SCROLL_TAG = "appearanceSettings.scroll"
const val APPEARANCE_SETTINGS_START_DESTINATION_TAG = "appearanceSettings.startDestination"
const val APPEARANCE_SETTINGS_ANSWER_DOUBLE_TAP_TAG = "appearanceSettings.answerDoubleTap"
const val APPEARANCE_SETTINGS_ANSWER_SWITCH_SENSITIVITY_TAG = "appearanceSettings.answerSwitchSensitivity"
const val APPEARANCE_SETTINGS_USE_WEBVIEW_TAG = "appearanceSettings.useWebView"
const val APPEARANCE_SETTINGS_TIQIAN_MARKDOWN_TAG = "appearanceSettings.tiqianMarkdown"
const val APPEARANCE_SETTINGS_WEBVIEW_FONT_TAG = "appearanceSettings.webViewFont"
const val APPEARANCE_SETTINGS_WEBVIEW_OPTIONS_TAG = "appearanceSettings.webViewOptions"
const val APPEARANCE_SETTINGS_BOTTOM_BAR_SECTION_KEY = "appearanceSettings.bottomBarSection"
const val APPEARANCE_SETTINGS_COLLECTION_DIRECT_BROWSE_TAG = "appearanceSettings.collectionDirectBrowse"
const val APPEARANCE_SETTINGS_DISABLE_BOTTOM_SHEET_ROUNDED_CORNERS_TAG = "appearanceSettings.disableBottomSheetRoundedCorners"
const val APPEARANCE_SETTINGS_LANDSCAPE_LIST_DETAIL_TAG = "appearanceSettings.landscapeListDetail"

const val START_DESTINATION_PREFERENCE_KEY = "startDestination"
const val BOTTOM_BAR_ITEMS_PREFERENCE_KEY = "bottom_bar_items"
const val BOTTOM_BAR_ITEM_ORDER_PREFERENCE_KEY = "bottom_bar_item_order"
const val COLLECTION_DIRECT_BROWSE_PREFERENCE_KEY = "collectionDirectBrowse"
const val LANDSCAPE_LIST_DETAIL_PREFERENCE_KEY = "landscapeListDetail"

const val APPEARANCE_SETTINGS_ARTICLE_OPEN_TRANSITION_TAG = "appearanceSettings.articleOpenTransition"
const val APPEARANCE_SETTINGS_ARTICLE_OPEN_TRANSITION_SPEED_TAG = "appearanceSettings.articleOpenTransitionSpeed"

/** 预测性返回开关；控制应用自管的返回动作是否参与进度预览喵。 */
const val PREDICTIVE_BACK_PREFERENCE_KEY = "enable_predictive_back"

/** 窗口首选刷新率档位；取值来自 `DisplayFrameRateMode.preferenceValue`喵。 */
const val DISPLAY_FRAME_RATE_PREFERENCE_KEY = "displayFrameRateMode"

const val APPEARANCE_SETTINGS_DISPLAY_FRAME_RATE_TAG = "appearanceSettings.displayFrameRate"

const val APPEARANCE_SETTINGS_GLASS_SECTION_KEY = "appearanceSettings.glassBottomBarSection"
const val APPEARANCE_SETTINGS_GLASS_HIGHLIGHT_TAG = "appearanceSettings.glassBottomBar.highlight"
const val APPEARANCE_SETTINGS_GLASS_BLUR_TAG = "appearanceSettings.glassBottomBar.blur"
const val APPEARANCE_SETTINGS_GLASS_CORNER_TAG = "appearanceSettings.glassBottomBar.corner"
const val APPEARANCE_SETTINGS_GLASS_REFRACTION_TAG = "appearanceSettings.glassBottomBar.refraction"
const val APPEARANCE_SETTINGS_GLASS_SURFACE_ALPHA_TAG = "appearanceSettings.glassBottomBar.surfaceAlpha"
const val APPEARANCE_SETTINGS_GLASS_RESET_TAG = "appearanceSettings.glassBottomBar.reset"

/** 玻璃底栏总开关喵。 */
const val GLASS_ENABLED_PREFERENCE_KEY = "glassBottomBar"
const val GLASS_HIGHLIGHT_PREFERENCE_KEY = "glassBottomBarHighlight"
const val GLASS_BLUR_PREFERENCE_KEY = "glassBottomBarBlurDp"
const val GLASS_CORNER_PREFERENCE_KEY = "glassBottomBarCornerDp"
const val GLASS_REFRACTION_PREFERENCE_KEY = "glassBottomBarRefractionDp"
const val GLASS_SURFACE_ALPHA_PREFERENCE_KEY = "glassBottomBarSurfaceAlpha"

/**
 * 读取玻璃底栏的绘制参数喵。
 *
 * 总开关关闭时返回 null，调用侧据此完全跳过玻璃路径、保留原有底栏喵。
 * Android 主壳与桌面主壳共用这一段，避免两处各写一份 key 与默认值喵。
 */
fun readLiquidGlassSettings(settings: SettingsStore): LiquidGlassSettings? {
    if (!settings.getBoolean(GLASS_ENABLED_PREFERENCE_KEY, false)) return null
    return LiquidGlassSettings(
        highlightAlpha = settings.getFloat(
            GLASS_HIGHLIGHT_PREFERENCE_KEY,
            DEFAULT_GLASS_HIGHLIGHT_ALPHA,
        ),
        blurDp = settings.getFloat(GLASS_BLUR_PREFERENCE_KEY, DEFAULT_GLASS_BLUR_DP),
        cornerDp = settings.getFloat(GLASS_CORNER_PREFERENCE_KEY, DEFAULT_GLASS_CORNER_DP),
        refractionDp = settings.getFloat(
            GLASS_REFRACTION_PREFERENCE_KEY,
            DEFAULT_GLASS_REFRACTION_DP,
        ),
        surfaceAlpha = settings.getFloat(
            GLASS_SURFACE_ALPHA_PREFERENCE_KEY,
            DEFAULT_GLASS_SURFACE_ALPHA,
        ),
    )
}

/**
 * 组合期读取玻璃参数喵。
 *
 * 同时观察总开关与 5 个参数键，用户在设置页调整后立刻生效；
 * 总开关关闭或平台不支持时返回 null，调用侧据此完全跳过玻璃路径、保留原有样式喵。
 */
@Composable
fun rememberLiquidGlassSettings(settings: SettingsStore): LiquidGlassSettings? {
    val enabled = rememberObservedSetting(settings, GLASS_ENABLED_PREFERENCE_KEY) {
        getBoolean(GLASS_ENABLED_PREFERENCE_KEY, false)
    }
    val highlightAlpha = rememberObservedSetting(settings, GLASS_HIGHLIGHT_PREFERENCE_KEY) {
        getFloat(GLASS_HIGHLIGHT_PREFERENCE_KEY, DEFAULT_GLASS_HIGHLIGHT_ALPHA)
    }
    val blurDp = rememberObservedSetting(settings, GLASS_BLUR_PREFERENCE_KEY) {
        getFloat(GLASS_BLUR_PREFERENCE_KEY, DEFAULT_GLASS_BLUR_DP)
    }
    val cornerDp = rememberObservedSetting(settings, GLASS_CORNER_PREFERENCE_KEY) {
        getFloat(GLASS_CORNER_PREFERENCE_KEY, DEFAULT_GLASS_CORNER_DP)
    }
    val refractionDp = rememberObservedSetting(settings, GLASS_REFRACTION_PREFERENCE_KEY) {
        getFloat(GLASS_REFRACTION_PREFERENCE_KEY, DEFAULT_GLASS_REFRACTION_DP)
    }
    val surfaceAlpha = rememberObservedSetting(settings, GLASS_SURFACE_ALPHA_PREFERENCE_KEY) {
        getFloat(GLASS_SURFACE_ALPHA_PREFERENCE_KEY, DEFAULT_GLASS_SURFACE_ALPHA)
    }
    if (!isLiquidGlassSupported || !enabled.value) return null
    return LiquidGlassSettings(
        highlightAlpha = highlightAlpha.value,
        blurDp = blurDp.value,
        cornerDp = cornerDp.value,
        refractionDp = refractionDp.value,
        surfaceAlpha = surfaceAlpha.value,
    )
}

private const val BOTTOM_BAR_ITEM_ORDER_SEPARATOR = ","

internal val contentFontSizeLevels = (50..120 step 5).toList() + (130..200 step 10).toList()
private val bottomBarSettingItemHeight = 64.dp
private val bottomBarSettingItemSpacing = 4.dp

private val topLevelDestinationsInOrder: List<Pair<String, TopLevelDestination>> = listOf(
    Home.name to Home,
    Follow.name to Follow,
    HotList.name to HotList,
    Daily.name to Daily,
    OnlineHistory.name to OnlineHistory,
    MyCollections.name to MyCollections,
    Account.name to Account,
)

internal fun navDestinationFromName(name: String): TopLevelDestination = topLevelDestinationsInOrder
    .firstOrNull { it.first == name }
    ?.second
    ?: Home

internal fun resolveValidStartDestinationKey(
    preferredKey: String?,
    availableKeysInOrder: List<String>,
): String = when {
    !preferredKey.isNullOrEmpty() && preferredKey in availableKeysInOrder -> preferredKey
    availableKeysInOrder.isNotEmpty() -> availableKeysInOrder.first()
    else -> Home.name
}

internal fun defaultBottomBarSelectionKeys(
    duo3HomeAccount: Boolean,
    maximumSelection: Int? = 5,
): Set<String> = if (maximumSelection == null) {
    if (duo3HomeAccount) {
        linkedSetOf(Home.name, Follow.name, HotList.name, Daily.name, OnlineHistory.name, MyCollections.name)
    } else {
        linkedSetOf(Home.name, Follow.name, HotList.name, Daily.name, OnlineHistory.name, MyCollections.name, Account.name)
    }
} else if (duo3HomeAccount) {
    linkedSetOf(Home.name, Follow.name, Daily.name)
} else {
    linkedSetOf(Home.name, Follow.name, Daily.name, OnlineHistory.name, Account.name)
}

internal fun normalizeBottomBarSelection(
    selectedKeys: Collection<String>,
    duo3HomeAccount: Boolean,
    enforceMinimumSelection: Boolean = false,
    maximumSelection: Int? = 5,
): Set<String> {
    val allowedKeys = topLevelDestinationsInOrder.map { it.first }.toSet()
    val normalized = selectedKeys
        .filterTo(linkedSetOf()) { it in allowedKeys }
        .ifEmpty { defaultBottomBarSelectionKeys(duo3HomeAccount, maximumSelection).toMutableSet() }

    if (duo3HomeAccount) {
        if (Home.name in normalized) {
            normalized.remove(Account.name)
        } else {
            normalized.add(Account.name)
        }
    } else {
        normalized.add(Account.name)
        while (maximumSelection != null && normalized.size > maximumSelection) {
            val removableKey = listOf(
                HotList.name,
                MyCollections.name,
                OnlineHistory.name,
                Daily.name,
                Follow.name,
                Home.name,
            ).firstOrNull { it in normalized } ?: break
            normalized.remove(removableKey)
        }
    }

    if (enforceMinimumSelection) {
        val fillOrder = if (duo3HomeAccount) {
            if (Home.name in normalized) {
                listOf(Follow.name, Daily.name, HotList.name, OnlineHistory.name)
            } else {
                listOf(Follow.name, Daily.name, HotList.name, OnlineHistory.name, Home.name)
            }
        } else {
            listOf(Home.name, Follow.name, Daily.name, HotList.name, OnlineHistory.name, MyCollections.name, Account.name)
        }
        fillOrder.forEach { key ->
            if (normalized.size < 3) {
                normalized.add(key)
            }
        }
    }

    return normalized
}

internal fun normalizeBottomBarItemOrder(
    preferredOrderKeys: List<String>,
    selectedKeys: Set<String>,
): List<String> {
    val allowedKeys = topLevelDestinationsInOrder.map { it.first }.toSet()
    val orderedKeys = mutableListOf<String>()
    preferredOrderKeys.forEach { key ->
        if (key in allowedKeys && key in selectedKeys && key !in orderedKeys) {
            orderedKeys.add(key)
        }
    }
    topLevelDestinationsInOrder.forEach { (key, _) ->
        if (key in selectedKeys && key !in orderedKeys) {
            orderedKeys.add(key)
        }
    }
    return orderedKeys
}

private fun bottomBarItemOrderPreferenceValue(keys: List<String>): String =
    keys.joinToString(BOTTOM_BAR_ITEM_ORDER_SEPARATOR)

internal fun bottomBarItemOrderFromPreference(
    preferenceValue: String?,
    selectedKeys: Set<String>,
): List<String> = normalizeBottomBarItemOrder(
    preferenceValue
        .orEmpty()
        .split(BOTTOM_BAR_ITEM_ORDER_SEPARATOR)
        .map { it.trim() }
        .filter { it.isNotEmpty() },
    selectedKeys,
)

internal fun shouldShowAccountHistoryShortcut(
    duo3HomeAccount: Boolean,
    selectedKeys: Set<String>,
): Boolean = duo3HomeAccount && OnlineHistory.name !in selectedKeys

/**
 * 外观与阅读体验设置页。
 *
 * 这里集中管理主题、字号/行高、信息流样式、文章页行为、底部导航栏、分享、搜索和技术性导航开关。页面支持通过 [setting]
 * 跳入指定设置项并高亮滚动到位，因此新增设置时应提供稳定的 `settingKey`，必要时也补充 test tag。
 *
 * 底部导航栏相关设置会影响 [com.github.zly2006.zhihu.ui.ZhihuMain] 的主壳状态；页面退出时必须通过 [onExit]
 * 触发上层重新读取设置，而不是直接重建 NavHost。
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
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
    val duo3HomeAccount = remember { mutableStateOf(settings.getBoolean("duo3_home_account", false)) }
    val selectedBottomBarItemKeys = remember {
        val normalizedSelection = normalizeBottomBarSelection(
            settings.getStringSet(
                BOTTOM_BAR_ITEMS_PREFERENCE_KEY,
                defaultBottomBarSelectionKeys(duo3HomeAccount.value, platformBottomBarItemLimit),
            ),
            duo3HomeAccount.value,
            enforceMinimumSelection = true,
            maximumSelection = platformBottomBarItemLimit,
        )
        mutableStateOf(
            bottomBarItemOrderFromPreference(
                settings.getStringOrNull(BOTTOM_BAR_ITEM_ORDER_PREFERENCE_KEY),
                normalizedSelection,
            ),
        )
    }
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
                title = { Text("外观与阅读体验") },
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
                .testTag(APPEARANCE_SETTINGS_SCROLL_TAG)
                .padding(innerPadding)
                .padding(vertical = 16.dp),
        ) {
            val useDynamicColor = ThemeManager.getUseDynamicColor()
            val currentThemeMode = ThemeManager.getThemeMode()

            // ── 主题 ────────────────────────────────────────────────────────────

            SettingItemGroup(
                title = "主题",
            ) {
                SettingItem(
                    title = { Text("主题模式") },
                    description = { Text("设置应用的显示主题。") },
                    settingKey = "nightMode",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("nightMode"),
                    bottomAction = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        ) {
                            val themeModes = listOf(
                                ThemeMode.SYSTEM to "自动",
                                ThemeMode.LIGHT to "亮色",
                                ThemeMode.DARK to "暗色",
                            )
                            themeModes.forEach { (mode, label) ->
                                val isSelected = currentThemeMode == mode
                                OutlinedButton(
                                    onClick = {
                                        ThemeManager.setThemeMode(mode)
                                        settings.putString("themeMode", mode.name)
                                        userMessages.showShortMessage("已切换到$label")
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            Color.Transparent
                                        },
                                        contentColor = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                    ),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(label)
                                }
                            }
                        }
                    },
                )

                SettingItemWithSwitch(
                    title = { Text("使用 Material You 动态取色") },
                    description = { Text("根据系统壁纸自动提取主题色（Android 12+ 可用）。\n关闭后可以自己设定主题颜色。") },
                    checked = useDynamicColor,
                    onCheckedChange = {
                        ThemeManager.setUseDynamicColor(it)
                        settings.putBoolean("useDynamicColor", it)
                        userMessages.showShortMessage("已${if (it) "启用" else "禁用"}动态取色")
                    },
                    settingKey = "dynamicColor",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("dynamicColor"),
                )

                var showColorPicker by remember { mutableStateOf(false) }
                val customColor = ThemeManager.getCustomColor()

                AnimatedVisibility(visible = !useDynamicColor) {
                    SettingItem(
                        title = { Text("自定义主题色") },
                        description = { Text("点击选择您喜欢的主题颜色") },
                        onClick = { showColorPicker = true },
                        endAction = {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(customColor)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                            )
                        },
                    )
                }
                if (showColorPicker) {
                    ColorPickerDialog(
                        title = "选择主题色",
                        initialColor = customColor,
                        onDismiss = { showColorPicker = false },
                        onColorSelected = { color ->
                            ThemeManager.setCustomColor(color)
                            settings.putInt("customThemeColor", color.toArgb())
                            userMessages.showShortMessage("主题色已保存")
                            showColorPicker = false
                        },
                    )
                }

                var showLuotianYiColorPicker by remember { mutableStateOf(false) }
                val luotianYiColor = remember {
                    Color(settings.getInt("luotianyi_color", 0xff_66CCFF.toInt()))
                }

                SettingItem(
                    title = { Text("唤起浏览器主题色") },
                    description = { Text("应用内浏览器的工具栏颜色") },
                    onClick = { showLuotianYiColorPicker = true },
                    endAction = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(luotianYiColor)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                        )
                    },
                )

                if (showLuotianYiColorPicker) {
                    ColorPickerDialog(
                        title = "选择浏览器主题色",
                        initialColor = luotianYiColor,
                        presetColors = listOf(
                            Color(0xFF66CCFF),
                            Color(0xFF2196F3),
                            Color(0xFF4CAF50),
                            Color(0xFFF44336),
                            Color(0xFFFF9800),
                            Color(0xFF9C27B0),
                        ),
                        onDismiss = { showLuotianYiColorPicker = false },
                        onColorSelected = { color ->
                            settings.putInt("luotianyi_color", color.toArgb())
                            userMessages.showShortMessage("浏览器主题色已保存")
                            showLuotianYiColorPicker = false
                        },
                    )
                }

                val currentIsDarkTheme = ThemeManager.isDarkTheme()
                var showBackgroundColorPicker by remember { mutableStateOf(false) }
                val backgroundColor = ThemeManager.getBackgroundColor()

                SettingItem(
                    title = { Text("自定义背景颜色") },
                    description = { Text(if (currentIsDarkTheme) "深色模式背景色" else "浅色模式背景色") },
                    onClick = { showBackgroundColorPicker = true },
                    endAction = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(backgroundColor)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                        )
                    },
                )

                if (showBackgroundColorPicker) {
                    ColorPickerDialog(
                        title = "选择背景颜色",
                        initialColor = backgroundColor,
                        presetColors = listOfNotNull(
                            Color(if (currentIsDarkTheme) 0xFF121212.toInt() else 0xFFFFFFFF.toInt()),
                            MaterialTheme.colorScheme.surfaceContainer,
                            if (ThemeManager.isDarkTheme()) Color.Black else null,
                        ),
                        onDismiss = { showBackgroundColorPicker = false },
                        onColorSelected = { color ->
                            ThemeManager.setBackgroundColor(color, currentIsDarkTheme)
                            settings.putInt(
                                if (currentIsDarkTheme) "backgroundColorDark" else "backgroundColorLight",
                                color.toArgb(),
                            )
                            userMessages.showShortMessage("背景颜色已保存")
                            showBackgroundColorPicker = false
                        },
                    )
                }

                val disableBottomSheetRoundedCorners = remember {
                    mutableStateOf(settings.getBoolean(DISABLE_BOTTOM_SHEET_ROUNDED_CORNERS_PREFERENCE_KEY, false))
                }
                SettingItemWithSwitch(
                    modifier = Modifier.testTag(APPEARANCE_SETTINGS_DISABLE_BOTTOM_SHEET_ROUNDED_CORNERS_TAG),
                    title = { Text("禁用 popup 圆角") },
                    description = { Text("开启后，评论等 popup 顶部不再显示圆角。") },
                    checked = disableBottomSheetRoundedCorners.value,
                    onCheckedChange = {
                        disableBottomSheetRoundedCorners.value = it
                        settings.putBoolean(DISABLE_BOTTOM_SHEET_ROUNDED_CORNERS_PREFERENCE_KEY, it)
                    },
                    settingKey = DISABLE_BOTTOM_SHEET_ROUNDED_CORNERS_PREFERENCE_KEY,
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor(DISABLE_BOTTOM_SHEET_ROUNDED_CORNERS_PREFERENCE_KEY),
                )

                var fabOpacity by remember {
                    mutableIntStateOf(settings.getInt(PREF_FAB_OPACITY, DEFAULT_FAB_OPACITY))
                }
                SettingItem(
                    title = { Text("悬浮按钮透明度") },
                    description = { Text("控制所有悬浮按钮的透明度 ($fabOpacity%)。") },
                    bottomAction = {
                        Slider(
                            value = fabOpacity.toFloat(),
                            onValueChange = {
                                val v = (it / 5).roundToInt() * 5
                                fabOpacity = v
                                settings.putInt(PREF_FAB_OPACITY, v)
                            },
                            valueRange = 10f..100f,
                            steps = 17,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        )
                    },
                )

                val landscapeListDetailEnabled = remember {
                    mutableStateOf(settings.getBoolean(LANDSCAPE_LIST_DETAIL_PREFERENCE_KEY, true))
                }
                SettingItemWithSwitch(
                    modifier = Modifier.testTag(APPEARANCE_SETTINGS_LANDSCAPE_LIST_DETAIL_TAG),
                    title = { Text("横屏双栏布局") },
                    description = { Text("在平板和电脑横屏时同时显示列表与详情。手机横屏始终使用单栏。") },
                    checked = landscapeListDetailEnabled.value,
                    onCheckedChange = {
                        landscapeListDetailEnabled.value = it
                        settings.putBoolean(LANDSCAPE_LIST_DETAIL_PREFERENCE_KEY, it)
                    },
                    settingKey = LANDSCAPE_LIST_DETAIL_PREFERENCE_KEY,
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor(LANDSCAPE_LIST_DETAIL_PREFERENCE_KEY),
                )
            }
            // ── 阅读 ────────────────────────────────────────────────────────────
            SettingItemGroup(
                title = "阅读",
            ) {
                var fontSize by remember {
                    val storedValue = settings.getInt(PREF_FONT_SIZE, 100)
                    val boundedValue = storedValue.coerceIn(
                        contentFontSizeLevels.first(),
                        contentFontSizeLevels.last(),
                    )
                    val snappedValue = contentFontSizeLevels.minBy { abs(it - boundedValue) }
                    if (storedValue != snappedValue) {
                        settings.putInt(PREF_FONT_SIZE, snappedValue)
                    }
                    mutableIntStateOf(snappedValue)
                }
                SettingItem(
                    title = { Text("字号") },
                    description = { Text("调整内容文字大小 ($fontSize%)") },
                    settingKey = "fontScale",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("fontScale"),
                    bottomAction = {
                        Slider(
                            value = contentFontSizeLevels.indexOf(fontSize).toFloat(),
                            onValueChange = {
                                val levelIndex = it.roundToInt().coerceIn(0, contentFontSizeLevels.lastIndex)
                                fontSize = contentFontSizeLevels[levelIndex]
                                settings.putInt(PREF_FONT_SIZE, fontSize)
                            },
                            valueRange = 0f..contentFontSizeLevels.lastIndex.toFloat(),
                            steps = contentFontSizeLevels.size - 2,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        )
                    },
                )

                var lineHeight by remember { mutableIntStateOf(settings.getInt(PREF_LINE_HEIGHT, 160)) }
                SettingItem(
                    title = { Text("行高") },
                    description = { Text("调整内容行间距 (${lineHeight / 100f})") },
                    bottomAction = {
                        Slider(
                            value = lineHeight.toFloat(),
                            onValueChange = {
                                lineHeight = it.toInt()
                                settings.putInt(PREF_LINE_HEIGHT, it.toInt())
                            },
                            valueRange = 100f..300f,
                            steps = 19,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        )
                    },
                )

                var blockSpacing by remember { mutableIntStateOf(settings.getInt(PREF_BLOCK_SPACING, 100)) }
                SettingItem(
                    title = { Text("段间距") },
                    description = { Text("调整正文段落和块级内容间距 ($blockSpacing%)") },
                    settingKey = PREF_BLOCK_SPACING,
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor(PREF_BLOCK_SPACING),
                    bottomAction = {
                        Slider(
                            value = blockSpacing.toFloat(),
                            onValueChange = {
                                val spacing = (it / 10).roundToInt() * 10
                                blockSpacing = spacing
                                settings.putInt(PREF_BLOCK_SPACING, spacing)
                            },
                            valueRange = 0f..300f,
                            steps = 29,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        )
                    },
                )
            }

            // ── 信息流 ──────────────────────────────────────────────────────────
            val showRefreshFab = remember { mutableStateOf(settings.getBoolean("showRefreshFab", true)) }
            SettingItemGroup(
                title = "信息流",
            ) {
                val showFeedThumbnail = remember { mutableStateOf(settings.getBoolean("showFeedThumbnail", true)) }
                SettingItemWithSwitch(
                    title = { Text("显示 Feed 卡片缩略图") },
                    description = { Text("在信息流卡片中显示文章缩略图。") },
                    checked = showFeedThumbnail.value,
                    onCheckedChange = {
                        showFeedThumbnail.value = it
                        settings.putBoolean("showFeedThumbnail", it)
                    },
                    settingKey = "showFeedThumbnail",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("showFeedThumbnail"),
                )

                SettingItemWithSwitch(
                    title = { Text("显示刷新悬浮按钮") },
                    description = { Text("在页面上显示可拖动的刷新按钮。") },
                    checked = showRefreshFab.value,
                    onCheckedChange = {
                        showRefreshFab.value = it
                        settings.putBoolean("showRefreshFab", it)
                    },
                    settingKey = "showRefreshFab",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("showRefreshFab"),
                )

                var feedCardStyleExpanded by remember { mutableStateOf(false) }
                val feedCardStyle = remember {
                    mutableStateOf(settings.getString("feedCardStyle", "divider"))
                }
                val feedCardStyleOptions = listOf(
                    "card" to "卡片样式",
                    "divider" to "分割线样式",
                )
                SettingItem(
                    title = { Text("信息流样式") },
                    description = { Text("卡片样式使用圆角卡片展示，分割线样式使用细线分隔条目。") },
                    settingKey = "feedCardStyle",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("feedCardStyle"),
                    endAction = {
                        ExposedDropdownMenuBox(
                            expanded = feedCardStyleExpanded,
                            onExpandedChange = { feedCardStyleExpanded = it },
                        ) {
                            OutlinedTextField(
                                value = feedCardStyleOptions.find { it.first == feedCardStyle.value }?.second ?: "卡片样式",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = feedCardStyleExpanded) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .width(160.dp),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            )
                            ExposedDropdownMenu(
                                expanded = feedCardStyleExpanded,
                                onDismissRequest = { feedCardStyleExpanded = false },
                            ) {
                                feedCardStyleOptions.forEach { (mode, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            feedCardStyle.value = mode
                                            settings.putString("feedCardStyle", mode)
                                            feedCardStyleExpanded = false
                                            userMessages.showShortMessage("已设置为：$label")
                                        },
                                    )
                                }
                            }
                        }
                    },
                )
            }

            // ── 回答页 ──────────────────────────────────────────────────────────
            val buttonSkipAnswer = remember { mutableStateOf(settings.getBoolean("buttonSkipAnswer", true)) }
            SettingItemGroup(
                title = "回答页",
            ) {
                val articleUseWebview = remember {
                    mutableStateOf(settings.getBoolean(ARTICLE_USE_WEBVIEW_PREFERENCE_KEY, false))
                }
                SettingItemWithSwitch(
                    modifier = Modifier.testTag(APPEARANCE_SETTINGS_USE_WEBVIEW_TAG),
                    title = { Text("使用 WebView 显示文章") },
                    description = { Text("关闭后使用 Compose 渲染，支持代码高亮等高级功能。警告：这个渲染模式不再推荐，非专业人士请不要开启！") },
                    checked = articleUseWebview.value,
                    enabled = isLegacyWebViewSupported,
                    onCheckedChange = {
                        articleUseWebview.value = it
                        settings.putBoolean(ARTICLE_USE_WEBVIEW_PREFERENCE_KEY, it)
                    },
                    settingKey = ARTICLE_USE_WEBVIEW_PREFERENCE_KEY,
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor(ARTICLE_USE_WEBVIEW_PREFERENCE_KEY),
                )

                if (articleUseWebview.value && isLegacyWebViewSupported) {
                    var customFontName by remember {
                        mutableStateOf(settings.getStringOrNull("webviewCustomFontName"))
                    }
                    Column(
                        modifier = Modifier.testTag(APPEARANCE_SETTINGS_WEBVIEW_OPTIONS_TAG),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        if (isWebViewCustomFontSupported) {
                            SettingItem(
                                modifier = Modifier.testTag(APPEARANCE_SETTINGS_WEBVIEW_FONT_TAG),
                                title = {
                                    Text(
                                        "WebView 自定义字体",
                                        modifier = Modifier.testTag(APPEARANCE_SETTINGS_WEBVIEW_FONT_TAG),
                                    )
                                },
                                description = { Text(customFontName ?: "未设置") },
                                bottomAction = {
                                    WebViewCustomFontSettings(
                                        customFontName = customFontName,
                                        onCustomFontNameChange = { name ->
                                            if (name == null) {
                                                settings.remove("webviewCustomFontName")
                                            } else {
                                                settings.putString("webviewCustomFontName", name)
                                            }
                                            customFontName = name
                                        },
                                    )
                                },
                            )
                        }

                        val useHardwareAcceleration = remember { mutableStateOf(settings.getBoolean("webviewHardwareAcceleration", true)) }
                        SettingItemWithSwitch(
                            title = { Text("WebView 硬件加速") },
                            description = { Text("提高渲染性能，可能导致兼容性问题。") },
                            checked = useHardwareAcceleration.value,
                            onCheckedChange = {
                                useHardwareAcceleration.value = it
                                settings.putBoolean("webviewHardwareAcceleration", it)
                            },
                        )
                    }
                }

                val isTitleAutoHide = remember { mutableStateOf(settings.getBoolean("titleAutoHide", false)) }
                SettingItemWithSwitch(
                    title = { Text("自动隐藏回答标题") },
                    description = { Text("滚动时自动隐藏回答标题栏。") },
                    checked = isTitleAutoHide.value,
                    onCheckedChange = {
                        isTitleAutoHide.value = it
                        settings.putBoolean("titleAutoHide", it)
                    },
                    settingKey = "titleAutoHide",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("titleAutoHide"),
                )

                val autoHideArticleBottomBar = remember {
                    mutableStateOf(settings.getBoolean("autoHideArticleBottomBar", false))
                }
                SettingItemWithSwitch(
                    title = { Text("自动隐藏回答底部按钮") },
                    description = { Text("上划时隐藏回答底部操作按钮，下划时重新显示。") },
                    checked = autoHideArticleBottomBar.value,
                    onCheckedChange = {
                        autoHideArticleBottomBar.value = it
                        settings.putBoolean("autoHideArticleBottomBar", it)
                    },
                    settingKey = "autoHideArticleBottomBar",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("autoHideArticleBottomBar"),
                )

                SettingItemWithSwitch(
                    title = { Text("显示跳转下一个回答按钮") },
                    description = { Text("在回答页面显示可拖动的快速跳转按钮。") },
                    checked = buttonSkipAnswer.value,
                    onCheckedChange = {
                        buttonSkipAnswer.value = it
                        settings.putBoolean("buttonSkipAnswer", it)
                    },
                    settingKey = "buttonSkipAnswer",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("buttonSkipAnswer"),
                )

                val autoHideSkipAnswerButton = remember { mutableStateOf(settings.getBoolean("autoHideSkipAnswerButton", true)) }
                AnimatedVisibility(buttonSkipAnswer.value) {
                    SettingItemWithSwitch(
                        title = { Text("滚动时自动隐藏跳转按钮") },
                        description = { Text("上划时淡出「下一个回答」按钮，下划时淡入显示。") },
                        checked = autoHideSkipAnswerButton.value,
                        onCheckedChange = {
                            autoHideSkipAnswerButton.value = it
                            settings.putBoolean("autoHideSkipAnswerButton", it)
                        },
                    )
                }

                val pinAnswerDate = remember { mutableStateOf(settings.getBoolean("pinAnswerDate", false)) }
                SettingItemWithSwitch(
                    title = { Text("置顶回答日期") },
                    description = { Text("将回答的发布日期和编辑日期移动到内容最前面显示。") },
                    checked = pinAnswerDate.value,
                    onCheckedChange = {
                        pinAnswerDate.value = it
                        settings.putBoolean("pinAnswerDate", it)
                    },
                    settingKey = "pinAnswerDate",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("pinAnswerDate"),
                )

                var answerSwitchExpanded by remember { mutableStateOf(false) }
                val answerSwitchMode = remember {
                    mutableStateOf(settings.getString("answerSwitchMode", "vertical"))
                }
                val answerSwitchOptions = listOf(
                    "off" to "关闭",
                    "vertical" to "上下滑动",
                    "horizontal" to "左右滑动",
                )
                if (isAnswerSwipeSupported) {
                    SettingItem(
                        title = { Text("回答切换手势") },
                        description = { Text("在回答页面通过手势切换同一问题下的其他回答。") },
                        settingKey = "answerSwitchMode",
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor("answerSwitchMode"),
                        endAction = {
                            ExposedDropdownMenuBox(
                                expanded = answerSwitchExpanded,
                                onExpandedChange = { answerSwitchExpanded = it },
                            ) {
                                OutlinedTextField(
                                    value = answerSwitchOptions.find { it.first == answerSwitchMode.value }?.second ?: "上下滑动切换",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = answerSwitchExpanded) },
                                    modifier = Modifier
                                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                        .width(160.dp),
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                )
                                ExposedDropdownMenu(
                                    expanded = answerSwitchExpanded,
                                    onDismissRequest = { answerSwitchExpanded = false },
                                ) {
                                    answerSwitchOptions.forEach { (mode, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                answerSwitchMode.value = mode
                                                settings.putString("answerSwitchMode", mode)
                                                answerSwitchExpanded = false
                                                userMessages.showShortMessage("已设置为：$label")
                                            },
                                        )
                                    }
                                }
                            }
                        },
                    )
                }

                var answerSwitchSensitivity by remember {
                    mutableStateOf(
                        normalizedAnswerSwitchSensitivity(
                            settings.getFloat(
                                ANSWER_SWITCH_SENSITIVITY_PREFERENCE_KEY,
                                DEFAULT_ANSWER_SWITCH_SENSITIVITY,
                            ),
                        ),
                    )
                }
                AnimatedVisibility(isAnswerSwipeSupported && answerSwitchMode.value != "off") {
                    SettingItem(
                        title = { Text("回答切换灵敏度") },
                        description = {
                            Text("当前 ${(answerSwitchSensitivity * 10).roundToInt() / 10f}x，数值越高，滑动越短；同时作用于上下和左右切换。")
                        },
                        settingKey = ANSWER_SWITCH_SENSITIVITY_PREFERENCE_KEY,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(ANSWER_SWITCH_SENSITIVITY_PREFERENCE_KEY),
                        bottomAction = {
                            Slider(
                                value = answerSwitchSensitivity,
                                onValueChange = {
                                    val sensitivity = (it * 10).roundToInt() / 10f
                                    answerSwitchSensitivity = sensitivity
                                    settings.putFloat(ANSWER_SWITCH_SENSITIVITY_PREFERENCE_KEY, sensitivity)
                                },
                                valueRange = MIN_ANSWER_SWITCH_SENSITIVITY..MAX_ANSWER_SWITCH_SENSITIVITY,
                                steps = 24,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .testTag(APPEARANCE_SETTINGS_ANSWER_SWITCH_SENSITIVITY_TAG),
                            )
                        },
                    )
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

                var answerDoubleTapExpanded by remember { mutableStateOf(false) }
                val answerDoubleTapAction = remember {
                    mutableStateOf(
                        AnswerDoubleTapAction.fromPreference(
                            settings.getString(
                                ANSWER_DOUBLE_TAP_ACTION_PREFERENCE_KEY,
                                AnswerDoubleTapAction.Ask.preferenceValue,
                            ),
                        ),
                    )
                }
                SettingItem(
                    title = { Text("双击回答动作") },
                    description = { Text("双击回答正文时执行的动作。默认弹窗询问。") },
                    settingKey = ANSWER_DOUBLE_TAP_ACTION_PREFERENCE_KEY,
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor(ANSWER_DOUBLE_TAP_ACTION_PREFERENCE_KEY),
                    endAction = {
                        ExposedDropdownMenuBox(
                            expanded = answerDoubleTapExpanded,
                            onExpandedChange = { answerDoubleTapExpanded = it },
                        ) {
                            OutlinedTextField(
                                value = answerDoubleTapAction.value.label,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = answerDoubleTapExpanded) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .width(160.dp)
                                    .testTag(APPEARANCE_SETTINGS_ANSWER_DOUBLE_TAP_TAG),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            )
                            ExposedDropdownMenu(
                                expanded = answerDoubleTapExpanded,
                                onDismissRequest = { answerDoubleTapExpanded = false },
                            ) {
                                AnswerDoubleTapAction.entries.forEach { action ->
                                    DropdownMenuItem(
                                        text = { Text(action.label) },
                                        onClick = {
                                            answerDoubleTapAction.value = action
                                            settings.putString(
                                                ANSWER_DOUBLE_TAP_ACTION_PREFERENCE_KEY,
                                                action.preferenceValue,
                                            )
                                            answerDoubleTapExpanded = false
                                            userMessages.showShortMessage("已设置为：${action.label}")
                                        },
                                    )
                                }
                            }
                        }
                    },
                )
            }

            // ── 底部导航栏 ──────────────────────────────────────────────────────
            val allBottomBarItems = listOf(
                Home.name to "主页",
                Follow.name to "关注",
                HotList.name to "热榜",
                Daily.name to "日报",
                OnlineHistory.name to "历史",
                MyCollections.name to "收藏夹",
                Account.name to "账号设置",
            )
            val bottomBarItemLabels = allBottomBarItems.toMap()
            var startDestinationExpanded by remember { mutableStateOf(false) }
            var startDestinationKey by remember {
                mutableStateOf(
                    resolveValidStartDestinationKey(
                        settings.getString(START_DESTINATION_PREFERENCE_KEY, Home.name),
                        allBottomBarItems.map { it.first }.filter { it in selectedBottomBarItemKeys.value },
                    ),
                )
            }

            fun persistBottomBarSelection(
                currentOrderKeys: List<String>,
                duo3HomeAccountEnabled: Boolean = duo3HomeAccount.value,
            ) {
                val normalizedSet = normalizeBottomBarSelection(
                    currentOrderKeys,
                    duo3HomeAccountEnabled,
                    enforceMinimumSelection = true,
                    maximumSelection = platformBottomBarItemLimit,
                )
                val normalizedOrderKeys = normalizeBottomBarItemOrder(currentOrderKeys, normalizedSet)
                val availableKeys = normalizedOrderKeys
                val resolvedStartDestination = resolveValidStartDestinationKey(startDestinationKey, availableKeys)
                selectedBottomBarItemKeys.value = normalizedOrderKeys
                startDestinationKey = resolvedStartDestination
                settings.putStringSet(BOTTOM_BAR_ITEMS_PREFERENCE_KEY, normalizedSet)
                settings.putString(
                    BOTTOM_BAR_ITEM_ORDER_PREFERENCE_KEY,
                    bottomBarItemOrderPreferenceValue(normalizedOrderKeys),
                )
                settings.putString(START_DESTINATION_PREFERENCE_KEY, resolvedStartDestination)
            }

            fun moveBottomBarItem(key: String, offset: Int) {
                val currentOrderKeys = selectedBottomBarItemKeys.value
                val fromIndex = currentOrderKeys.indexOf(key)
                val toIndex = fromIndex + offset
                if (fromIndex < 0 || toIndex !in currentOrderKeys.indices) {
                    return
                }
                val reorderedKeys = currentOrderKeys.toMutableList()
                reorderedKeys.removeAt(fromIndex)
                reorderedKeys.add(toIndex, key)
                persistBottomBarSelection(reorderedKeys)
            }

            SettingItemGroup(
                title = "底部导航栏",
                settingKey = APPEARANCE_SETTINGS_BOTTOM_BAR_SECTION_KEY,
                highlightedKey = settingKey,
                bringIntoViewRequester = requesterFor(APPEARANCE_SETTINGS_BOTTOM_BAR_SECTION_KEY),
            ) {
                val selectedBottomBarItemKeySet = selectedBottomBarItemKeys.value.toSet()
                val startDestinationItems = selectedBottomBarItemKeys.value.mapNotNull { key ->
                    bottomBarItemLabels[key]?.let { label -> key to label }
                }
                val orderedSettingItems = selectedBottomBarItemKeys.value.mapNotNull { key ->
                    bottomBarItemLabels[key]?.let { label -> key to label }
                } + allBottomBarItems.filter { it.first !in selectedBottomBarItemKeySet }

                SettingItem(
                    title = { Text("应用启动默认页面") },
                    description = { Text("仅可选择已在底部导航栏中显示的页面。") },
                    endAction = {
                        ExposedDropdownMenuBox(
                            expanded = startDestinationExpanded,
                            onExpandedChange = {
                                if (startDestinationItems.isNotEmpty()) {
                                    startDestinationExpanded = it
                                }
                            },
                        ) {
                            OutlinedTextField(
                                value = startDestinationItems.find { it.first == startDestinationKey }?.second ?: "主页",
                                onValueChange = {},
                                readOnly = true,
                                enabled = startDestinationItems.isNotEmpty(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = startDestinationExpanded) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .width(160.dp)
                                    .testTag(APPEARANCE_SETTINGS_START_DESTINATION_TAG),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            )
                            ExposedDropdownMenu(
                                expanded = startDestinationExpanded,
                                onDismissRequest = { startDestinationExpanded = false },
                            ) {
                                startDestinationItems.forEach { (key, label) ->
                                    DropdownMenuItem(
                                        modifier = Modifier.testTag("appearanceSettings:startDestination:option:$key"),
                                        text = { Text(label) },
                                        onClick = {
                                            startDestinationKey = key
                                            settings.putString(START_DESTINATION_PREFERENCE_KEY, key)
                                            startDestinationExpanded = false
                                            userMessages.showShortMessage("已设置启动页：$label，重启后生效")
                                        },
                                    )
                                }
                            }
                        }
                    },
                )

                SettingItem(
                    title = { Text("选择要在底部栏显示的页面") },
                    description = {
                        Text("建议选择 3-5 项，可用箭头调整显示和滑动顺序。")
                    },
                    bottomAction = {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    8.dp +
                                        bottomBarSettingItemHeight * orderedSettingItems.size +
                                        bottomBarSettingItemSpacing * (orderedSettingItems.size - 1).coerceAtLeast(0),
                                ).padding(top = 8.dp),
                            userScrollEnabled = false,
                            verticalArrangement = Arrangement.spacedBy(bottomBarSettingItemSpacing),
                        ) {
                            items(
                                items = orderedSettingItems,
                                key = { it.first },
                            ) { (key, label) ->
                                val isChecked = selectedBottomBarItemKeys.value.contains(key)
                                val selectedIndex = selectedBottomBarItemKeys.value.indexOf(key)
                                val candidateOrderKeys = if (isChecked) {
                                    selectedBottomBarItemKeys.value.filter { it != key }
                                } else {
                                    selectedBottomBarItemKeys.value + key
                                }
                                val isEnabled = key != Account.name

                                Row(
                                    modifier = Modifier
                                        .animateItem(
                                            fadeInSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                            fadeOutSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                            placementSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                        ).testTag("appearanceSettings:bottomBar:item:$key")
                                        .fillMaxWidth()
                                        .height(bottomBarSettingItemHeight)
                                        .clickable(enabled = isEnabled) {
                                            when {
                                                isChecked && selectedBottomBarItemKeys.value.size <= 3 -> {
                                                    userMessages.showShortMessage("至少保留3项")
                                                }

                                                !isChecked -> {
                                                    platformBottomBarItemLimit
                                                        ?.takeIf { selectedBottomBarItemKeys.value.size >= it }
                                                        ?.let { maximum ->
                                                            userMessages.showShortMessage("最多选择${maximum}项")
                                                        }
                                                        ?: persistBottomBarSelection(candidateOrderKeys)
                                                }

                                                else -> persistBottomBarSelection(candidateOrderKeys)
                                            }
                                        },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = null,
                                            enabled = isEnabled,
                                        )
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = if (isEnabled) {
                                                MaterialTheme.colorScheme.onSurface
                                            } else {
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                            },
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.width(96.dp),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        if (isChecked) {
                                            IconButton(
                                                onClick = { moveBottomBarItem(key, -1) },
                                                enabled = selectedIndex > 0,
                                                modifier = Modifier.testTag("appearanceSettings:bottomBar:moveUp:$key"),
                                            ) {
                                                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "上移$label")
                                            }
                                            IconButton(
                                                onClick = { moveBottomBarItem(key, 1) },
                                                enabled = selectedIndex in 0 until selectedBottomBarItemKeys.value.lastIndex,
                                                modifier = Modifier.testTag("appearanceSettings:bottomBar:moveDown:$key"),
                                            ) {
                                                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "下移$label")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                )

                val collectionDirectBrowse = remember {
                    mutableStateOf(settings.getBoolean(COLLECTION_DIRECT_BROWSE_PREFERENCE_KEY, false))
                }
                SettingItemWithSwitch(
                    modifier = Modifier.testTag(APPEARANCE_SETTINGS_COLLECTION_DIRECT_BROWSE_TAG),
                    title = { Text("收藏直达浏览（测试）") },
                    description = {
                        Text("测试功能，请谨慎开启，可能存在问题。开启后支持收藏夹直览、顺序模式与随机模式，欢迎提交 Issue。")
                    },
                    checked = collectionDirectBrowse.value,
                    onCheckedChange = {
                        collectionDirectBrowse.value = it
                        settings.putBoolean(COLLECTION_DIRECT_BROWSE_PREFERENCE_KEY, it)
                    },
                    settingKey = COLLECTION_DIRECT_BROWSE_PREFERENCE_KEY,
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor(COLLECTION_DIRECT_BROWSE_PREFERENCE_KEY),
                )

                val tapToRefresh = remember { mutableStateOf(settings.getBoolean("bottomBarTapScrollToTop", true)) }
                SettingItemWithSwitch(
                    title = { Text("点击底部导航栏回到顶部/刷新") },
                    description = { Text("点击底部导航栏当前页面按钮回到顶部，已在顶部时则刷新页面。双击可直接刷新。") },
                    checked = tapToRefresh.value,
                    onCheckedChange = {
                        tapToRefresh.value = it
                        settings.putBoolean("bottomBarTapScrollToTop", it)
                    },
                )

                val autoHideBottomBar = remember { mutableStateOf(settings.getBoolean("autoHideBottomBar", false)) }
                SettingItemWithSwitch(
                    title = { Text("滚动时自动隐藏底部导航栏") },
                    description = { Text("上划时隐藏底部导航栏，下划时重新显示。") },
                    checked = autoHideBottomBar.value,
                    onCheckedChange = {
                        autoHideBottomBar.value = it
                        settings.putBoolean("autoHideBottomBar", it)
                    },
                )
            }

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

            if (isPageTurnSupported) {
                SettingItemGroup(
                    title = "电纸书翻页",
                    header = {
                        Text(
                            "在内容列表、正文、评论区和设置等可滚动页面生效，私聊除外。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    },
                ) {
                    var volumeKeyPageTurn by remember {
                        mutableStateOf(settings.getBoolean(PREF_VOLUME_KEY_PAGE_TURN, false))
                    }
                    SettingItemWithSwitch(
                        title = { Text("音量键翻页") },
                        description = { Text("短按翻页，长按跳到顶部或底部。") },
                        checked = volumeKeyPageTurn,
                        onCheckedChange = {
                            volumeKeyPageTurn = it
                            settings.putBoolean(PREF_VOLUME_KEY_PAGE_TURN, it)
                        },
                        settingKey = PREF_VOLUME_KEY_PAGE_TURN,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(PREF_VOLUME_KEY_PAGE_TURN),
                    )

                    var pageTurnSwitchAnswer by remember {
                        mutableStateOf(settings.getBoolean(PREF_PAGE_TURN_SWITCH_ANSWER, DEFAULT_PAGE_TURN_SWITCH_ANSWER))
                    }
                    SettingItemWithSwitch(
                        title = { Text("翻页切换回答") },
                        description = { Text("默认开启。在回答顶部继续上翻进入上一个回答，在底部继续下翻进入下一个回答。") },
                        checked = pageTurnSwitchAnswer,
                        onCheckedChange = {
                            pageTurnSwitchAnswer = it
                            settings.putBoolean(PREF_PAGE_TURN_SWITCH_ANSWER, it)
                        },
                        settingKey = PREF_PAGE_TURN_SWITCH_ANSWER,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(PREF_PAGE_TURN_SWITCH_ANSWER),
                    )

                    var showPageTurnFab by remember {
                        mutableStateOf(settings.getBoolean(PREF_SHOW_PAGE_TURN_FAB, false))
                    }
                    SettingItemWithSwitch(
                        title = { Text("显示翻页悬浮按钮") },
                        description = { Text("只在支持翻页的可滚动页面显示。") },
                        checked = showPageTurnFab,
                        onCheckedChange = {
                            showPageTurnFab = it
                            settings.putBoolean(PREF_SHOW_PAGE_TURN_FAB, it)
                        },
                        settingKey = PREF_SHOW_PAGE_TURN_FAB,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(PREF_SHOW_PAGE_TURN_FAB),
                    )

                    var pageTurnPercent by remember {
                        mutableIntStateOf(settings.getInt(PREF_PAGE_TURN_PERCENT, DEFAULT_PAGE_TURN_PERCENT))
                    }
                    SettingItem(
                        title = { Text("翻页距离") },
                        description = { Text("每次滚动可见区域的 $pageTurnPercent%。") },
                        bottomAction = {
                            Slider(
                                value = pageTurnPercent.toFloat(),
                                onValueChange = {
                                    pageTurnPercent = (it / 5).roundToInt() * 5
                                    settings.putInt(PREF_PAGE_TURN_PERCENT, pageTurnPercent)
                                },
                                valueRange = 50f..100f,
                                steps = 9,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            )
                        },
                        settingKey = PREF_PAGE_TURN_PERCENT,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(PREF_PAGE_TURN_PERCENT),
                    )

                    var showGuide by remember {
                        mutableStateOf(settings.getBoolean(PREF_SHOW_PAGE_TURN_GUIDE, DEFAULT_SHOW_PAGE_TURN_GUIDE))
                    }
                    SettingItemWithSwitch(
                        title = { Text("显示翻页位置线") },
                        description = { Text("翻页后标记上一页与下一页的重叠位置。") },
                        checked = showGuide,
                        onCheckedChange = {
                            showGuide = it
                            settings.putBoolean(PREF_SHOW_PAGE_TURN_GUIDE, it)
                        },
                        settingKey = PREF_SHOW_PAGE_TURN_GUIDE,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(PREF_SHOW_PAGE_TURN_GUIDE),
                    )

                    var showContentEndMarker by remember {
                        mutableStateOf(
                            settings.getBoolean(
                                PREF_SHOW_CONTENT_END_MARKER,
                                DEFAULT_SHOW_CONTENT_END_MARKER,
                            ),
                        )
                    }
                    SettingItemWithSwitch(
                        title = { Text("显示内容结束标记") },
                        description = { Text("方便电纸书用户确定内容结束了，再向下翻页将跳转下一个内容或无效果。") },
                        checked = showContentEndMarker,
                        onCheckedChange = {
                            showContentEndMarker = it
                            settings.putBoolean(PREF_SHOW_CONTENT_END_MARKER, it)
                        },
                        settingKey = PREF_SHOW_CONTENT_END_MARKER,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(PREF_SHOW_CONTENT_END_MARKER),
                    )
                }
            }

            // ── 交互 ────────────────────────────────────────────────────────────
            SettingItemGroup(
                title = "交互",
            ) {
                var shareActionExpanded by remember { mutableStateOf(false) }
                val shareActionMode = remember {
                    mutableStateOf(settings.getString("shareActionMode", "ask"))
                }
                val shareActionOptions = listOf(
                    "ask" to "询问",
                    "copy" to "复制链接",
                    "share" to "Android分享",
                )
                SettingItem(
                    title = { Text("分享操作") },
                    description = { Text("点击分享按钮时的默认行为。") },
                    settingKey = "shareAction",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("shareAction"),
                    endAction = {
                        ExposedDropdownMenuBox(
                            expanded = shareActionExpanded,
                            onExpandedChange = { shareActionExpanded = it },
                        ) {
                            OutlinedTextField(
                                value = shareActionOptions.find { it.first == shareActionMode.value }?.second ?: "询问",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shareActionExpanded) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .width(160.dp),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            )
                            ExposedDropdownMenu(
                                expanded = shareActionExpanded,
                                onDismissRequest = { shareActionExpanded = false },
                            ) {
                                shareActionOptions.forEach { (mode, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            shareActionMode.value = mode
                                            settings.putString("shareActionMode", mode)
                                            shareActionExpanded = false
                                            userMessages.showShortMessage("已设置为：$label")
                                        },
                                    )
                                }
                            }
                        }
                    },
                )
            }

            // ── 搜索 ────────────────────────────────────────────────────────────
            SettingItemGroup(
                title = "搜索",
            ) {
                val showSearchHotSearch = remember { mutableStateOf(settings.getBoolean("showSearchHotSearch", true)) }
                SettingItemWithSwitch(
                    title = { Text("搜索界面显示热搜") },
                    description = { Text("在搜索界面空白时显示知乎热搜关键词。") },
                    checked = showSearchHotSearch.value,
                    onCheckedChange = {
                        showSearchHotSearch.value = it
                        settings.putBoolean("showSearchHotSearch", it)
                    },
                    settingKey = "showSearchHotSearch",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("showSearchHotSearch"),
                )
                val showSearchHistory = remember { mutableStateOf(settings.getBoolean("showSearchHistory", true)) }
                SettingItemWithSwitch(
                    title = { Text("记录并显示搜索历史") },
                    description = { Text("在搜索界面显示最近搜索过的关键词，关闭后不再记录新的搜索。") },
                    checked = showSearchHistory.value,
                    onCheckedChange = {
                        showSearchHistory.value = it
                        settings.putBoolean("showSearchHistory", it)
                    },
                    settingKey = "showSearchHistory",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("showSearchHistory"),
                )
            }

            // ── 导航 ────────────────────────────────────────────────────────────
            SettingItemGroup(
                title = "技术性导航设置",
            ) {
                val useCustomNavHost = remember { mutableStateOf(settings.getBoolean("use_custom_nav_host", true)) }
                SettingItemWithSwitch(
                    title = { Text("使用自定义导航") },
                    description = { Text("使用自定义导航替代系统默认的导航组件，可能部分提升国产手机上的操作手感，请视情况开启。") },
                    checked = useCustomNavHost.value,
                    onCheckedChange = {
                        useCustomNavHost.value = it
                        settings.putBoolean("use_custom_nav_host", it)
                        userMessages.showShortMessage("需要重启应用生效")
                    },
                    settingKey = "use_custom_nav_host",
                    highlightedKey = settingKey,
                    bringIntoViewRequester = requesterFor("use_custom_nav_host"),
                )

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
            // ── 123duo3 UI 改进 ─────────────────────────────────────────────────

            // 先声明所有子开关状态，以便主开关可以批量操作
            val duo3All = remember { mutableStateOf(settings.getBoolean("duo3_all", false)) }
            val duo3CardAppearance = remember { mutableStateOf(settings.getBoolean("duo3_card_appearance", false)) }
            val duo3CardLayout = remember { mutableStateOf(settings.getBoolean("duo3_card_layout", false)) }
            val duo3CardLargeTitle = remember {
                mutableStateOf(settings.getBoolean(DUO3_CARD_LARGE_TITLE_PREFERENCE_KEY, true))
            }
            val duo3ArticleBar = remember { mutableStateOf(settings.getBoolean("duo3_article_bar", false)) }
            val duo3ArticleActions = remember { mutableStateOf(settings.getBoolean("duo3_article_actions", false)) }
            val duo3TiqianMarkdown = remember {
                mutableStateOf(settings.getBoolean(DUO3_TIQIAN_MARKDOWN_PREFERENCE_KEY, false))
            }
            val duo3TiqianMathFont = remember {
                mutableStateOf(settings.getString(DUO3_TIQIAN_MATH_FONT_PREFERENCE_KEY, "lete"))
            }

            fun enableAllSubs() {
                if (isTiqianMarkdownRendererAvailable) {
                    settings.putBoolean(DUO3_TIQIAN_MARKDOWN_PREFERENCE_KEY, true)
                    duo3TiqianMarkdown.value = true
                }
                settings.putBoolean("duo3_home_account", true)
                settings.putBoolean("duo3_card_appearance", true)
                settings.putBoolean("duo3_card_layout", true)
                settings.putBoolean("duo3_article_bar", true)
                settings.putBoolean("duo3_article_actions", true)
                settings.putBoolean("showRefreshFab", false)
                settings.putBoolean("buttonSkipAnswer", false)
                duo3HomeAccount.value = true
                duo3CardAppearance.value = true
                duo3CardLayout.value = true
                duo3ArticleBar.value = true
                duo3ArticleActions.value = true
                // 123duo3 改动中会移除 FAB。
                showRefreshFab.value = false
                buttonSkipAnswer.value = false
                val updatedSelection = if (Home.name !in selectedBottomBarItemKeys.value) {
                    selectedBottomBarItemKeys.value + Account.name
                } else {
                    selectedBottomBarItemKeys.value
                }
                persistBottomBarSelection(updatedSelection, duo3HomeAccountEnabled = true)
            }

            fun disableAllSubs() {
                if (isTiqianMarkdownRendererAvailable) {
                    settings.putBoolean(DUO3_TIQIAN_MARKDOWN_PREFERENCE_KEY, false)
                    duo3TiqianMarkdown.value = false
                }
                settings.putBoolean("duo3_home_account", false)
                settings.putBoolean("duo3_card_appearance", false)
                settings.putBoolean("duo3_card_layout", false)
                settings.putBoolean("duo3_article_bar", false)
                settings.putBoolean("duo3_article_actions", false)
                duo3HomeAccount.value = false
                duo3CardAppearance.value = false
                duo3CardLayout.value = false
                duo3ArticleBar.value = false
                duo3ArticleActions.value = false
                persistBottomBarSelection(selectedBottomBarItemKeys.value, duo3HomeAccountEnabled = false)
            }

            SettingItemGroup(
                title = "123Duo3 的 UI/UX 改进（beta）",
                settingKey = "123Duo3",
                highlightedKey = settingKey,
                bringIntoViewRequester = requesterFor("123Duo3"),
                header = {
                    SettingItemOverall(
                        title = { Text("启用所有修改并关闭浮动按钮") },
                        checked = duo3All.value,
                        onCheckedChange = {
                            duo3All.value = it
                            settings.putBoolean("duo3_all", it)
                            if (it) {
                                enableAllSubs()
                            } else {
                                disableAllSubs()
                            }
                        },
                    )
                },
                footer = {
                    Text(
                        text = buildAnnotatedString {
                            append("以上设置项可能随时更改，或并入主线。\n欢迎")
                            withLink(LinkAnnotation.Url("https://github.com/zly2006/zhihu-plus-plus/issues")) {
                                withStyle(
                                    MaterialTheme.typography.bodyMedium
                                        .copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            textDecoration = TextDecoration.Underline,
                                            fontWeight = FontWeight.Medium,
                                        ).toSpanStyle(),
                                ) {
                                    append("提交 Issue")
                                }
                            }
                            append(" 讨论本次 UI/UX 修改和反馈问题。")
                        },
                    )
                },
            ) {
                if (isTiqianMarkdownRendererAvailable) {
                    SettingItemWithSwitch(
                        modifier = Modifier.testTag(APPEARANCE_SETTINGS_TIQIAN_MARKDOWN_TAG),
                        title = { TiqianBrandTitle(prefix = "正文：使用", suffix = " Markdown 渲染器") },
                        description = { Text("使用「提椠」段落书写器排版正文：段落两端对齐，改进中西混排间距、代码、表格、公式与脚注等样式，接近纸质书的排版效果。作用于文章、想法与问题详情。实验功能。") },
                        checked = duo3TiqianMarkdown.value,
                        onCheckedChange = {
                            duo3TiqianMarkdown.value = it
                            settings.putBoolean(DUO3_TIQIAN_MARKDOWN_PREFERENCE_KEY, it)
                        },
                        settingKey = DUO3_TIQIAN_MARKDOWN_PREFERENCE_KEY,
                        highlightedKey = settingKey,
                        bringIntoViewRequester = requesterFor(DUO3_TIQIAN_MARKDOWN_PREFERENCE_KEY),
                    )
                    AnimatedVisibility(visible = duo3TiqianMarkdown.value) {
                        SettingItemWithSwitch(
                            title = { Text("正文：使用非衬线数学字体") },
                            description = { Text("默认公式使用非衬线的 Lete Sans Math；关闭后改用衬线的 STIX Two Math。") },
                            checked = duo3TiqianMathFont.value == "lete",
                            onCheckedChange = {
                                val fontId = if (it) "lete" else "stix"
                                duo3TiqianMathFont.value = fontId
                                settings.putString(DUO3_TIQIAN_MATH_FONT_PREFERENCE_KEY, fontId)
                            },
                        )
                    }
                }

                SettingItemWithSwitch(
                    title = { Text("主页：账号入口迁移至顶部头像") },
                    description = { Text("搜索栏样式变更；点击头像弹出账号与设置；「历史」入口可挪入账号设置页。") },
                    checked = duo3HomeAccount.value,
                    onCheckedChange = {
                        duo3HomeAccount.value = it
                        settings.putBoolean("duo3_home_account", it)
                        val updatedSelection = if (it && Home.name !in selectedBottomBarItemKeys.value) {
                            selectedBottomBarItemKeys.value + Account.name
                        } else {
                            selectedBottomBarItemKeys.value
                        }
                        persistBottomBarSelection(updatedSelection, it)
                    },
                )

                SettingItemWithSwitch(
                    title = { Text("信息流卡片：外观更改") },
                    description = { Text("卡片圆角增大，移除阴影；修改背景与卡片颜色。") },
                    checked = duo3CardAppearance.value,
                    onCheckedChange = {
                        duo3CardAppearance.value = it
                        settings.putBoolean("duo3_card_appearance", it)
                    },
                )

                SettingItemWithSwitch(
                    title = { Text("信息流卡片：更改内容排版") },
                    description = { Text("作者移至底部；图片不与底部小字并列；摘要最多显示 4 行（原 3 行），规范字体样式。") },
                    checked = duo3CardLayout.value,
                    onCheckedChange = {
                        duo3CardLayout.value = it
                        settings.putBoolean("duo3_card_layout", it)
                    },
                )

                AnimatedVisibility(visible = duo3CardLayout.value) {
                    SettingItemWithSwitch(
                        title = { Text("信息流卡片：使用更大的标题字体") },
                        description = { Text("默认启用；关闭后标题会缩小一档。") },
                        checked = duo3CardLargeTitle.value,
                        onCheckedChange = {
                            duo3CardLargeTitle.value = it
                            settings.putBoolean(DUO3_CARD_LARGE_TITLE_PREFERENCE_KEY, it)
                        },
                    )
                }

                SettingItemWithSwitch(
                    title = { Text("文章阅读页：更改整体顶/底栏框架") },
                    description = { Text("更改标题栏样式；优化顶/底栏隐藏逻辑。") },
                    checked = duo3ArticleBar.value,
                    onCheckedChange = {
                        duo3ArticleBar.value = it
                        settings.putBoolean("duo3_article_bar", it)
                    },
                )

                AnimatedVisibility(visible = duo3ArticleBar.value) {
                    SettingItemWithSwitch(
                        title = { Text("文章阅读页：更改操作栏样式") },
                        description = { Text("底栏操作按钮用药丸包裹；分隔赞同/反对按钮并添加动画。") },
                        checked = duo3ArticleActions.value,
                        onCheckedChange = {
                            duo3ArticleActions.value = it
                            settings.putBoolean("duo3_article_actions", it)
                        },
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
