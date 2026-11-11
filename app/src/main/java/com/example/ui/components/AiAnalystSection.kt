package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AnalystMessage
import com.example.ui.theme.*

@Composable
fun AiAnalystSection(
    messages: List<AnalystMessage>,
    onAskQuestion: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    val quickQuestions = listOf(
        "Aaj market bullish hai ya bearish?",
        "CE selling better hai ya PE selling?",
        "Kaunsi strike comparatively safer hai?",
        "OI buildup kya indicate kar raha hai?",
        "PCR kya bata raha hai?",
        "Koi important news hai?",
        "Aaj option selling avoid karni chahiye?",
        "Expiry ke din kya risk hai?",
        "Market mein volatility badhne ka risk hai?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
    ) {
        // Quick Question Chips Row
        Surface(
            color = TerminalSurface,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "QUICK MARKET QUERIES (TAP TO ASK):",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(quickQuestions) { q ->
                        Surface(
                            color = TerminalElevated,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder),
                            modifier = Modifier.clickable { onAskQuestion(q) }
                        ) {
                            Text(
                                text = q,
                                fontSize = 11.sp,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                if (msg.sender == "USER") {
                    // User bubble
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Surface(
                            color = AccentBlue,
                            shape = RoundedCornerShape(12.dp, 12.dp, 2.dp, 12.dp),
                            modifier = Modifier.widthIn(max = 320.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = msg.text,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 9.sp,
                                    color = Color(0xFFCFE0FC),
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                } else {
                    // AI response bubble
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp, 12.dp, 12.dp, 2.dp),
                        colors = CardDefaults.cardColors(containerColor = TerminalCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF243B5A))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = "AI",
                                        tint = AccentCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "NIFTY AI ANALYST",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = AccentCyan
                                    )
                                }

                                Surface(
                                    color = Color(0xFF102820),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "CONFIDENCE: ${msg.confidence}",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BullishGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = msg.text,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                lineHeight = 17.sp
                            )

                            if (msg.riskFactors.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFF261D10),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "⚠️ Risk Factor: ${msg.riskFactors}",
                                        fontSize = 10.sp,
                                        color = CautionYellow,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input bar
        Surface(
            color = TerminalSurface,
            modifier = Modifier.fillMaxWidth(),
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask about NIFTY trend, delta, PCR...", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("analyst_query_input"),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextPrimary),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = TerminalCardBorder,
                        focusedContainerColor = TerminalCard,
                        unfocusedContainerColor = TerminalCard
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(6.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onAskQuestion(inputText.trim())
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(AccentBlue, RoundedCornerShape(6.dp))
                        .testTag("send_analyst_query")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
