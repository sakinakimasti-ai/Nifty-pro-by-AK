package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DataProviderType
import com.example.data.model.MarketStatus
import com.example.data.model.SpotData
import com.example.ui.theme.*

@Composable
fun TopHeaderBar(
    spot: SpotData,
    currentProvider: DataProviderType,
    refreshInterval: Int,
    isAutoRefresh: Boolean,
    isRefreshing: Boolean,
    sessionNotice: String,
    onProviderSelected: (DataProviderType) -> Unit,
    onIntervalSelected: (Int) -> Unit,
    onAutoRefreshToggled: (Boolean) -> Unit,
    onManualRefresh: () -> Unit
) {
    var showProviderMenu by remember { mutableStateOf(false) }
    var showIntervalMenu by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TerminalSurface)
            .border(width = 1.dp, color = TerminalCardBorder)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Top row: App title & Live status indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "NIFTY 50",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextHighlight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFF1E3A8A),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "OPTION PRO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = spot.timestamp,
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Provider status pill
            Surface(
                color = when {
                    spot.providerStatus.contains("DEMO") -> Color(0xFF1E293B)
                    spot.providerStatus.contains("Fallback") -> Color(0xFF332A00)
                    else -> Color(0xFF063318)
                },
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (spot.providerStatus.contains("DEMO")) AccentBlue else BullishGreenDark
                )
            ) {
                Text(
                    text = spot.providerStatus,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (spot.providerStatus.contains("DEMO")) AccentCyan else BullishGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Price, Day Change, India VIX and Market Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Live Price
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "₹${"%,.2f".format(spot.spotPrice)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (spot.change >= 0) BullishGreen else BearishRed,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${if (spot.change >= 0) "+" else ""}${"%.2f".format(spot.change)} (${if (spot.change >= 0) "+" else ""}${"%.2f".format(spot.changePercent)}%)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (spot.change >= 0) BullishGreen else BearishRed,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VWAP: ₹${"%,.1f".format(spot.vwap)}  |  H: ${"%,.0f".format(spot.dayHigh)}  L: ${"%,.0f".format(spot.dayLow)}",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Market Status & VIX Box
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = when (spot.marketStatus) {
                        MarketStatus.OPEN -> BullishGreenSubtle
                        MarketStatus.PRE_OPEN -> CautionYellowSubtle
                        MarketStatus.CLOSED -> Color(0xFF261D1D)
                    },
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (spot.marketStatus) {
                            MarketStatus.OPEN -> BullishGreen
                            MarketStatus.PRE_OPEN -> CautionYellow
                            MarketStatus.CLOSED -> BearishRed
                        }
                    )
                ) {
                    Text(
                        text = spot.marketStatus.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (spot.marketStatus) {
                            MarketStatus.OPEN -> BullishGreen
                            MarketStatus.PRE_OPEN -> CautionYellow
                            MarketStatus.CLOSED -> BearishRed
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "India VIX: ", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "${spot.indiaVix}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (spot.indiaVix > 16.0) BearishRed else CautionYellow
                    )
                }
            }
        }

        // Session notice banner if active (e.g. "Refreshing NSE session...")
        if (sessionNotice.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = Color(0xFF26200B),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CautionYellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Notice",
                        tint = CautionYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = sessionNotice,
                        fontSize = 11.sp,
                        color = CautionYellow,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Controls bar: Provider Dropdown, Auto-refresh Interval & Refresh Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Provider selector dropdown button
            Box {
                Surface(
                    color = TerminalCard,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder),
                    modifier = Modifier
                        .clickable { showProviderMenu = true }
                        .testTag("provider_dropdown")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentProvider) {
                                DataProviderType.YAHOO_LIVE -> "Yahoo Finance (LIVE)"
                                DataProviderType.NSE_PUBLIC -> "NSE Public (Session)"
                                DataProviderType.DEMO -> "Demo Mode"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Provider",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showProviderMenu,
                    onDismissRequest = { showProviderMenu = false },
                    modifier = Modifier.background(TerminalElevated)
                ) {
                    DataProviderType.values().forEach { provider ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = provider.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = if (currentProvider == provider) FontWeight.Bold else FontWeight.Normal,
                                        color = if (currentProvider == provider) AccentCyan else TextPrimary
                                    )
                                    Text(
                                        text = provider.badgeLabel,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            },
                            onClick = {
                                showProviderMenu = false
                                onProviderSelected(provider)
                            },
                            trailingIcon = {
                                if (currentProvider == provider) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = AccentCyan
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Right side: Auto-refresh selector and Refresh button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Auto Refresh Interval Button
                Box {
                    Surface(
                        color = TerminalCard,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder),
                        modifier = Modifier.clickable { showIntervalMenu = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAutoRefresh) "${refreshInterval}s Auto" else "Off",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isAutoRefresh) BullishGreen else TextMuted
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Interval",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showIntervalMenu,
                        onDismissRequest = { showIntervalMenu = false },
                        modifier = Modifier.background(TerminalElevated)
                    ) {
                        listOf(5, 10, 15, 30, 60).forEach { sec ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (sec == 5) "5 seconds (Default)" else "$sec seconds",
                                        fontSize = 12.sp,
                                        color = if (isAutoRefresh && refreshInterval == sec) AccentCyan else TextPrimary
                                    )
                                },
                                onClick = {
                                    showIntervalMenu = false
                                    onAutoRefreshToggled(true)
                                    onIntervalSelected(sec)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(text = "Turn Off Auto-Refresh", fontSize = 12.sp, color = BearishRed) },
                            onClick = {
                                showIntervalMenu = false
                                onAutoRefreshToggled(false)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Manual refresh button
                IconButton(
                    onClick = onManualRefresh,
                    modifier = Modifier
                        .size(32.dp)
                        .background(TerminalCard, RoundedCornerShape(6.dp))
                        .border(1.dp, TerminalCardBorder, RoundedCornerShape(6.dp))
                        .testTag("manual_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Data",
                        tint = AccentCyan,
                        modifier = Modifier
                            .size(18.dp)
                            .then(if (isRefreshing) Modifier.rotate(angle) else Modifier)
                    )
                }
            }
        }
    }
}
