package com.venkat.healthapp.hair.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.venkat.healthapp.hair.alarm.*
import com.venkat.healthapp.common.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen() {
    val context = LocalContext.current

    // Permission launcher for Android 13+
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* handled silently */ }

    // Request notification permission on first open
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(containerColor = BgDark) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                "Reminders",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp)
            )
            Text(
                "Set alarms so you never miss a dose",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))

            // ── Info card ─────────────────────────────────────────────────────
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AccentAlpha, contentColor = TextPrimary),
                border = BorderStroke(1.dp, Accent.copy(0.3f))
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Notifications, null, tint = Accent, modifier = Modifier.size(24.dp))
                    Text(
                        "Alarms repeat every day automatically. Weekly alarm fires every Sunday. Works even after phone restart.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Accent, fontSize = 12.sp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Alarm cards ───────────────────────────────────────────────────
            AlarmSlot.values().forEach { slot ->
                AlarmCard(context = context, slot = slot)
                Spacer(Modifier.height(12.dp))
            }

            // ── Quick set all ─────────────────────────────────────────────────
            Spacer(Modifier.height(4.dp))
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Card2Dark, contentColor = TextPrimary),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "⚡ Recommended Schedule",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp)
                    )
                    listOf(
                        "🌅 Morning" to "8:00 AM — After breakfast",
                        "🌤 Afternoon" to "1:00 PM — After lunch",
                        "🌙 Night" to "9:00 PM — After dinner",
                        "📆 Weekly" to "9:00 AM — Every Sunday"
                    ).forEach { (label, time) ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp))
                            Text(
                                time,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Accent, fontSize = 13.sp
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = {
                            // Set all alarms to recommended times
                            AlarmSlot.values().forEach { slot ->
                                AlarmScheduler.schedule(context, slot, slot.defaultHour, slot.defaultMinute)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Icon(Icons.Default.Alarm, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Set All Recommended Alarms",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Individual Alarm Card ─────────────────────────────────────────────────────
@Composable
fun AlarmCard(context: Context, slot: AlarmSlot) {
    var enabled by remember { mutableStateOf(AlarmScheduler.isEnabled(context, slot)) }
    var savedTime by remember {
        mutableStateOf(AlarmScheduler.getSavedTime(context, slot))
    }

    val accentColor = when (slot) {
        AlarmSlot.MORNING   -> Gold
        AlarmSlot.AFTERNOON -> AccentBlue
        AlarmSlot.NIGHT     -> Color(0xFFB57AFF)
        AlarmSlot.WEEKLY    -> RedPill
    }
    val alphaColor = when (slot) {
        AlarmSlot.MORNING   -> GoldAlpha
        AlarmSlot.AFTERNOON -> BlueAlpha
        AlarmSlot.NIGHT     -> Color(0x22B57AFF)
        AlarmSlot.WEEKLY    -> RedAlpha
    }

    val timeStr = remember(savedTime) {
        val (h, m) = savedTime
        val amPm = if (h < 12) "AM" else "PM"
        val displayH = when {
            h == 0   -> 12
            h > 12   -> h - 12
            else     -> h
        }
        "%d:%02d %s".format(displayH, m, amPm)
    }

    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) alphaColor else CardDark,
            contentColor = TextPrimary
        ),
        border = BorderStroke(
            1.dp, if (enabled) accentColor.copy(0.4f) else BorderDark
        )
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji icon
                Box(
                    Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (enabled) alphaColor else Card2Dark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(slot.emoji, fontSize = 22.sp)
                }

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        slot.label,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold, fontSize = 15.sp
                        )
                    )
                    Text(
                        if (slot.isWeekly) "Every Sunday" else "Every day",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                    )
                }

                // Toggle switch
                Switch(
                    checked = enabled,
                    onCheckedChange = { on ->
                        enabled = on
                        if (on) {
                            AlarmScheduler.schedule(context, slot, savedTime.first, savedTime.second)
                        } else {
                            AlarmScheduler.cancel(context, slot)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = accentColor,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = BorderDark
                    )
                )
            }

            AnimatedVisibility(visible = enabled) {
                Column {
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = BorderDark)
                    Spacer(Modifier.height(14.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Alarm Time",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                            )
                            Text(
                                timeStr,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = accentColor,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        // Time picker button
                        OutlinedButton(
                            onClick = {
                                val (h, m) = savedTime
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        savedTime = Pair(hour, minute)
                                        AlarmScheduler.schedule(context, slot, hour, minute)
                                    },
                                    h, m, false
                                ).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, accentColor.copy(0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Change", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Notification preview
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Card2Dark)
                            .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    null,
                                    tint = accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    "Notification preview",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextMuted, fontSize = 10.sp
                                    )
                                )
                            }
                            Text(
                                slot.notifTitle,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                slot.notifMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}
