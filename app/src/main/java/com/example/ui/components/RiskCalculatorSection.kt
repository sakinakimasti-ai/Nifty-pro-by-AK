package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RiskAnalysis
import com.example.ui.theme.*

@Composable
fun RiskCalculatorSection(
    risk: RiskAnalysis,
    onCalculate: (capital: Double, riskPercent: Double, lots: Int, shortPrem: Double, hedgePrem: Double, slPercent: Double, targetPercent: Double) -> Unit
) {
    var capitalStr by remember(risk.capital) { mutableStateOf(risk.capital.toInt().toString()) }
    var riskPercentStr by remember(risk.riskPercent) { mutableStateOf(risk.riskPercent.toString()) }
    var lotsStr by remember(risk.lots) { mutableStateOf(risk.lots.toString()) }
    var shortPremStr by remember(risk.shortPremium) { mutableStateOf(risk.shortPremium.toString()) }
    var hedgePremStr by remember(risk.hedgePremium) { mutableStateOf(risk.hedgePremium.toString()) }
    var slPercentStr by remember(risk.stopLossPercent) { mutableStateOf(risk.stopLossPercent.toInt().toString()) }
    var targetPercentStr by remember(risk.targetPercent) { mutableStateOf(risk.targetPercent.toInt().toString()) }

    fun triggerCalc() {
        val c = capitalStr.toDoubleOrNull() ?: risk.capital
        val rp = riskPercentStr.toDoubleOrNull() ?: risk.riskPercent
        val l = lotsStr.toIntOrNull() ?: risk.lots
        val sp = shortPremStr.toDoubleOrNull() ?: risk.shortPremium
        val hp = hedgePremStr.toDoubleOrNull() ?: risk.hedgePremium
        val sl = slPercentStr.toDoubleOrNull() ?: risk.stopLossPercent
        val tp = targetPercentStr.toDoubleOrNull() ?: risk.targetPercent
        onCalculate(c, rp, l, sp, hp, sl, tp)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Over-risk limit warning if applicable
        if (risk.isOverRiskLimit) {
            item {
                Surface(
                    color = Color(0xFF331414),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BearishRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Risk Warning", tint = BearishRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RISK LIMIT BREACH: Potential max loss (₹${"%,.0f".format(risk.maxPotentialLoss)}) exceeds configured risk limit of ${risk.riskPercent}% of capital (₹${"%,.0f".format(risk.capital * risk.riskPercent / 100.0)}). Reduce lot size or buy a closer hedge leg.",
                            fontSize = 11.sp,
                            color = BearishRed,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Calculator Inputs Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "RISK MANAGEMENT PARAMETERS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighlight,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RiskInputField("Trading Capital (₹)", capitalStr, Modifier.weight(1f)) {
                            capitalStr = it
                            triggerCalc()
                        }
                        RiskInputField("Max Risk %", riskPercentStr, Modifier.weight(1f)) {
                            riskPercentStr = it
                            triggerCalc()
                        }
                        RiskInputField("Lots (75 Qty)", lotsStr, Modifier.weight(1f)) {
                            lotsStr = it
                            triggerCalc()
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RiskInputField("Short Leg (₹)", shortPremStr, Modifier.weight(1f)) {
                            shortPremStr = it
                            triggerCalc()
                        }
                        RiskInputField("Hedge Leg (₹)", hedgePremStr, Modifier.weight(1f)) {
                            hedgePremStr = it
                            triggerCalc()
                        }
                        RiskInputField("Stop Loss %", slPercentStr, Modifier.weight(1f)) {
                            slPercentStr = it
                            triggerCalc()
                        }
                        RiskInputField("Target %", targetPercentStr, Modifier.weight(1f)) {
                            targetPercentStr = it
                            triggerCalc()
                        }
                    }
                }
            }
        }

        // Computed Risk & PnL Outcomes Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "POSITION RISK & PAYOFF METRICS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighlight,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox("Net Credit / Sh", "₹${"%.1f".format(risk.netCreditPoints)} pts", Modifier.weight(1f), BullishGreen)
                        MetricBox("Max Profit", "₹${"%,.0f".format(risk.maxPotentialProfit)}", Modifier.weight(1f), BullishGreen)
                        MetricBox("Max Loss", "₹${"%,.0f".format(risk.maxPotentialLoss)}", Modifier.weight(1f), BearishRed)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox("Est. Margin Req.", "₹${"%,.0f".format(risk.estimatedMargin)}", Modifier.weight(1f), AccentCyan)
                        MetricBox("Risk / Reward", risk.riskRewardRatio, Modifier.weight(1f), CautionYellow)
                        MetricBox("Brokerage & Taxes", "₹${"%.0f".format(risk.estimatedBrokerageTaxes)}", Modifier.weight(1f), TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = TerminalElevated,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Net Estimated Profit (After Charges):", fontSize = 11.sp, color = TextPrimary)
                            Text(
                                text = "₹${"%,.0f".format(risk.netEstimatedPnl)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = BullishGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun RiskInputField(label: String, value: String, modifier: Modifier, onValueChange: (String) -> Unit) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 9.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(42.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 11.sp,
                color = TextHighlight,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = TerminalCardBorder,
                focusedContainerColor = TerminalElevated,
                unfocusedContainerColor = TerminalElevated
            ),
            shape = RoundedCornerShape(6.dp)
        )
    }
}
