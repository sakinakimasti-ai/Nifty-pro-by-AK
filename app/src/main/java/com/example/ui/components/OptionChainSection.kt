package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OptionChainData
import com.example.data.model.OptionStrike
import com.example.ui.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun OptionChainSection(
    chain: OptionChainData,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedExpiry: String,
    onExpirySelected: (String) -> Unit,
    onExportCsv: () -> String
) {
    val context = LocalContext.current
    var showExpiryDropdown by remember { mutableStateOf(false) }
    val horizontalScrollState = rememberScrollState()

    // Filter strikes based on search query
    val filteredStrikes = remember(chain.strikes, searchQuery) {
        if (searchQuery.isBlank()) {
            chain.strikes
        } else {
            chain.strikes.filter {
                it.strikePrice.toInt().toString().contains(searchQuery.trim())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
    ) {
        // Controls Row: Search, Expiry Filter & Export CSV Button
        Surface(
            color = TerminalSurface,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Strike Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search strike...", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("strike_search_input"),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = TerminalCardBorder,
                        focusedContainerColor = TerminalCard,
                        unfocusedContainerColor = TerminalCard
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(6.dp)
                )

                // Expiry Dropdown
                Box {
                    Surface(
                        color = TerminalCard,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder),
                        modifier = Modifier
                            .height(44.dp)
                            .clickable { showExpiryDropdown = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Expiry",
                                tint = AccentCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (selectedExpiry.isNotEmpty()) selectedExpiry else "Expiry",
                                fontSize = 11.sp,
                                color = TextHighlight,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showExpiryDropdown,
                        onDismissRequest = { showExpiryDropdown = false },
                        modifier = Modifier.background(TerminalElevated)
                    ) {
                        chain.expiryDates.forEach { exp ->
                            DropdownMenuItem(
                                text = { Text(text = exp, fontSize = 12.sp, color = TextPrimary) },
                                onClick = {
                                    showExpiryDropdown = false
                                    onExpirySelected(exp)
                                }
                            )
                        }
                    }
                }

                // Share / Export CSV Button
                IconButton(
                    onClick = {
                        val csvData = onExportCsv()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, csvData)
                            type = "text/csv"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Export NIFTY Option Chain CSV"))
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(TerminalCard, RoundedCornerShape(6.dp))
                        .border(1.dp, TerminalCardBorder, RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Export CSV",
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Legends row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(CautionYellow, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("ATM", fontSize = 9.sp, color = CautionYellow, fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(BullishGreen, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("ITM Call", fontSize = 9.sp, color = TextMuted)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(BearishRed, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("ITM Put", fontSize = 9.sp, color = TextMuted)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFF332000),
                    shape = RoundedCornerShape(2.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, CautionYellow)
                ) {
                    Text("★ MAX OI", fontSize = 8.sp, color = CautionYellow, modifier = Modifier.padding(2.dp))
                }
            }
        }

        // Horizontal scroll container holding header and rows
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
        ) {
            Column(modifier = Modifier.width(1340.dp)) {
                // Table Super Header (CALLS vs STRIKE vs PUTS)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .border(width = 0.5.dp, color = TerminalCardBorder)
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "CALL OPTIONS (CE)",
                        modifier = Modifier.width(600.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = BullishGreen,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "STRIKE",
                        modifier = Modifier.width(140.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = CautionYellow,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "PUT OPTIONS (PE)",
                        modifier = Modifier.width(600.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = BearishRed,
                        letterSpacing = 1.sp
                    )
                }

                // Table Column Names
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalElevated)
                        .border(width = 0.5.dp, color = TerminalCardBorder)
                        .padding(vertical = 6.dp)
                ) {
                    // CE Columns
                    HeaderCell("Delta", 45.dp)
                    HeaderCell("Theta", 45.dp)
                    HeaderCell("IV%", 45.dp)
                    HeaderCell("Chg OI", 65.dp)
                    HeaderCell("OI", 65.dp)
                    HeaderCell("Volume", 65.dp)
                    HeaderCell("Bid", 55.dp)
                    HeaderCell("Ask", 55.dp)
                    HeaderCell("Chg", 55.dp)
                    HeaderCell("CE LTP", 65.dp, color = BullishGreen)

                    // Strike & ATM Distance Column
                    HeaderCell("Distance", 55.dp, color = TextMuted)
                    HeaderCell("Strike", 85.dp, color = CautionYellow)

                    // PE Columns
                    HeaderCell("PE LTP", 65.dp, color = BearishRed)
                    HeaderCell("Chg", 55.dp)
                    HeaderCell("Bid", 55.dp)
                    HeaderCell("Ask", 55.dp)
                    HeaderCell("Volume", 65.dp)
                    HeaderCell("OI", 65.dp)
                    HeaderCell("Chg OI", 65.dp)
                    HeaderCell("IV%", 45.dp)
                    HeaderCell("Theta", 45.dp)
                    HeaderCell("Delta", 45.dp)
                }

                // Strikes Rows
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredStrikes, key = { it.strikePrice }) { strike ->
                        OptionStrikeRow(strike = strike, spotPrice = chain.underlyingValue)
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    color: Color = TextSecondary
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        textAlign = TextAlign.Center
    )
}

@Composable
fun OptionStrikeRow(strike: OptionStrike, spotPrice: Double) {
    val isCeItm = strike.strikePrice < spotPrice
    val isPeItm = strike.strikePrice > spotPrice

    val rowBg = when {
        strike.isAtm -> AtmHighlightBg
        else -> TerminalSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .border(
                width = if (strike.isAtm) 1.5.dp else 0.5.dp,
                color = if (strike.isAtm) AtmHighlightBorder else Color(0xFF1E2838)
            )
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // CE Side (ITM tinted green)
        Box(
            modifier = Modifier
                .width(555.dp)
                .background(if (isCeItm) ItmCeBg else OtmBg)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TableCell("${strike.ce.delta}", 45.dp, TextSecondary)
                TableCell("${strike.ce.theta}", 45.dp, TextMuted)
                TableCell("${strike.ce.iv}", 45.dp, CautionYellow)
                TableCell(
                    text = "${(strike.ce.changeInOI / 1000).toInt()}k",
                    width = 65.dp,
                    color = if (strike.ce.changeInOI >= 0) BullishGreen else BearishRed,
                    highlightBadge = if (strike.isHighestCeChgOi) "★" else null
                )
                TableCell(
                    text = "${(strike.ce.openInterest / 100000.0).roundToInt() / 10.0}L",
                    width = 65.dp,
                    color = TextPrimary,
                    highlightBadge = if (strike.isHighestCeOi) "MAX" else null
                )
                TableCell("${(strike.ce.volume / 1000).toInt()}k", 65.dp, TextMuted)
                TableCell("${strike.ce.bid}", 55.dp, TextSecondary)
                TableCell("${strike.ce.ask}", 55.dp, TextSecondary)
                TableCell(
                    text = "${if (strike.ce.change >= 0) "+" else ""}${"%.1f".format(strike.ce.change)}",
                    width = 55.dp,
                    color = if (strike.ce.change >= 0) BullishGreen else BearishRed
                )
                TableCell(
                    text = "₹${strike.ce.ltp}",
                    width = 65.dp,
                    color = BullishGreen,
                    isBold = true
                )
            }
        }

        // Center Strike & ATM Distance Box
        TableCell(
            text = "${if (strike.atmDistance >= 0) "+" else ""}${strike.atmDistance.toInt()}",
            width = 55.dp,
            color = TextMuted
        )

        Surface(
            modifier = Modifier.width(85.dp),
            color = if (strike.isAtm) Color(0xFF4A3800) else Color(0xFF1A2333),
            shape = RoundedCornerShape(4.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (strike.isAtm) CautionYellow else TerminalCardBorder
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${strike.strikePrice.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (strike.isAtm) CautionYellow else TextHighlight,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
                if (strike.isAtm) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "ATM",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = CautionYellow
                    )
                }
            }
        }

        // PE Side (ITM tinted red)
        Box(
            modifier = Modifier
                .width(555.dp)
                .background(if (isPeItm) ItmPeBg else OtmBg)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TableCell(
                    text = "₹${strike.pe.ltp}",
                    width = 65.dp,
                    color = BearishRed,
                    isBold = true
                )
                TableCell(
                    text = "${if (strike.pe.change >= 0) "+" else ""}${"%.1f".format(strike.pe.change)}",
                    width = 55.dp,
                    color = if (strike.pe.change >= 0) BullishGreen else BearishRed
                )
                TableCell("${strike.pe.bid}", 55.dp, TextSecondary)
                TableCell("${strike.pe.ask}", 55.dp, TextSecondary)
                TableCell("${(strike.pe.volume / 1000).toInt()}k", 65.dp, TextMuted)
                TableCell(
                    text = "${(strike.pe.openInterest / 100000.0).roundToInt() / 10.0}L",
                    width = 65.dp,
                    color = TextPrimary,
                    highlightBadge = if (strike.isHighestPeOi) "MAX" else null
                )
                TableCell(
                    text = "${(strike.pe.changeInOI / 1000).toInt()}k",
                    width = 65.dp,
                    color = if (strike.pe.changeInOI >= 0) BullishGreen else BearishRed,
                    highlightBadge = if (strike.isHighestPeChgOi) "★" else null
                )
                TableCell("${strike.pe.iv}", 45.dp, CautionYellow)
                TableCell("${strike.pe.theta}", 45.dp, TextMuted)
                TableCell("${strike.pe.delta}", 45.dp, TextSecondary)
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    color: Color = TextPrimary,
    isBold: Boolean = false,
    highlightBadge: String? = null
) {
    Row(
        modifier = Modifier.width(width),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal,
            color = color,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )
        if (highlightBadge != null) {
            Spacer(modifier = Modifier.width(2.dp))
            Surface(
                color = CautionYellow,
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(
                    text = highlightBadge,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.5.dp)
                )
            }
        }
    }
}
