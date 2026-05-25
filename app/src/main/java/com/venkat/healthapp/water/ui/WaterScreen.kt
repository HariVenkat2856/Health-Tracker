package com.venkat.healthapp.water.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.common.*
import kotlin.math.*

@Composable
fun WaterScreen(vm: MainViewModel) {
    val waterDrunk  by vm.waterToday.collectAsState()       // how much already drunk today
    val waterTarget by vm.waterTarget.collectAsState()      // e.g. 3000 ml
    val waterLogs   by vm.waterLogs.collectAsState()
    val context     = LocalContext.current

    // Apply any hourly auto-reduces that fired while app was closed
    LaunchedEffect(Unit) {
        vm.applyPendingAutoReduce(context)
    }

    // ── Tilt sensor ───────────────────────────────────────────────────────────
    var tiltX by remember { mutableStateOf(0f) }
    DisposableEffect(Unit) {
        val sm  = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                tiltX = (e.values[0] / 9.8f).coerceIn(-1f, 1f)
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        sm.registerListener(listener, acc, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sm.unregisterListener(listener) }
    }

    // ── Bottle logic ──────────────────────────────────────────────────────────
    // Each bottle holds half the target (e.g. 1500ml each)
    val bottleCapacity = waterTarget / 2

    // Remaining water in each bottle (starts full, reduces as you drink)
    val totalRemaining = (waterTarget - waterDrunk).coerceAtLeast(0)

    // Bottle 1 fills first — starts at 1500ml, drains first
    val bottle1Remaining = totalRemaining.coerceAtMost(bottleCapacity)
    val bottle2Remaining = (totalRemaining - bottleCapacity).coerceAtLeast(0)

    // Fill % for bottle canvas (1.0 = full, 0.0 = empty)
    val bottle1Pct = bottle1Remaining.toFloat() / bottleCapacity.toFloat()
    val bottle2Pct = bottle2Remaining.toFloat() / bottleCapacity.toFloat()

    // Overall drunk percentage for progress bar
    val drunkPct = (waterDrunk.toFloat() / waterTarget.toFloat()).coerceIn(0f, 1f)

    // Water alarm toggle
    val alarmEnabled = remember { mutableStateOf(WaterAlarmScheduler.isEnabled(context)) }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        // ── Header ────────────────────────────────────────────────────────────
        Text("Water Tracker", fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text("Daily goal: ${waterTarget}ml  •  Drink every hour",
            color = TextMuted, fontSize = 13.sp)

        Spacer(Modifier.height(24.dp))

        // ── Two bottles — START FULL, DRAIN AS YOU DRINK ──────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            WaterBottle(
                label      = "Bottle 1",
                subLabel   = "${bottle1Remaining}ml left",
                fillPct    = bottle1Pct,
                tiltX      = tiltX,
                color      = AccentBlue,
                isEmpty    = bottle1Remaining == 0
            )
            // Middle divider with arrow
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("→", fontSize = 28.sp, color = TextMuted)
                Text("drink", fontSize = 10.sp, color = TextMuted)
            }
            WaterBottle(
                label      = "Bottle 2",
                subLabel   = "${bottle2Remaining}ml left",
                fillPct    = bottle2Pct,
                tiltX      = tiltX,
                color      = Accent,
                isEmpty    = bottle2Remaining == 0
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Progress summary card ─────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CardDark)
                .border(1.dp,
                    if (drunkPct >= 1f) Accent.copy(0.6f) else AccentBlue.copy(0.35f),
                    RoundedCornerShape(18.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Drunk Today", fontSize = 12.sp, color = TextMuted)
                        Text("${waterDrunk}ml",
                            fontSize   = 32.sp,
                            fontWeight = FontWeight.Black,
                            color      = AccentBlue)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Remaining", fontSize = 12.sp, color = TextMuted)
                        Text("${totalRemaining}ml",
                            fontSize   = 32.sp,
                            fontWeight = FontWeight.Black,
                            color      = if (totalRemaining == 0) Accent else Gold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Progress bar
                Box(
                    Modifier.fillMaxWidth().height(10.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(BorderDark)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(drunkPct)
                            .height(10.dp)
                            .background(
                                Brush.horizontalGradient(listOf(AccentBlue, Accent)),
                                RoundedCornerShape(100.dp)
                            )
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    when {
                        drunkPct >= 1f   -> "🎉 Both bottles empty! Goal complete!"
                        drunkPct >= 0.75f -> "Almost there! Just ${totalRemaining}ml more 💪"
                        drunkPct >= 0.5f  -> "Halfway! Keep drinking 💧"
                        drunkPct >= 0.25f -> "Good start! ${totalRemaining}ml remaining"
                        else             -> "Tap below to log your water intake"
                    },
                    color      = if (drunkPct >= 1f) Accent else TextMuted,
                    fontSize   = 13.sp,
                    fontWeight = if (drunkPct >= 1f) FontWeight.Bold else FontWeight.Normal,
                    textAlign  = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Hourly alarm toggle ───────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (alarmEnabled.value) AccentAlpha else Card2Dark)
                .border(1.dp,
                    if (alarmEnabled.value) Accent.copy(0.5f) else BorderDark,
                    RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("⏰", fontSize = 26.sp)
                    Column {
                        Text("Hourly Reminder",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary)
                        Text(
                            if (alarmEnabled.value)
                                "Rings every hour — even when app closed"
                            else
                                "Turn on to get hourly reminders",
                            fontSize = 12.sp,
                            color    = TextMuted
                        )
                    }
                }
                Switch(
                    checked       = alarmEnabled.value,
                    onCheckedChange = { on ->
                        alarmEnabled.value = on
                        if (on) WaterAlarmScheduler.enable(context)
                        else    WaterAlarmScheduler.disable(context)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor  = Color.Black,
                        checkedTrackColor  = Accent,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = BorderDark
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Quick add buttons ─────────────────────────────────────────────────
        Text("I drank water — tap to log:",
            fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
            color = TextPrimary, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(10.dp))

        // Row 1
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            WaterAddBtn("💧\n150ml\nSmall cup", AccentBlue, Modifier.weight(1f)) { vm.addWater(150) }
            WaterAddBtn("💧\n250ml\nGlass",    AccentBlue, Modifier.weight(1f)) { vm.addWater(250) }
            WaterAddBtn("💧\n350ml\nBig glass", Accent,    Modifier.weight(1f)) { vm.addWater(350) }
        }
        Spacer(Modifier.height(8.dp))
        // Row 2
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            WaterAddBtn("🍶\n500ml\nBottle",    Accent,  Modifier.weight(1f)) { vm.addWater(500) }
            WaterAddBtn("🍶\n750ml\nLarge",     Gold,    Modifier.weight(1f)) { vm.addWater(750) }
            WaterAddBtn("↩️\nUndo\nLast",       RedPill, Modifier.weight(1f)) { vm.removeLastWater() }
        }

        Spacer(Modifier.height(20.dp))

        // ── Today's log ───────────────────────────────────────────────────────
        if (waterLogs.isNotEmpty()) {
            Text("Today's Log",
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextPrimary,
                modifier   = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(8.dp))

            waterLogs.reversed().forEach { log ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                        .padding(14.dp, 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💧", fontSize = 16.sp)
                        Text("+${log.amountMl}ml",
                            fontSize   = 14.sp,
                            color      = AccentBlue,
                            fontWeight = FontWeight.Bold)
                    }
                    Text(
                        java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                            .format(java.util.Date(log.loggedAt)),
                        fontSize = 12.sp,
                        color    = TextMuted
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ── Animated liquid bottle — starts FULL, drains as water is drunk ────────────
@Composable
fun WaterBottle(
    label: String,
    subLabel: String,
    fillPct: Float,        // 1.0 = full, 0.0 = empty
    tiltX: Float,
    color: Color,
    isEmpty: Boolean
) {
    // Smooth animation when fill changes
    val animFill by animateFloatAsState(
        targetValue  = fillPct,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 60f),
        label        = "bottleFill"
    )

    // Continuous wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label         = "wavePhase"
    )

    // Tilt-responsive offset — phone tilt moves water sideways
    val tiltOffset by animateFloatAsState(
        targetValue   = tiltX * 22f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 90f),
        label         = "tilt"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = TextPrimary)

        Canvas(modifier = Modifier.width(100.dp).height(200.dp)) {
            val w = size.width
            val h = size.height

            // Bottle shape parameters
            val neckTop    = h * 0.05f
            val neckBottom = h * 0.15f
            val bodyTop    = h * 0.15f
            val bodyBottom = h * 0.96f
            val neckLeft   = w * 0.32f
            val neckRight  = w * 0.68f
            val bodyLeft   = w * 0.08f
            val bodyRight  = w * 0.92f

            // Build bottle outline path
            val bottlePath = Path().apply {
                moveTo(neckLeft, neckTop)
                lineTo(neckRight, neckTop)
                lineTo(neckRight, neckBottom)
                cubicTo(neckRight, neckBottom, bodyRight * 0.85f, bodyTop + h * 0.04f, bodyRight, bodyTop + h * 0.06f)
                lineTo(bodyRight, bodyBottom - h * 0.04f)
                quadraticBezierTo(bodyRight, bodyBottom, bodyRight - h * 0.025f, bodyBottom)
                lineTo(bodyLeft + h * 0.025f, bodyBottom)
                quadraticBezierTo(bodyLeft, bodyBottom, bodyLeft, bodyBottom - h * 0.04f)
                lineTo(bodyLeft, bodyTop + h * 0.06f)
                cubicTo(bodyLeft, bodyTop + h * 0.04f, neckLeft * 1.15f, neckBottom, neckLeft, neckBottom)
                close()
            }

            // Draw bottle background (empty look)
            drawPath(bottlePath, color = color.copy(alpha = 0.08f))

            // Draw water fill with wave — clipped inside bottle
            if (animFill > 0.002f) {
                val fillTop = bodyTop + h * 0.06f +
                        (bodyBottom - bodyTop - h * 0.06f) * (1f - animFill)
                val waveAmp = 5f * (1f - animFill * 0.6f)     // smaller wave when nearly full

                val waterPath = Path().apply {
                    // Start bottom-left inside bottle
                    moveTo(bodyLeft, bodyBottom)
                    lineTo(bodyLeft, fillTop + waveAmp)

                    // Draw wave across top surface
                    val steps = 40
                    for (i in 0..steps) {
                        val x = bodyLeft + (bodyRight - bodyLeft) * i.toFloat() / steps
                        val waveY = fillTop +
                                sin(wavePhase + i.toFloat() * 0.4f + tiltOffset * 0.08f).toFloat() * waveAmp +
                                tiltOffset * (i.toFloat() / steps - 0.5f) * 0.5f
                        lineTo(x, waveY)
                    }
                    lineTo(bodyRight, bodyBottom)
                    close()
                }

                // Clip water to bottle shape
                clipPath(bottlePath) {
                    // Water gradient — deeper blue at bottom, lighter at top
                    drawPath(
                        waterPath,
                        brush = Brush.verticalGradient(
                            colors  = listOf(color.copy(alpha = 0.5f), color.copy(alpha = 0.85f)),
                            startY  = fillTop,
                            endY    = bodyBottom
                        )
                    )
                    // Shine effect
                    drawRect(
                        brush    = Brush.horizontalGradient(
                            listOf(Color.White.copy(0.15f), Color.Transparent, Color.Transparent)
                        ),
                        topLeft  = Offset(bodyLeft, fillTop),
                        size     = Size(bodyRight - bodyLeft, bodyBottom - fillTop)
                    )
                }
            }

            // Bottle outline stroke
            drawPath(
                bottlePath,
                color = if (isEmpty) RedPill.copy(0.5f) else color.copy(alpha = 0.7f),
                style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Bottle cap
            drawRoundRect(
                color      = color.copy(alpha = 0.8f),
                topLeft    = Offset(neckLeft + 2f, 0f),
                size       = Size(neckRight - neckLeft - 4f, neckTop + 4f),
                cornerRadius = CornerRadius(6f)
            )

            // "EMPTY" cross mark when empty
            if (isEmpty) {
                val cx = w / 2f; val cy = h * 0.55f; val r = 22f
                drawCircle(RedPill.copy(0.2f), r, Offset(cx, cy))
                drawLine(RedPill, Offset(cx - r * 0.6f, cy - r * 0.6f),
                    Offset(cx + r * 0.6f, cy + r * 0.6f), 4f, cap = StrokeCap.Round)
                drawLine(RedPill, Offset(cx + r * 0.6f, cy - r * 0.6f),
                    Offset(cx - r * 0.6f, cy + r * 0.6f), 4f, cap = StrokeCap.Round)
            }
        }

        // Fill percentage label
        Text(
            if (isEmpty) "Empty ✅" else "${(fillPct * 100).toInt()}% full",
            fontSize   = 12.sp,
            fontWeight = FontWeight.Bold,
            color      = if (isEmpty) Accent else color
        )
        Text(subLabel, fontSize = 11.sp, color = TextMuted)
    }
}

@Composable
fun WaterAddBtn(label: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(0.1f))
            .border(1.dp, color.copy(0.4f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize  = 12.sp,
            color     = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
