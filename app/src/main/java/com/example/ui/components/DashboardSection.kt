package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.DashboardTab
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun DashboardSection(
    spot: SpotData,
    chain: OptionChainData,
    direction: MarketDirection,
    ceCandidate: SellingCandidate?,
    peCandidate: SellingCandidate?,
    ironCondor: TradeSetup?,
    onNavigateTab: (DashboardTab) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Market Direction & Trend Strength Score Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Analysis",
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MARKET DIRECTION ANALYSIS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighlight,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Status Pill
                    Surface(
                        color = when {
                            direction.status.contains("BULLISH") -> BullishGreenSubtle
                            direction.status.contains("BEARISH") -> BearishRedSubtle
                            direction.status.contains("VOLATILITY") -> CautionYellowSubtle
                            else -> Color(0xFF1E3A8A)
                        },
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when {
                                direction.status.contains("BULLISH") -> BullishGreen
                                direction.status.contains("BEARISH") -> BearishRed
                                direction.status.contains("VOLATILITY") -> CautionYellow
                                else -> AccentBlue
                            }
                        )
                    ) {
                        Text(
                            text = direction.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                direction.status.contains("BULLISH") -> BullishGreen
                                direction.status.contains("BEARISH") -> BearishRed
                                direction.status.contains("VOLATILITY") -> CautionYellow
                                else -> AccentCyan
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Trend Strength Bar (0 to 100)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Trend Strength Score",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${direction.strengthScore} / 100",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (direction.strengthScore >= 60) BullishGreen else if (direction.strengthScore <= 40) BearishRed else CautionYellow,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { (direction.strengthScore / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (direction.strengthScore >= 60) BullishGreen else if (direction.strengthScore <= 40) BearishRed else CautionYellow,
                    trackColor = Color(0xFF1F293D)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Support & Resistance 4-box grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // S2
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = TerminalElevated,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A2E))
                    ) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Strong S2", fontSize = 9.sp, color = TextMuted)
                            Text(
                                text = "₹${direction.strongSupport.toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullishGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // S1
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = TerminalElevated,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A2E))
                    ) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Imm S1", fontSize = 9.sp, color = TextMuted)
                            Text(
                                text = "₹${direction.immediateSupport.toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BullishGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // R1
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = TerminalElevated,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A1E1E))
                    ) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Imm R1", fontSize = 9.sp, color = TextMuted)
                            Text(
                                text = "₹${direction.immediateResistance.toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BearishRed,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // R2
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = TerminalElevated,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A1E1E))
                    ) {
                        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Strong R2", fontSize = 9.sp, color = TextMuted)
                            Text(
                                text = "₹${direction.strongResistance.toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BearishRed,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Structure & Reason
                Text(
                    text = direction.marketStructure,
                    fontSize = 11.sp,
                    color = AccentCyan,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = direction.reason,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Key Quantitative Meters: PCR & Max Pain
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // PCR Box
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateTab(DashboardTab.ANALYTICS) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "OI PCR", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "${"%.2f".format(chain.pcrOi)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (chain.pcrOi > 1.1) BullishGreen else if (chain.pcrOi < 0.85) BearishRed else CautionYellow,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (chain.pcrOi > 1.1) "Put Writing Heavy" else if (chain.pcrOi < 0.85) "Call Writing Heavy" else "Balanced",
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                }
            }

            // Max Pain Box
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateTab(DashboardTab.ANALYTICS) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "MAX PAIN", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "₹${chain.maxPain.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    val dist = chain.maxPain - spot.spotPrice
                    Text(
                        text = "Dist: ${if (dist >= 0) "+" else ""}${dist.roundToInt()} pts",
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                }
            }

            // ATM IV Box
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateTab(DashboardTab.EXPIRY_CALENDAR) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "ATM IV", fontSize = 10.sp, color = TextMuted)
                    Text(
                        text = "${chain.atmIv}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CautionYellow,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Theta Burn Active",
                        fontSize = 9.sp,
                        color = BullishGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI Selling Candidates Quick Cards
        Text(
            text = "AI OPTION SELLING RECOMMENDATIONS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextHighlight,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Best CE Candidate Quick Card
        if (ceCandidate != null) {
            SellingCandidateBannerCard(
                candidate = ceCandidate,
                onClick = { onNavigateTab(DashboardTab.SELLING_SCANNER) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Best PE Candidate Quick Card
        if (peCandidate != null) {
            SellingCandidateBannerCard(
                candidate = peCandidate,
                onClick = { onNavigateTab(DashboardTab.SELLING_SCANNER) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Iron Condor Setup Card
        if (ironCondor != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTab(DashboardTab.SELLING_SCANNER) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "IRON CONDOR",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color(0xFF1E3A8A),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Defined Risk",
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Short ${ironCondor.shortStrike} | Hedge: ${ironCondor.hedgeStrike}",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = ironCondor.entryPriceRange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullishGreen,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "R:R ${ironCondor.rrRatio}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SellingCandidateBannerCard(
    candidate: SellingCandidate,
    onClick: () -> Unit
) {
    val isCe = candidate.type == "CE"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = TerminalCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCe) Color(0xFF2A3A56) else Color(0xFF3A2A46)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isCe) BearishRedSubtle else BullishGreenSubtle,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "BEST ${candidate.type} CANDIDATE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCe) BearishRed else BullishGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${candidate.strike.toInt()} ${candidate.type}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextHighlight,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // AI Suitability score badge
                Surface(
                    color = when {
                        candidate.suitabilityScore >= 80 -> BullishGreenSubtle
                        candidate.suitabilityScore >= 65 -> CautionYellowSubtle
                        else -> Color(0xFF242C3D)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "AI Score: ${candidate.suitabilityScore}/100",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            candidate.suitabilityScore >= 80 -> BullishGreen
                            candidate.suitabilityScore >= 65 -> CautionYellow
                            else -> TextSecondary
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "LTP: ₹${candidate.premium}  |  Delta: ${candidate.delta}  |  IV: ${candidate.iv}%",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Net: ₹${candidate.netCredit}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BullishGreen,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hedge: Buy ${candidate.suggestedHedgeStrike.toInt()} ${candidate.type} @ ₹${candidate.hedgePremium} (Protected Spread)",
                fontSize = 10.sp,
                color = AccentCyan
            )
        }
    }
}
