package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SellingCandidate
import com.example.data.model.TradeSetup
import com.example.ui.theme.*

@Composable
fun ScannerSection(
    ceCandidate: SellingCandidate?,
    peCandidate: SellingCandidate?,
    ironCondor: TradeSetup?,
    creditSpread: TradeSetup?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Analytical Disclaimer Header
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Disclaimer",
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ANALYTICAL SETUP – NOT AN EXECUTED TRADE. Multi-factor algorithmic scanner based on Delta (0.12-0.22), IV percentiles, Open Interest walls, and VWAP structure.",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // CARD 1: BEST CE SELLING CANDIDATE
        item {
            if (ceCandidate != null) {
                DetailedSellingCard(candidate = ceCandidate)
            } else {
                NoCandidateCard(type = "CE")
            }
        }

        // CARD 2: BEST PE SELLING CANDIDATE
        item {
            if (peCandidate != null) {
                DetailedSellingCard(candidate = peCandidate)
            } else {
                NoCandidateCard(type = "PE")
            }
        }

        // Iron Condor Setup Card
        item {
            if (ironCondor != null) {
                MultiLegSetupCard(setup = ironCondor)
            }
        }

        // Credit Spread Setup Card
        item {
            if (creditSpread != null) {
                MultiLegSetupCard(setup = creditSpread)
            }
        }

        // Bottom space
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DetailedSellingCard(candidate: SellingCandidate) {
    val isCe = candidate.type == "CE"
    val accentColor = if (isCe) BearishRed else BullishGreen

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCe) Color(0xFF3D2323) else Color(0xFF1D3D28))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isCe) BearishRedSubtle else BullishGreenSubtle,
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor)
                    ) {
                        Text(
                            text = "CARD ${if (isCe) "1" else "2"}: BEST ${candidate.type} SELLING CANDIDATE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // AI Suitability Rating
                Surface(
                    color = when (candidate.rating) {
                        "STRONG CANDIDATE" -> BullishGreenSubtle
                        "GOOD CANDIDATE" -> CautionYellowSubtle
                        else -> Color(0xFF262D3D)
                    },
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (candidate.rating) {
                            "STRONG CANDIDATE" -> BullishGreen
                            "GOOD CANDIDATE" -> CautionYellow
                            else -> TextMuted
                        }
                    )
                ) {
                    Text(
                        text = "${candidate.rating} (${candidate.suitabilityScore}/100)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = when (candidate.rating) {
                            "STRONG CANDIDATE" -> BullishGreen
                            "GOOD CANDIDATE" -> CautionYellow
                            else -> TextSecondary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Strike & Premium Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Recommended Short Strike", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "${candidate.strike.toInt()} ${candidate.type}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextHighlight,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Current Premium (LTP)", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "₹${candidate.premium}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = BullishGreen,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Bid: ${candidate.bid}  Ask: ${candidate.ask}",
                        fontSize = 9.sp,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quantitative Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetricChip("Delta", "${candidate.delta}", Modifier.weight(1f))
                MetricChip("IV", "${candidate.iv}%", Modifier.weight(1f))
                MetricChip("Open Int", "${(candidate.oi / 100000.0).toInt()}L", Modifier.weight(1f))
                MetricChip("Chg in OI", "+${(candidate.changeInOI / 1000).toInt()}k", Modifier.weight(1f), BullishGreen)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Suggested Hedge Leg Box
            Surface(
                color = TerminalElevated,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🛡️ Suggested Hedge Strike (Defined Risk):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentCyan
                        )
                        Text(
                            text = "Buy ${candidate.suggestedHedgeStrike.toInt()} ${candidate.type} @ ₹${candidate.hedgePremium}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighlight,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Estimated Spread Net Credit:", fontSize = 10.sp, color = TextSecondary)
                        Text(
                            text = "₹${candidate.netCredit} pts (₹${(candidate.netCredit * 75).toInt()} / Lot)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullishGreen,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Execution & Invalidation Rules
            ExecutionRow("Entry Condition", candidate.entryCondition)
            ExecutionRow("Invalidation Level", "Spot breach of ₹${candidate.invalidationLevel.toInt()}")
            ExecutionRow("Stop-Loss Method", candidate.stopLossMethod)
            ExecutionRow("Profit Booking", candidate.profitBooking)

            Spacer(modifier = Modifier.height(10.dp))

            // AI Selection Reason Explanation
            Surface(
                color = Color(0xFF141F30),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3250)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "WHY THIS STRIKE WAS SELECTED:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CautionYellow,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = candidate.selectionReason,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transparent AI Score Breakdown Accordion / Chips
            Text(
                text = "Scoring Breakdown (Max 100):",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                candidate.scoreBreakdown.entries.take(4).forEach { (k, v) ->
                    Surface(
                        color = TerminalElevated,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = k.take(8), fontSize = 8.sp, color = TextMuted)
                            Text(
                                text = "+$v",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullishGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricChip(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = TextPrimary) {
    Surface(
        modifier = modifier,
        color = TerminalElevated,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 9.sp, color = TextMuted)
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun ExecutionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "• $label:", fontSize = 10.sp, color = TextSecondary)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextHighlight,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
fun MultiLegSetupCard(setup: TradeSetup) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                    text = setup.title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentCyan
                )
                Surface(
                    color = if (setup.isNoTrade) BearishRedSubtle else BullishGreenSubtle,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (setup.isNoTrade) "NO TRADE" else "ACTIVE SETUP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (setup.isNoTrade) BearishRed else BullishGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Legs: ${setup.strategy}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextHighlight
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Max Risk: ${setup.maxRisk}", fontSize = 10.sp, color = BearishRed, fontFamily = FontFamily.Monospace)
                Text(text = "Max Reward: ${setup.maxReward}", fontSize = 10.sp, color = BullishGreen, fontFamily = FontFamily.Monospace)
                Text(text = "R:R ${setup.rrRatio}", fontSize = 10.sp, color = CautionYellow, fontFamily = FontFamily.Monospace)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = setup.reasoning,
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun NoCandidateCard(type: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.Block, contentDescription = "None", tint = CautionYellow)
            Spacer(modifier = Modifier.height(6.dp))
            Text("INSUFFICIENT DATA – NO RELIABLE $type SETUP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CautionYellow)
        }
    }
}
