package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TradeEntity
import com.example.ui.theme.*

@Composable
fun TradeJournalSection(
    trades: List<TradeEntity>,
    onAddTrade: (strategy: String, optionType: String, strike: Double, entryPrice: Double, exitPrice: Double, quantity: Int, reason: String, notes: String) -> Unit,
    onDeleteTrade: (TradeEntity) -> Unit,
    onExportCsv: () -> String
) {
    val context = LocalContext.current
    var isAddingTrade by remember { mutableStateOf(false) }

    var strategyInput by remember { mutableStateOf("Credit Spread") }
    var optionTypeInput by remember { mutableStateOf("CE") }
    var strikeInput by remember { mutableStateOf("24500") }
    var entryPriceInput by remember { mutableStateOf("85.0") }
    var exitPriceInput by remember { mutableStateOf("25.0") }
    var quantityInput by remember { mutableStateOf("150") }
    var reasonInput by remember { mutableStateOf("R1 resistance rejection + high CE OI buildup") }
    var notesInput by remember { mutableStateOf("Booked 70% decay before 3 PM") }

    val totalTrades = trades.size
    val winTrades = trades.count { it.pnl > 0 }
    val lossTrades = trades.count { it.pnl < 0 }
    val winRate = if (totalTrades > 0) (winTrades.toDouble() / totalTrades * 100.0) else 0.0
    val totalPnl = trades.sumOf { it.pnl }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Journal Summary Performance Metrics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TRADE JOURNAL PERFORMANCE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighlight,
                            letterSpacing = 0.5.sp
                        )

                        // Export CSV
                        IconButton(
                            onClick = {
                                val csv = onExportCsv()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, csv)
                                    type = "text/csv"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export Trade Journal CSV"))
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Export", tint = AccentCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox("Total P&L", "₹${"%,.0f".format(totalPnl)}", Modifier.weight(1f), if (totalPnl >= 0) BullishGreen else BearishRed)
                        MetricBox("Win Rate", "${"%.1f".format(winRate)}%", Modifier.weight(1f), if (winRate >= 60) BullishGreen else CautionYellow)
                        MetricBox("Trades", "$totalTrades (W:$winTrades L:$lossTrades)", Modifier.weight(1f), AccentCyan)
                    }
                }
            }
        }

        // Action Button: Add New Trade
        item {
            Button(
                onClick = { isAddingTrade = !isAddingTrade },
                modifier = Modifier.fillMaxWidth().height(42.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isAddingTrade) Color(0xFF334155) else AccentBlue)
            ) {
                Icon(
                    imageVector = if (isAddingTrade) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Toggle"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAddingTrade) "Cancel New Trade Form" else "Log Executed Option Trade",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Add Trade Form
        if (isAddingTrade) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = TerminalElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentCyan)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "RECORD NEW TRADE RECORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RiskInputField("Strategy", strategyInput, Modifier.weight(1.5f)) { strategyInput = it }
                            RiskInputField("CE/PE", optionTypeInput, Modifier.weight(1f)) { optionTypeInput = it }
                            RiskInputField("Strike", strikeInput, Modifier.weight(1.2f)) { strikeInput = it }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RiskInputField("Entry ₹", entryPriceInput, Modifier.weight(1f)) { entryPriceInput = it }
                            RiskInputField("Exit ₹", exitPriceInput, Modifier.weight(1f)) { exitPriceInput = it }
                            RiskInputField("Quantity", quantityInput, Modifier.weight(1f)) { quantityInput = it }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        RiskInputField("Reason for Entry", reasonInput, Modifier.fillMaxWidth()) { reasonInput = it }
                        Spacer(modifier = Modifier.height(8.dp))
                        RiskInputField("Notes / Expiry Context", notesInput, Modifier.fillMaxWidth()) { notesInput = it }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val s = strikeInput.toDoubleOrNull() ?: 24500.0
                                val ep = entryPriceInput.toDoubleOrNull() ?: 85.0
                                val xp = exitPriceInput.toDoubleOrNull() ?: 25.0
                                val qty = quantityInput.toIntOrNull() ?: 150
                                onAddTrade(strategyInput, optionTypeInput, s, ep, xp, qty, reasonInput, notesInput)
                                isAddingTrade = false
                            },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BullishGreen),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Save Trade to Persistent Database", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // List of Trades
        if (trades.isEmpty()) {
            item {
                Surface(
                    color = TerminalCard,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Book, contentDescription = "Empty", tint = TextMuted, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No trades recorded yet. Tap above to log your first trade setup.", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        } else {
            items(trades, key = { it.id }) { trade ->
                TradeItemCard(trade = trade, onDelete = { onDeleteTrade(trade) })
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun TradeItemCard(trade: TradeEntity, onDelete: () -> Unit) {
    val isWin = trade.pnl >= 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isWin) BullishGreenSubtle else BearishRedSubtle,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${trade.optionType} - ${trade.strike.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWin) BullishGreen else BearishRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = trade.strategy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${if (isWin) "+" else ""}₹${"%,.0f".format(trade.pnl)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isWin) BullishGreen else BearishRed,
                        fontFamily = FontFamily.Monospace
                    )
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).padding(start = 4.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Entry: ₹${trade.entryPrice}  Exit: ₹${trade.exitPrice}  Qty: ${trade.quantity}",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = trade.tradeDate,
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }

            if (trade.reason.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reason: ${trade.reason}",
                    fontSize = 10.sp,
                    color = TextHighlight
                )
            }
        }
    }
}
