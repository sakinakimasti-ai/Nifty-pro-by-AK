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
import com.example.data.model.OptionChainData
import com.example.data.model.SpotData
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun AnalyticsSection(
    spot: SpotData,
    chain: OptionChainData
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section A: PCR (PUT-CALL RATIO) ANALYSIS
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
                            text = "PUT-CALL RATIO (PCR) ANALYSIS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighlight,
                            letterSpacing = 0.5.sp
                        )
                        Surface(
                            color = if (chain.pcrOi >= 1.0) BullishGreenSubtle else BearishRedSubtle,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (chain.pcrOi >= 1.25) "STRONG BULLISH" else if (chain.pcrOi >= 1.0) "MILD BULLISH" else "BEARISH BIAS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (chain.pcrOi >= 1.0) BullishGreen else BearishRed,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBox("OI PCR", "${"%.2f".format(chain.pcrOi)}", Modifier.weight(1f), if (chain.pcrOi >= 1.0) BullishGreen else BearishRed)
                        MetricBox("Volume PCR", "${"%.2f".format(chain.pcrVolume)}", Modifier.weight(1f), AccentCyan)
                        MetricBox("Total PE OI", "${(chain.totalPeOi / 1000000.0).roundToInt() / 10.0}M", Modifier.weight(1f), BullishGreen)
                        MetricBox("Total CE OI", "${(chain.totalCeOi / 1000000.0).roundToInt() / 10.0}M", Modifier.weight(1f), BearishRed)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Interpretation: PCR of ${"%.2f".format(chain.pcrOi)} indicates that Put writers are ${if (chain.pcrOi >= 1.0) "more aggressive than Call writers, providing solid support on pullbacks" else "cautious, with Call writers capping upside momentum"}. In options selling, a healthy PCR between 1.05 and 1.30 creates an ideal range for selling OTM Puts and Iron Condors.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section B: MAX PAIN ANALYSIS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MAX PAIN THEORY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighlight,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Max Pain Strike", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = "₹${chain.maxPain.toInt()}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = AccentCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val diff = chain.maxPain - spot.spotPrice
                            Text(text = "Distance from Current Spot", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = "${if (diff >= 0) "+" else ""}${"%.1f".format(diff)} pts",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (diff >= 0) BullishGreen else BearishRed,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Max Pain Strike represents the price point at which option sellers (writers) collectively experience the minimum monetary payout upon expiry. NIFTY tends to gravitate toward Max Pain (₹${chain.maxPain.toInt()}) as expiry approaches, especially on weekly expiry Thursdays.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section C: OPEN INTEREST (OI) BUILDUP MATRIX
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "OPEN INTEREST (OI) BUILDUP MATRIX",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighlight,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BuildupCard("Long Buildup", "Price ▲ + OI ▲", "Bullish strength", BullishGreen, Modifier.weight(1f))
                        BuildupCard("Short Buildup", "Price ▼ + OI ▲", "Bearish supply", BearishRed, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BuildupCard("Short Covering", "Price ▲ + OI ▼", "Short squeeze", AccentCyan, Modifier.weight(1f))
                        BuildupCard("Long Unwinding", "Price ▼ + OI ▼", "Bull exit", CautionYellow, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: OI interpretation is probabilistic. In Indian markets, institutional participation (FII/DII) primarily acts on the selling side of the option chain.",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Section D: GREEKS IMPLICATIONS FOR SELLERS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GREEKS IMPLICATIONS FOR OPTION SELLERS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighlight,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    GreekRow("Delta (Δ)", "Measures directional risk. Option sellers target 0.12 - 0.20 delta (80-88% statistical probability of expiring OTM).")
                    GreekRow("Theta (θ)", "Option seller's best friend. Measures daily time decay. Theta decay accelerates exponentially within the last 3-4 days to expiry.")
                    GreekRow("Gamma (Γ)", "Measures rate of delta change. High gamma on expiry day causes rapid delta swings; option sellers must beware of afternoon gamma spikes.")
                    GreekRow("Vega (ν)", "Measures sensitivity to volatility. Option sellers benefit when IV drops (IV Crush post economic events or RBI announcements).")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun MetricBox(label: String, value: String, modifier: Modifier, valueColor: Color) {
    Surface(
        modifier = modifier,
        color = TerminalElevated,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 9.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun BuildupCard(title: String, condition: String, meaning: String, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = TerminalElevated,
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = condition, fontSize = 9.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
            Text(text = meaning, fontSize = 9.sp, color = TextMuted)
        }
    }
}

@Composable
fun GreekRow(name: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
        Text(text = desc, fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
    }
}
