package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ExpiryCalendarSection() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Calendar", tint = AccentCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NIFTY 50 CONTRACT EXPIRY SCHEDULE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighlight,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ExpiryItemRow("Current Weekly Expiry", "14-Nov-2026 (Thursday)", "2 Days Remaining", BullishGreen)
                    ExpiryItemRow("Next Weekly Expiry", "21-Nov-2026 (Thursday)", "9 Days Remaining", AccentCyan)
                    ExpiryItemRow("Monthly Contract Expiry", "28-Nov-2026 (Thursday)", "16 Days Remaining", CautionYellow)
                    ExpiryItemRow("Quarterly Far Expiry", "26-Dec-2026 (Thursday)", "44 Days Remaining", TextMuted)
                }
            }
        }

        // Theta Decay Curve & Expiry Warning
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2638)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = "Theta", tint = CautionYellow)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EXPIRY DAY THETA ACCELERATION NOTICE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CautionYellow
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Option extrinsic value experiences non-linear exponential decay during the last 48 hours of contract lifecycle. On expiry day (Thursday), OTM strikes decay to zero rapidly, but Gamma risk surges after 1:30 PM. Professional sellers recommend taking profits when 70-80% of maximum potential credit is harvested.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // NSE Clearing & Settlement Specifications
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = TerminalCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = TextSecondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EXCHANGE CONTRACT SPECIFICATIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextHighlight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SpecRow("Underlying Index", "NIFTY 50 Index (NSE)")
                    SpecRow("Trading Hours", "09:15 AM to 03:30 PM IST")
                    SpecRow("Contract Lot Size", "75 Units (Standard Market Lot)")
                    SpecRow("Settlement Type", "Cash Settled at Weighted Average Price (3:00 - 3:30 PM)")
                    SpecRow("Tick Size", "0.05 INR")
                    SpecRow("Strike Interval", "50 Points")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ExpiryItemRow(title: String, date: String, remaining: String, color: Color) {
    Surface(
        color = TerminalElevated,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 10.sp, color = TextMuted)
                Text(text = date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextHighlight)
            }
            Surface(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = remaining,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun SpecRow(key: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = key, fontSize = 10.sp, color = TextSecondary)
        Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextHighlight, fontFamily = FontFamily.Monospace)
    }
}
