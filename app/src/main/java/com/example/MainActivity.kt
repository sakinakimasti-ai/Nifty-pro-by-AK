package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DashboardTab
import com.example.ui.NiftyViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: NiftyViewModel = viewModel()
                NiftyAppContent(viewModel)
            }
        }
    }
}

@Composable
fun NiftyAppContent(viewModel: NiftyViewModel) {
    val spotData by viewModel.spotData.collectAsStateWithLifecycle()
    val optionChain by viewModel.optionChain.collectAsStateWithLifecycle()
    val direction by viewModel.marketDirection.collectAsStateWithLifecycle()
    val ceCandidate by viewModel.ceCandidate.collectAsStateWithLifecycle()
    val peCandidate by viewModel.peCandidate.collectAsStateWithLifecycle()
    val ironCondor by viewModel.ironCondor.collectAsStateWithLifecycle()
    val creditSpread by viewModel.creditSpread.collectAsStateWithLifecycle()
    val riskAnalysis by viewModel.riskAnalysis.collectAsStateWithLifecycle()
    val newsEvents by viewModel.newsEvents.collectAsStateWithLifecycle()
    val currentProvider by viewModel.currentProvider.collectAsStateWithLifecycle()
    val refreshInterval by viewModel.refreshIntervalSeconds.collectAsStateWithLifecycle()
    val isAutoRefresh by viewModel.isAutoRefreshActive.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val sessionNotice by viewModel.sessionNotice.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val strikeSearch by viewModel.strikeSearch.collectAsStateWithLifecycle()
    val selectedExpiry by viewModel.selectedExpiry.collectAsStateWithLifecycle()
    val trades by viewModel.trades.collectAsStateWithLifecycle()
    val analystMessages by viewModel.analystMessages.collectAsStateWithLifecycle()

    // BackHandler to return to Dashboard if on any sub-tab
    BackHandler(enabled = activeTab != DashboardTab.DASHBOARD) {
        viewModel.selectTab(DashboardTab.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg),
        topBar = {
            TopHeaderBar(
                spot = spotData,
                currentProvider = currentProvider,
                refreshInterval = refreshInterval,
                isAutoRefresh = isAutoRefresh,
                isRefreshing = isRefreshing,
                sessionNotice = sessionNotice,
                onProviderSelected = { viewModel.setProvider(it) },
                onIntervalSelected = { viewModel.setRefreshInterval(it) },
                onAutoRefreshToggled = { viewModel.toggleAutoRefresh(it) },
                onManualRefresh = { viewModel.triggerManualRefresh() }
            )
        },
        bottomBar = {
            NavigationTabRow(
                currentTab = activeTab,
                onSelectTab = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TerminalBg)
        ) {
            when (activeTab) {
                DashboardTab.DASHBOARD -> DashboardSection(
                    spot = spotData,
                    chain = optionChain,
                    direction = direction,
                    ceCandidate = ceCandidate,
                    peCandidate = peCandidate,
                    ironCondor = ironCondor,
                    onNavigateTab = { viewModel.selectTab(it) }
                )
                DashboardTab.OPTION_CHAIN -> OptionChainSection(
                    chain = optionChain,
                    searchQuery = strikeSearch,
                    onSearchChange = { viewModel.updateStrikeSearch(it) },
                    selectedExpiry = selectedExpiry,
                    onExpirySelected = { viewModel.updateSelectedExpiry(it) },
                    onExportCsv = { viewModel.generateOptionChainCsv() }
                )
                DashboardTab.SELLING_SCANNER -> ScannerSection(
                    ceCandidate = ceCandidate,
                    peCandidate = peCandidate,
                    ironCondor = ironCondor,
                    creditSpread = creditSpread
                )
                DashboardTab.ANALYTICS -> AnalyticsSection(
                    spot = spotData,
                    chain = optionChain
                )
                DashboardTab.RISK_CALCULATOR -> RiskCalculatorSection(
                    risk = riskAnalysis,
                    onCalculate = { c, rp, l, sp, hp, sl, tp ->
                        viewModel.updateRiskInputs(c, rp, l, sp, hp, sl, tp)
                    }
                )
                DashboardTab.CHARTS -> ChartsSection(
                    spot = spotData,
                    chain = optionChain
                )
                DashboardTab.NEWS_EVENTS -> NewsEventsSection(
                    events = newsEvents
                )
                DashboardTab.TRADE_JOURNAL -> TradeJournalSection(
                    trades = trades,
                    onAddTrade = { strat, type, strike, entry, exit, qty, reason, notes ->
                        viewModel.addTradeToJournal(strat, type, strike, entry, exit, qty, reason, notes)
                    },
                    onDeleteTrade = { viewModel.deleteTrade(it) },
                    onExportCsv = { viewModel.generateTradeJournalCsv() }
                )
                DashboardTab.AI_ANALYST -> AiAnalystSection(
                    messages = analystMessages,
                    onAskQuestion = { viewModel.askAnalyst(it) }
                )
                DashboardTab.EXPIRY_CALENDAR -> ExpiryCalendarSection()
            }
        }
    }
}

@Composable
fun NavigationTabRow(
    currentTab: DashboardTab,
    onSelectTab: (DashboardTab) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = currentTab.ordinal,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("navigation_tab_row"),
        containerColor = TerminalSurface,
        contentColor = TextHighlight,
        edgePadding = 8.dp,
        divider = {
            HorizontalDivider(color = TerminalCardBorder, thickness = 1.dp)
        }
    ) {
        DashboardTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            Tab(
                selected = isSelected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = getTabIcon(tab),
                        contentDescription = tab.title,
                        tint = if (isSelected) AccentCyan else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) AccentCyan else TextSecondary
                    )
                }
            )
        }
    }
}

fun getTabIcon(tab: DashboardTab): ImageVector {
    return when (tab) {
        DashboardTab.DASHBOARD -> Icons.Default.Dashboard
        DashboardTab.OPTION_CHAIN -> Icons.Default.TableChart
        DashboardTab.SELLING_SCANNER -> Icons.Default.Radar
        DashboardTab.ANALYTICS -> Icons.Default.QueryStats
        DashboardTab.RISK_CALCULATOR -> Icons.Default.Calculate
        DashboardTab.CHARTS -> Icons.Default.ShowChart
        DashboardTab.NEWS_EVENTS -> Icons.Default.Newspaper
        DashboardTab.TRADE_JOURNAL -> Icons.Default.Book
        DashboardTab.AI_ANALYST -> Icons.Default.Psychology
        DashboardTab.EXPIRY_CALENDAR -> Icons.Default.CalendarToday
    }
}
