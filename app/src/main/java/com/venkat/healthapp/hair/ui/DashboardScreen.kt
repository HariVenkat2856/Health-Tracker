package com.venkat.healthapp.hair.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.venkat.healthapp.hair.data.DailySummary
import com.venkat.healthapp.common.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.hair.ui.components.StatCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(vm: MainViewModel) {
    val summaries by vm.allSummaries.collectAsState()
    val perfectDays by vm.perfectDays.collectAsState()
    val activeDays by vm.activeDays.collectAsState()
    val totalCompleted by vm.totalCompleted.collectAsState()
    val streak by vm.currentStreak.collectAsState()

    Scaffold(containerColor = BgDark) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(Modifier.height(20.dp)) }

            // ── Title ─────────────────────────────────────────────────────────
            item {
                Column {
                    Text(
                        "Dashboard",
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp)
                    )
                    Text(
                        "Your treatment journey overview",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ── Streak hero ───────────────────────────────────────────────────
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                    border = BorderStroke(1.dp, Accent.copy(0.4f))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                    listOf(AccentAlpha, Color.Transparent)
                                )
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔥", fontSize = 48.sp)
                            Text(
                                "$streak",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    color = Accent, fontSize = 56.sp, fontWeight = FontWeight.Black
                                )
                            )
                            Text(
                                "Day Streak",
                                style = MaterialTheme.typography.titleMedium.copy(color = TextMuted)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (streak == 0) "Start your streak today!"
                                else "Keep it going — don't break the chain! 💪",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // ── Stats grid ────────────────────────────────────────────────────
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        emoji = "✅",
                        value = "$perfectDays",
                        label = "Perfect\nDays",
                        accentColor = Gold,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        emoji = "📅",
                        value = "$activeDays",
                        label = "Active\nDays",
                        accentColor = Accent,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        emoji = "💊",
                        value = "$totalCompleted",
                        label = "Tasks\nDone",
                        accentColor = AccentBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Progress bar towards 180 days ─────────────────────────────────
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Treatment Journey",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp)
                            )
                            Text(
                                "$activeDays / 180 days",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Accent, fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                        val prog = (activeDays / 180f).coerceIn(0f, 1f)
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(BorderDark)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(prog)
                                    .height(10.dp)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                            listOf(AccentBlue, Accent)
                                        ),
                                        shape = RoundedCornerShape(100.dp)
                                    )
                            )
                        }
                        val milestones = listOf(
                            30 to "Scalp clears 🌱",
                            90 to "Baby hairs 🌿",
                            180 to "Full results 🌳"
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            milestones.forEach { (day, label) ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (activeDays >= day) Accent else TextMuted,
                                            fontSize = 10.sp
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        "Day $day",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextMuted, fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Completion heatmap (last 30 days) ─────────────────────────────
            item {
                Text(
                    "Last 30 Days",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                )
            }
            item { HeatmapGrid(summaries) }

            // ── History list ──────────────────────────────────────────────────
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Activity History",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                    )
                    Button(
                        onClick = {
                            val today = LocalDate.now()
                            // Sun 10 to Fri 15 = last 5 days (today is day 6 — already logging today live)
                            // 10 out of 13 = 76% (daily tasks only, skip 3 weekly tasks)
                            (1..5).forEach { daysAgo ->
                                val date = today.minusDays(daysAgo.toLong())
                                    .format(DateTimeFormatter.ISO_LOCAL_DATE)
                                vm.logPastDay(date, 10) // 10 tasks = daily only, skip weekly 3
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Fill 5 Days", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (summaries.isEmpty()) {
                item {
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
                        border = BorderStroke(1.dp, BorderDark)
                    ) {
                        Column(
                            Modifier.padding(32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📋", fontSize = 36.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No history yet",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Start completing tasks today!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(summaries) { summary ->
                    HistoryRow(summary)
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// ── Heatmap ──────────────────────────────────────────────────────────────────
@Composable
fun HeatmapGrid(summaries: List<DailySummary>) {
    val summaryMap = summaries.associateBy { it.date }
    val today = LocalDate.now()
    val days = (29 downTo 0).map { today.minusDays(it.toLong()) }
    val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                days.forEach { date ->
                    val key = date.format(fmt)
                    val summary = summaryMap[key]
                    val pct = if (summary != null && summary.totalTasks > 0)
                        summary.completedTasks.toFloat() / summary.totalTasks else 0f
                    val color = when {
                        pct >= 1f   -> Gold
                        pct >= 0.7f -> Accent
                        pct >= 0.3f -> AccentBlue
                        pct > 0f    -> AccentBlue.copy(0.4f)
                        else        -> BorderDark
                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Less", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = TextMuted))
                Spacer(Modifier.width(4.dp))
                listOf(BorderDark, AccentBlue.copy(0.4f), AccentBlue, Accent, Gold).forEach { c ->
                    Box(
                        Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(c)
                    )
                    Spacer(Modifier.width(2.dp))
                }
                Text("More", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = TextMuted))
            }
        }
    }
}

// ── History row ───────────────────────────────────────────────────────────────
@Composable
fun HistoryRow(summary: DailySummary) {
    val fmt = DateTimeFormatter.ISO_LOCAL_DATE
    val displayFmt = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")
    val date = runCatching { LocalDate.parse(summary.date, fmt) }.getOrNull()
    val pct = if (summary.totalTasks > 0)
        summary.completedTasks.toFloat() / summary.totalTasks else 0f
    val (color, emoji) = when {
        pct >= 1f   -> Gold to "🏆"
        pct >= 0.7f -> Accent to "✅"
        pct >= 0.3f -> AccentBlue to "📊"
        else        -> TextMuted to "📋"
    }

    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Row(
            Modifier.padding(14.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(emoji, fontSize = 22.sp)
            Column(Modifier.weight(1f)) {
                Text(
                    date?.format(displayFmt) ?: summary.date,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium, fontSize = 14.sp
                    )
                )
                Text(
                    "${summary.completedTasks} / ${summary.totalTasks} tasks",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                )
            }
            // Mini progress
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "${(pct * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )
                )
                Box(
                    Modifier
                        .width(60.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(BorderDark)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(pct)
                            .height(5.dp)
                            .background(color, RoundedCornerShape(100.dp))
                    )
                }
            }
        }
    }



}

