package com.venkat.healthapp.sleep.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.common.*
import com.venkat.healthapp.sleep.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(vm: MainViewModel) {
    val allLogs     by vm.sleepLogs.collectAsState()
    val lastSeven   by vm.sleepLastSeven.collectAsState()
    val avgDuration by vm.sleepAvgDuration.collectAsState()
    val avgQuality  by vm.sleepAvgQuality.collectAsState()
    val goodDays    by vm.sleepGoodDays.collectAsState()
    val todayLog    by vm.todaySleepLog.collectAsState()

    var showLogSheet by remember { mutableStateOf(false) }

    LazyColumn(
        Modifier.fillMaxSize().background(BgDark).padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(Modifier.height(20.dp)) }

        // ── Header ────────────────────────────────────────────────────────────
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Sleep Tracker", fontSize = 26.sp,
                        fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("Track your rest & recovery",
                        color = TextMuted, fontSize = 13.sp)
                }
                FloatingActionButton(
                    onClick = { showLogSheet = true },
                    containerColor = Purple,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.Bedtime, null, modifier = Modifier.size(24.dp))
                }
            }
        }

        // ── Moon animation card ───────────────────────────────────────────────
        item {
            SleepMoonCard(todayLog = todayLog, avgDuration = avgDuration ?: 0f)
        }

        // ── Stats row ─────────────────────────────────────────────────────────
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SleepStatCard(
                    emoji = "⏱",
                    value = formatDuration((avgDuration ?: 0f).toInt()),
                    label = "Avg Duration",
                    color = Purple,
                    modifier = Modifier.weight(1f)
                )
                SleepStatCard(
                    emoji = "⭐",
                    value = "%.1f/5".format(avgQuality ?: 0f),
                    label = "Avg Quality",
                    color = Gold,
                    modifier = Modifier.weight(1f)
                )
                SleepStatCard(
                    emoji = "✅",
                    value = "$goodDays",
                    label = "7h+ Nights",
                    color = Accent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Last 7 days bar chart ──────────────────────────────────────────────
        if (lastSeven.isNotEmpty()) {
            item {
                Text("Last 7 Nights", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            item { SleepBarChart(lastSeven) }
        }

        // ── Today's log or prompt ─────────────────────────────────────────────
        item {
            if (todayLog != null) {
                TodaySleepCard(todayLog!!, onDelete = { vm.deleteSleepLog(todayLog!!) })
            } else {
                LogSleepPrompt(onClick = { showLogSheet = true })
            }
        }

        // ── Sleep tips ────────────────────────────────────────────────────────
        item {
            Text("Sleep Tips", fontSize = 16.sp,
                fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        item { SleepTipsCard() }

        // ── History ───────────────────────────────────────────────────────────
        if (allLogs.isNotEmpty()) {
            item {
                Text("Sleep History", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            items(allLogs.take(30)) { log ->
                SleepHistoryRow(log)
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }

    // ── Log sleep bottom sheet ────────────────────────────────────────────────
    if (showLogSheet) {
        LogSleepSheet(
            onDismiss = { showLogSheet = false },
            onSave    = { bedTime, wakeTime, quality, note ->
                vm.logSleep(bedTime, wakeTime, quality, note)
                showLogSheet = false
            }
        )
    }
}

// ── Moon animation card ───────────────────────────────────────────────────────
@Composable
fun SleepMoonCard(todayLog: SleepLog?, avgDuration: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "starTwinkle"
    )
    val moonFloat by infiniteTransition.animateFloat(
        initialValue = -4f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "moonFloat"
    )

    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(
                listOf(Color(0xFF0A0A2A), Color(0xFF10103A), Color(0xFF0D1117))
            ))
            .border(1.dp, Purple.copy(0.4f), RoundedCornerShape(20.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Stars background
        Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
            val starPositions = listOf(
                Offset(50f, -60f), Offset(150f, -30f), Offset(250f, -70f),
                Offset(100f, -90f), Offset(300f, -40f), Offset(200f, -100f)
            )
            starPositions.forEach { pos ->
                drawCircle(Color.White.copy(starAlpha), 2f, pos)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(10.dp))
            Text("🌙", fontSize = 52.sp,
                modifier = Modifier.offset(y = moonFloat.dp))

            if (todayLog != null) {
                Text(
                    formatDuration(todayLog.durationMinutes),
                    fontSize   = 36.sp,
                    fontWeight = FontWeight.Black,
                    color      = Purple
                )
                Text(
                    "Last night • ${qualityLabel(todayLog.quality)}",
                    color    = TextMuted,
                    fontSize = 13.sp
                )
                Text(
                    "${formatTime(todayLog.bedTimeMillis)} → ${formatTime(todayLog.wakeTimeMillis)}",
                    color    = Purple.copy(0.8f),
                    fontSize = 12.sp
                )
            } else {
                Text(
                    formatDuration(avgDuration.toInt()),
                    fontSize   = 36.sp,
                    fontWeight = FontWeight.Black,
                    color      = Purple
                )
                Text("Average sleep duration", color = TextMuted, fontSize = 13.sp)
                Text(
                    if (avgDuration >= 420) "Great sleep! Keep it up 🌟"
                    else "Aim for 7-8 hours every night",
                    color    = Purple.copy(0.8f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

// ── Sleep bar chart — last 7 nights ──────────────────────────────────────────
@Composable
fun SleepBarChart(logs: List<SleepLog>) {
    val maxMinutes = 480f // 8 hours max display
    Card(
        Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(Modifier.padding(16.dp)) {
            // 8h and 7h markers
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("8h target", fontSize = 10.sp, color = Accent)
                Text("7h min", fontSize = 10.sp, color = Gold)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth().height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val sorted = logs.sortedBy { it.date }
                sorted.forEach { log ->
                    val pct = (log.durationMinutes / maxMinutes).coerceIn(0f, 1f)
                    val barColor = when {
                        log.durationMinutes >= 480 -> Purple
                        log.durationMinutes >= 420 -> Accent
                        log.durationMinutes >= 360 -> Gold
                        else -> RedPill
                    }
                    val dayLabel = runCatching {
                        LocalDate.parse(log.date)
                            .format(DateTimeFormatter.ofPattern("EEE"))
                    }.getOrDefault("—")

                    Column(
                        Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            formatDuration(log.durationMinutes),
                            fontSize = 8.sp,
                            color    = barColor,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(pct)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(barColor.copy(0.8f))
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(dayLabel, fontSize = 9.sp, color = TextMuted)
                    }
                }
            }
            // Legend
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(Purple to "8h+", Accent to "7-8h", Gold to "6-7h", RedPill to "<6h").forEach { (c, l) ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(c))
                        Text(l, fontSize = 10.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}

// ── Stat card ─────────────────────────────────────────────────────────────────
@Composable
fun SleepStatCard(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(emoji, fontSize = 20.sp)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center)
        }
    }
}

// ── Today's sleep card ────────────────────────────────────────────────────────
@Composable
fun TodaySleepCard(log: SleepLog, onDelete: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PurpleAlpha)
            .border(1.dp, Purple.copy(0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Last Night", fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = Purple)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, null,
                        tint = RedPill, modifier = Modifier.size(18.dp))
                }
            }
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Duration", fontSize = 11.sp, color = TextMuted)
                    Text(formatDuration(log.durationMinutes),
                        fontSize = 22.sp, fontWeight = FontWeight.Black, color = Purple)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bed", fontSize = 11.sp, color = TextMuted)
                    Text(formatTime(log.bedTimeMillis),
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Wake", fontSize = 11.sp, color = TextMuted)
                    Text(formatTime(log.wakeTimeMillis),
                        fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Quality", fontSize = 11.sp, color = TextMuted)
                    Text(qualityLabel(log.quality),
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Gold)
                }
            }
            if (log.note.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Notes, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text(log.note, fontSize = 12.sp, color = TextMuted)
                }
            }
        }
    }
}

// ── Log sleep prompt ──────────────────────────────────────────────────────────
@Composable
fun LogSleepPrompt(onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Card2Dark)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("🌙", fontSize = 36.sp)
            Text("Log Last Night's Sleep",
                fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("Tap to add your sleep data",
                fontSize = 12.sp, color = TextMuted)
        }
    }
}

// ── Log sleep sheet ───────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSleepSheet(
    onDismiss: () -> Unit,
    onSave: (bedTime: Long, wakeTime: Long, quality: Int, note: String) -> Unit
) {
    var bedHour    by remember { mutableIntStateOf(22) }
    var bedMinute  by remember { mutableIntStateOf(30) }
    var wakeHour   by remember { mutableIntStateOf(6) }
    var wakeMinute by remember { mutableIntStateOf(30) }
    var quality    by remember { mutableIntStateOf(3) }
    var note       by remember { mutableStateOf("") }
    var showBedPicker  by remember { mutableStateOf(false) }
    var showWakePicker by remember { mutableStateOf(false) }

    // Calculate duration
    val bedTotalMin  = bedHour * 60 + bedMinute
    val wakeTotalMin = wakeHour * 60 + wakeMinute
    val durationMin  = if (wakeTotalMin > bedTotalMin)
        wakeTotalMin - bedTotalMin
    else
        (24 * 60 - bedTotalMin) + wakeTotalMin

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = CardDark,
        contentColor     = TextPrimary
    ) {
        Column(
            Modifier.padding(20.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Log Sleep", fontSize = 20.sp,
                fontWeight = FontWeight.Black, color = TextPrimary)

            // Duration preview
            Box(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurpleAlpha)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(formatDuration(durationMin),
                        fontSize = 32.sp, fontWeight = FontWeight.Black, color = Purple)
                    Text("Sleep duration", fontSize = 12.sp, color = TextMuted)
                }
            }

            // Bed time + Wake time
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Bed time
                TimePickerBox(
                    label   = "🌙 Bed Time",
                    hour    = bedHour,
                    minute  = bedMinute,
                    color   = Purple,
                    modifier = Modifier.weight(1f),
                    onClick = { showBedPicker = true }
                )
                // Wake time
                TimePickerBox(
                    label   = "☀️ Wake Time",
                    hour    = wakeHour,
                    minute  = wakeMinute,
                    color   = Gold,
                    modifier = Modifier.weight(1f),
                    onClick = { showWakePicker = true }
                )
            }

            // Quality selector
            Text("Sleep Quality", fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { q ->
                    val sel = quality == q
                    val qColor = when(q) {
                        1 -> RedPill; 2 -> Gold; 3 -> AccentBlue; 4 -> Accent; 5 -> Purple
                        else -> TextMuted
                    }
                    Column(
                        Modifier.weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (sel) qColor.copy(0.2f) else Card2Dark)
                            .border(1.dp, if (sel) qColor else BorderDark, RoundedCornerShape(10.dp))
                            .clickable { quality = q }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            when(q) { 1->"😫"; 2->"😴"; 3->"😐"; 4->"😊"; 5->"🌟"; else->"" },
                            fontSize = 18.sp
                        )
                        Text("$q", fontSize = 12.sp, color = if (sel) qColor else TextMuted,
                            fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            // Note field
            OutlinedTextField(
                value         = note,
                onValueChange = { note = it },
                label         = { Text("Note (optional)", color = TextMuted) },
                placeholder   = { Text("e.g. Had coffee late, woke up refreshed...", color = TextMuted) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                maxLines      = 2,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Purple,
                    unfocusedBorderColor = BorderDark,
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextPrimary,
                    cursorColor          = Purple
                )
            )

            // Save button
            Button(
                onClick = {
                    val cal = java.util.Calendar.getInstance()
                    // Bed time — last night
                    cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                    cal.set(java.util.Calendar.HOUR_OF_DAY, bedHour)
                    cal.set(java.util.Calendar.MINUTE, bedMinute)
                    cal.set(java.util.Calendar.SECOND, 0)
                    val bedMillis = cal.timeInMillis

                    // Wake time — this morning
                    cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    cal.set(java.util.Calendar.HOUR_OF_DAY, wakeHour)
                    cal.set(java.util.Calendar.MINUTE, wakeMinute)
                    val wakeMillis = cal.timeInMillis

                    onSave(bedMillis, wakeMillis, quality, note)
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Purple)
            ) {
                Icon(Icons.Default.Bedtime, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Save Sleep Log", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // Time pickers
    if (showBedPicker) {
        TimePickerDialog(
            initialHour   = bedHour,
            initialMinute = bedMinute,
            onDismiss     = { showBedPicker = false },
            onConfirm     = { h, m -> bedHour = h; bedMinute = m; showBedPicker = false }
        )
    }
    if (showWakePicker) {
        TimePickerDialog(
            initialHour   = wakeHour,
            initialMinute = wakeMinute,
            onDismiss     = { showWakePicker = false },
            onConfirm     = { h, m -> wakeHour = h; wakeMinute = m; showWakePicker = false }
        )
    }
}

@Composable
fun TimePickerBox(label: String, hour: Int, minute: Int, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(0.1f))
            .border(1.dp, color.copy(0.4f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 11.sp, color = TextMuted)
            Spacer(Modifier.height(4.dp))
            val amPm = if (hour < 12) "AM" else "PM"
            val displayH = when { hour == 0 -> 12; hour > 12 -> hour - 12; else -> hour }
            Text("%d:%02d %s".format(displayH, minute, amPm),
                fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(Modifier.height(4.dp))
            Text("Tap to change", fontSize = 10.sp, color = TextMuted)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int, initialMinute: Int,
    onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour, initialMinute = initialMinute, is24Hour = false
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CardDark,
        title = { Text("Select Time", color = TextPrimary) },
        text  = {
            TimePicker(
                state  = state,
                colors = TimePickerDefaults.colors(
                    clockDialColor          = Card2Dark,
                    selectorColor           = Purple,
                    containerColor          = CardDark,
                    periodSelectorBorderColor = BorderDark,
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = TextMuted,
                    timeSelectorSelectedContainerColor = Purple,
                    timeSelectorUnselectedContainerColor = Card2Dark,
                    timeSelectorSelectedContentColor = Color.White,
                    timeSelectorUnselectedContentColor = TextMuted
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) {
                Text("OK", color = Purple, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        }
    )
}

// ── History row ───────────────────────────────────────────────────────────────
@Composable
fun SleepHistoryRow(log: SleepLog) {
    val pct = (log.durationMinutes / 480f).coerceIn(0f, 1f)
    val color = when {
        log.durationMinutes >= 480 -> Purple
        log.durationMinutes >= 420 -> Accent
        log.durationMinutes >= 360 -> Gold
        else -> RedPill
    }
    val dateDisplay = runCatching {
        LocalDate.parse(log.date).format(DateTimeFormatter.ofPattern("EEE, dd MMM"))
    }.getOrDefault(log.date)

    Box(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(14.dp, 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("🌙", fontSize = 20.sp)
            Column(Modifier.weight(1f)) {
                Text(dateDisplay, fontSize = 14.sp,
                    fontWeight = FontWeight.Medium, color = TextPrimary)
                Text(
                    "${formatTime(log.bedTimeMillis)} → ${formatTime(log.wakeTimeMillis)}  •  ${qualityLabel(log.quality)}",
                    fontSize = 11.sp, color = TextMuted
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatDuration(log.durationMinutes),
                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
                Box(
                    Modifier.width(50.dp).height(4.dp)
                        .clip(RoundedCornerShape(100.dp)).background(BorderDark)
                ) {
                    Box(Modifier.fillMaxWidth(pct).height(4.dp)
                        .background(color, RoundedCornerShape(100.dp)))
                }
            }
        }
    }
}

// ── Sleep tips card ───────────────────────────────────────────────────────────
@Composable
fun SleepTipsCard() {
    val tips = listOf(
        "🌙" to "Sleep before 11 PM for best hair growth hormone release",
        "📵" to "No phone 30 mins before bed — blue light disrupts melatonin",
        "🌡" to "Keep room cool (18-20°C) for deeper sleep",
        "💊" to "Take your T.Minodez at night — it works better during sleep",
        "💧" to "Avoid water 1 hour before bed to prevent wake-ups",
        "🧘" to "5 min deep breathing before sleep improves quality"
    )
    Card(
        Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, Purple.copy(0.3f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            tips.forEach { (emoji, tip) ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(emoji, fontSize = 16.sp)
                    Text(tip, fontSize = 13.sp, color = TextMuted)
                }
            }
        }
    }
}