package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NewsEvent
import com.example.ui.theme.*

@Composable
fun NewsEventsSection(events: List<NewsEvent>) {
    var selectedCategory by remember { mutableStateOf("ALL") }

    val filteredEvents = remember(events, selectedCategory) {
        if (selectedCategory == "ALL") events else events.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // AI Event Risk Engine Alert Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261D00)),
                border = androidx.compose.foundation.BorderStroke(1.dp, CautionYellow)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alert",
                                tint = CautionYellow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI EVENT RISK ENGINE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = CautionYellow,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Surface(
                            color = CautionYellow,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "YELLOW ALERT: HIGH EVENT MONITORING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Event Risk on Option Selling: Key central bank rate decisions (RBI MPC / US FOMC) and CPI prints cause sharp IV expansions before announcements followed by violent IV crush afterwards. Maintain defined-risk hedged credit spreads and avoid overnight naked short gamma.",
                        fontSize = 11.sp,
                        color = Color(0xFFFDE68A),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Filter chips (ALL / INDIAN / GLOBAL)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "INDIAN", "GLOBAL").forEach { cat ->
                    Surface(
                        color = if (selectedCategory == cat) AccentBlue else TerminalCard,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        TextButton(onClick = { selectedCategory = cat }) {
                            Text(
                                text = if (cat == "ALL") "All Events" else "$cat Events",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedCategory == cat) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // News Events Cards
        items(filteredEvents, key = { it.id }) { event ->
            EventCard(event = event)
        }

        // Disclaimer
        item {
            Text(
                text = "Disclaimer: News and calendar event impact is inherently uncertain and should not be treated as guaranteed market direction. Never invent economic figures. Sources: Official RBI, MOSPI, US Federal Reserve and NSE circulars.",
                fontSize = 10.sp,
                color = TextMuted,
                lineHeight = 14.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun EventCard(event: NewsEvent) {
    val importanceColor = when (event.importance) {
        "CRITICAL" -> BearishRed
        "HIGH" -> CautionYellow
        "MEDIUM" -> AccentCyan
        else -> TextMuted
    }

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
                Surface(
                    color = importanceColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, importanceColor)
                ) {
                    Text(
                        text = "${event.importance} IMPACT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = importanceColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${event.date} • ${event.timeIst}",
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextHighlight
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forecast / Previous / Actual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = TerminalElevated,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(text = "Previous", fontSize = 8.sp, color = TextMuted)
                        Text(text = event.previous, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    color = TerminalElevated,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(text = "Forecast", fontSize = 8.sp, color = TextMuted)
                        Text(text = event.forecast, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CautionYellow)
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    color = TerminalElevated,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(text = "Actual", fontSize = 8.sp, color = TextMuted)
                        Text(text = event.actual, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Market Impact: ${event.marketImpact}",
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}
