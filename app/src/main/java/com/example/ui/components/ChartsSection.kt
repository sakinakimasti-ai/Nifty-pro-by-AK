package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OptionChainData
import com.example.data.model.SpotData
import com.example.ui.theme.*
import kotlin.math.max
import kotlin.math.min

@Composable
fun ChartsSection(
    spot: SpotData,
    chain: OptionChainData
) {
    var selectedTimeframe by remember { mutableStateOf("5m") }
    val timeframes = listOf("1m", "5m", "15m", "30m", "1D")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Timeframe selector bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                timeframes.forEach { tf ->
                    Surface(
                        color = if (selectedTimeframe == tf) AccentBlue else TerminalCard,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTimeframe = tf }
                    ) {
                        Text(
                            text = tf,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTimeframe == tf) Color.White else TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Chart 1: NIFTY 50 Intraday Spot Price Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NIFTY 50 INTRADAY PRICE TREND",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextHighlight
                            )
                            Text(
                                text = "Current: ₹${spot.spotPrice}  |  VWAP: ₹${"%.1f".format(spot.vwap)}",
                                fontSize = 10.sp,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Surface(
                            color = if (spot.change >= 0) BullishGreenSubtle else BearishRedSubtle,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${if (spot.change >= 0) "+" else ""}${"%.2f".format(spot.changePercent)}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (spot.change >= 0) BullishGreen else BearishRed,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Canvas Line Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(TerminalSurface, RoundedCornerShape(6.dp))
                            .border(0.5.dp, TerminalCardBorder, RoundedCornerShape(6.dp))
                    ) {
                        val candles = spot.candleHistory
                        if (candles.size > 2) {
                            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                                val w = size.width
                                val h = size.height

                                val prices = candles.map { it.price }
                                val minP = prices.minOrNull() ?: 24200.0
                                val maxP = prices.maxOrNull() ?: 24400.0
                                val pRange = max(10.0, maxP - minP)

                                // Draw subtle horizontal gridlines
                                for (i in 1..3) {
                                    val y = (h / 4f) * i
                                    drawLine(
                                        color = Color(0xFF1E2838),
                                        start = Offset(0f, y),
                                        end = Offset(w, y),
                                        strokeWidth = 1f
                                    )
                                }

                                val points = mutableListOf<Offset>()
                                val stepX = w / (prices.size - 1)
                                for (i in prices.indices) {
                                    val px = i * stepX
                                    val py = h - ((prices[i] - minP) / pRange * h).toFloat()
                                    points.add(Offset(px, py))
                                }

                                // Create line path
                                val path = Path().apply {
                                    moveTo(points[0].x, points[0].y)
                                    for (i in 1 until points.size) {
                                        lineTo(points[i].x, points[i].y)
                                    }
                                }

                                // Gradient fill under the path
                                val fillPath = Path().apply {
                                    addPath(path)
                                    lineTo(points.last().x, h)
                                    lineTo(points.first().x, h)
                                    close()
                                }

                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            (if (spot.change >= 0) BullishGreen else BearishRed).copy(alpha = 0.35f),
                                            Color.Transparent
                                        ),
                                        startY = 0f,
                                        endY = h
                                    )
                                )

                                drawPath(
                                    path = path,
                                    color = if (spot.change >= 0) BullishGreen else BearishRed,
                                    style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )

                                // Current price dot
                                drawCircle(
                                    color = TextHighlight,
                                    radius = 4f,
                                    center = points.last()
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chart 2: CE vs PE Open Interest Distribution Bar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OPEN INTEREST (OI) DISTRIBUTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighlight
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(BearishRed, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("CE OI (Resistance)", fontSize = 9.sp, color = TextMuted)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(BullishGreen, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("PE OI (Support)", fontSize = 9.sp, color = TextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dual Bar Chart for +/- 5 strikes around ATM
                    val displayStrikes = chain.strikes.take(11)
                    if (displayStrikes.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(TerminalSurface, RoundedCornerShape(6.dp))
                                .border(0.5.dp, TerminalCardBorder, RoundedCornerShape(6.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 12.dp)) {
                                val w = size.width
                                val h = size.height

                                val maxOi = max(
                                    displayStrikes.maxOf { it.ce.openInterest },
                                    displayStrikes.maxOf { it.pe.openInterest }
                                ).coerceAtLeast(100000L).toFloat()

                                val numGroups = displayStrikes.size
                                val groupWidth = w / numGroups
                                val barWidth = (groupWidth * 0.35f).coerceAtMost(14f)

                                for (i in displayStrikes.indices) {
                                    val item = displayStrikes[i]
                                    val groupCenterX = i * groupWidth + groupWidth / 2f

                                    // CE OI bar (Red)
                                    val ceHeight = (item.ce.openInterest / maxOi) * (h - 10f)
                                    drawRoundRect(
                                        color = BearishRed,
                                        topLeft = Offset(groupCenterX - barWidth - 1f, h - ceHeight),
                                        size = Size(barWidth, ceHeight),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                                    )

                                    // PE OI bar (Green)
                                    val peHeight = (item.pe.openInterest / maxOi) * (h - 10f)
                                    drawRoundRect(
                                        color = BullishGreen,
                                        topLeft = Offset(groupCenterX + 1f, h - peHeight),
                                        size = Size(barWidth, peHeight),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Chart 3: Option Strategy Payoff Diagram (Defined Risk Iron Condor)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "STRATEGY P&L PAYOFF CURVE (DEFINED RISK)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextHighlight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exemplifies Iron Condor / Credit Spread profit plateau and capped risk wings.",
                        fontSize = 10.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(TerminalSurface, RoundedCornerShape(6.dp))
                            .border(0.5.dp, TerminalCardBorder, RoundedCornerShape(6.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                            val w = size.width
                            val h = size.height

                            val zeroY = h * 0.60f

                            // Draw Zero P&L reference line
                            drawLine(
                                color = TextMuted.copy(alpha = 0.5f),
                                start = Offset(0f, zeroY),
                                end = Offset(w, zeroY),
                                strokeWidth = 1f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                            )

                            // Draw plateau payoff curve
                            val plateauY = h * 0.22f
                            val wingBottomY = h * 0.88f

                            val p1 = Offset(0f, wingBottomY)
                            val p2 = Offset(w * 0.22f, wingBottomY)
                            val p3 = Offset(w * 0.35f, plateauY)
                            val p4 = Offset(w * 0.65f, plateauY)
                            val p5 = Offset(w * 0.78f, wingBottomY)
                            val p6 = Offset(w, wingBottomY)

                            val payoffPath = Path().apply {
                                moveTo(p1.x, p1.y)
                                lineTo(p2.x, p2.y)
                                lineTo(p3.x, p3.y)
                                lineTo(p4.x, p4.y)
                                lineTo(p5.x, p5.y)
                                lineTo(p6.x, p6.y)
                            }

                            drawPath(
                                path = payoffPath,
                                color = AccentCyan,
                                style = Stroke(width = 3f, cap = StrokeCap.Round)
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
