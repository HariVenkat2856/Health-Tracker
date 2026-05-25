package com.venkat.healthapp.hair.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.venkat.healthapp.hair.data.Section
import com.venkat.healthapp.hair.ui.*
import com.venkat.healthapp.common.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.hair.ui.components.ProgressRing
import com.venkat.healthapp.hair.ui.components.SectionHeader
import com.venkat.healthapp.hair.ui.components.ShampooSelector
import com.venkat.healthapp.hair.ui.components.TaskCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(vm: MainViewModel) {
    val tasks by vm.todayTasks.collectAsState()
    val (done, total) = vm.todayProgress.collectAsState().value
    val shampoo by vm.shampooToday.collectAsState()
    val grouped = vm.tasksBySection(tasks)

    val today = LocalDate.now()
    val dateStr = today.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy"))

    Scaffold(containerColor = BgDark) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // ── Header ───────────────────────────────────────────────────────
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(AccentAlpha, BlueAlpha)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, Accent.copy(0.3f), RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        "Dr. Murugusundram · CSF",
                        style = MaterialTheme.typography.labelSmall.copy(color = Accent, fontSize = 11.sp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Hair Treatment Tracker",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                    )
                    Text(
                        "Mr. Venkatramana H · Age 26",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Date + review bar ─────────────────────────────────────────────
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(CardDark, RoundedCornerShape(14.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Today", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp))
                    Text(dateStr, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("📅 Next Review", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp))
                    Text(
                        "09 June 2026",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold, color = Gold, fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Progress card ─────────────────────────────────────────────────
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Row(
                    Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ProgressRing(done, total)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Today's Progress",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp)
                        )
                        AnimatedContent(targetState = done, label = "prog") { d ->
                            Text(
                                when {
                                    total == 0 -> "Loading tasks..."
                                    d == total -> "🎉 Perfect day! Amazing!"
                                    d >= total / 2 -> "💪 More than halfway — keep going!"
                                    else -> "Tap each task as you complete it"
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                            )
                        }
                        Text(
                            "$done of $total tasks done",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Accent, fontWeight = FontWeight.Medium, fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // ── Shampoo selector ──────────────────────────────────────────────
            ShampooSelector(selected = shampoo, onSelect = { vm.setShampoo(it) })

            Spacer(Modifier.height(4.dp))

            // ── Task sections ─────────────────────────────────────────────────
            grouped.forEach { (section, items) ->
                val secDone = items.count { it.second?.completed == true }
                SectionHeader(section, secDone, items.size)

                if (section == Section.WEEKLY) {
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldAlpha, contentColor = TextPrimary),
                        border = BorderStroke(1.dp, Gold.copy(0.3f))
                    ) {
                        Text(
                            "⭐  Fix a day (e.g. Sunday). Do Derma Roller first, then Stemcello.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Gold, fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                items.forEach { (def, log) ->
                    TaskCard(def = def, log = log) {
                        vm.toggleTask(def.id, log?.completed == true)
                    }
                }

                Spacer(Modifier.height(4.dp))
            }

            // ── Review reminder ───────────────────────────────────────────────
            Spacer(Modifier.height(12.dp))
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldAlpha, contentColor = TextPrimary),
                border = BorderStroke(1.dp, Gold.copy(0.3f))
            ) {
                Row(
                    Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("🏥", fontSize = 30.sp)
                    Column {
                        Text(
                            "Next Review Appointment",
                            style = MaterialTheme.typography.titleMedium.copy(color = Gold, fontSize = 14.sp)
                        )
                        Text(
                            "09 June 2026 · Chennai Skin Foundation",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                        )
                        Text(
                            "Dr. S. Murugusundram · Don't miss!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            Text(
                "\"6 months of discipline = a lifetime of hair.\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontSize = 13.sp
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}
