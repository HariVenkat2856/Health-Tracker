package com.venkat.healthapp.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.auth.data.AppUser
import com.venkat.healthapp.common.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(


    vm: MainViewModel,
    onHairTracker: () -> Unit,
    onFoodTracker: () -> Unit,
    onWaterTracker: () -> Unit,
    onSleepTracker: () -> Unit,
    onWorkoutTracker: () -> Unit,
    onExpenseTracker: () -> Unit,
    onVault: () -> Unit
)



{
    val (hairDone, hairTotal) = vm.todayProgress.collectAsState().value
    val waterMl   by vm.waterToday.collectAsState()
    val waterTarget by vm.waterTarget.collectAsState()
    val nutrition by vm.todayNutrition.collectAsState()
    val targets   by vm.nutritionTargets.collectAsState()

    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11  -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else      -> "Good Night"
        }
    }
    val dateStr = remember {
        SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date())
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        // ── Greeting header ───────────────────────────────────────────────────
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(listOf(Color(0xFF0D2A1F), Color(0xFF0A1628), BgDark))
                )
                .border(1.dp, Accent.copy(0.3f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(greeting, color = Accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                Spacer(Modifier.height(4.dp))
                Text("Venkatramana 👋", fontSize = 26.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                Text(dateStr, color = TextMuted, fontSize = 13.sp)
                Spacer(Modifier.height(14.dp))
                // Quick stats row
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickStat("💊", "$hairDone/$hairTotal", "Medicines", Accent, Modifier.weight(1f))
                    QuickStat("💧", "${waterMl}ml", "Water", AccentBlue, Modifier.weight(1f))
                    QuickStat("🔥", "${(nutrition["calories"] ?: 0f).toInt()} kcal", "Eaten", Gold, Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Your Health Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Tap a module to get started", color = TextMuted, fontSize = 13.sp)

        Spacer(Modifier.height(16.dp))

        // ── Feature cards ─────────────────────────────────────────────────────
        FeatureCard(
            emoji = "💆",
            title = "Hair Tracker",
            subtitle = "Daily medicines • Photo journal • Progress",
            accentColor = Accent,
            gradientColors = listOf(Color(0xFF0D2A1F), Color(0xFF081A13)),
            progress = if (hairTotal > 0) hairDone.toFloat() / hairTotal else 0f,
            progressLabel = "$hairDone of $hairTotal tasks done today",
            badges = listOf("13 Medicines", "Weekly Photos", "Alarms"),
            onClick = onHairTracker
        )

        Spacer(Modifier.height(14.dp))

        FeatureCard(
            emoji = "🥗",
            title = "Food Tracker",
            subtitle = "Indian foods • Nutrition calculator • Body goals",
            accentColor = Gold,
            gradientColors = listOf(Color(0xFF2A1F0D), Color(0xFF1A1308)),
            progress = if ((targets?.calories ?: 0) > 0)
                ((nutrition["calories"] ?: 0f) / (targets?.calories ?: 1)).coerceIn(0f, 1f) else 0f,
            progressLabel = "${(nutrition["calories"] ?: 0f).toInt()} / ${targets?.calories ?: 0} kcal",
            badges = listOf("55+ Indian Foods", "Protein Tracker", "Body Goals"),
            onClick = onFoodTracker
        )

        Spacer(Modifier.height(14.dp))

        FeatureCard(
            emoji = "💧",
            title = "Water Tracker",
            subtitle = "3L daily goal • Hourly reminders • Liquid animation",
            accentColor = AccentBlue,
            gradientColors = listOf(Color(0xFF0A1628), Color(0xFF060E1A)),
            progress = if (waterTarget > 0) (waterMl.toFloat() / waterTarget).coerceIn(0f, 1f) else 0f,
            progressLabel = "${waterMl}ml / ${waterTarget}ml",
            badges = listOf("Hourly Alarms", "2 Bottles", "Sensor Tilt"),
            onClick = onWaterTracker
        )

        Spacer(Modifier.height(14.dp))
        // ── Coming soon ───────────────────────────────────────────────────────
        FeatureCard(
            emoji          = "🏋️",
            title          = "Workout Tracker",
            subtitle       = "PPL Split • Exercise GIFs • Diet Plan",
            accentColor    = Gold,
            gradientColors = listOf(Color(0xFF1A0D2E), Color(0xFF0D1117)),
            progress       = 0f,
            progressLabel  = "Start today's workout",
            badges         = listOf("PPL Split", "GIF Guide", "Veg Diet"),
            onClick        = onWorkoutTracker
        )

        Spacer(Modifier.height(14.dp))
        FeatureCard(
            emoji          = "💰",
            title          = "Expense Tracker",
            subtitle       = "Track spending • Note reminders • Analytics",
            accentColor    = Gold,
            gradientColors = listOf(Color(0xFF1A1200), Color(0xFF0D1117)),
            progress       = 0f,
            progressLabel  = "Track your spending",
            badges         = listOf("Note Reminder", "Categories", "Monthly"),
            onClick        = onExpenseTracker
        )

        Spacer(Modifier.height(14.dp))
        FeatureCard(
            emoji          = "🔐",
            title          = "Secure Vault",
            subtitle       = "Bank accounts • Passwords • Encrypted",
            accentColor    = Accent,
            gradientColors = listOf(Color(0xFF0A1628), Color(0xFF0D1117)),
            progress       = 0f,
            progressLabel  = "PIN protected • AES-256 encrypted",
            badges         = listOf("Encrypted", "PIN Lock", "Recovery"),
            onClick        = onVault
        )



        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FeatureCard(
                emoji          = "😴",
                title          = "Sleep Tracker",
                subtitle       = "Track rest • Quality • Duration",
                accentColor    = Purple,
                gradientColors = listOf(Color(0xFF1A0A2E), Color(0xFF100820)),
                progress       = 0f,
                progressLabel  = "Log last night's sleep",
                badges         = listOf("Moon Chart", "Bar Graph", "Tips"),
                onClick        = onSleepTracker
            )
            ComingSoonCard("😴", "Sleep\nTracker", Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))

    }
}

@Composable
fun QuickStat(emoji: String, value: String, label: String, color: Color, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = TextMuted)
    }
}


// Add user profile header at top of HomeScreen
@Composable
fun UserHeader(user: AppUser, onLogout: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar initials
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .background(Accent.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    user.displayName.take(2).uppercase(),
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Accent
                )
            }
            Column {
                Text(user.displayName,
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(user.email, fontSize = 12.sp, color = TextMuted)
            }
        }
        IconButton(onClick = onLogout) {
            Icon(Icons.Default.Logout, null, tint = TextMuted)
        }
    }
}

@Composable
fun FeatureCard(
    emoji: String, title: String, subtitle: String,
    accentColor: Color, gradientColors: List<Color>,
    progress: Float, progressLabel: String,
    badges: List<String>, onClick: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradientColors))
            .border(1.dp, accentColor.copy(0.35f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) { Text(emoji, fontSize = 26.sp) }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(subtitle, fontSize = 12.sp, color = TextMuted)
                }
                Icon(Icons.Default.ChevronRight, null, tint = accentColor, modifier = Modifier.size(24.dp))
            }

            Spacer(Modifier.height(14.dp))

            // Progress bar
            Text(progressLabel, fontSize = 12.sp, color = accentColor)
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(BorderDark)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .background(accentColor, RoundedCornerShape(100.dp))
                )
            }

            Spacer(Modifier.height(12.dp))

            // Badge row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.forEach { badge ->
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(accentColor.copy(0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(badge, fontSize = 11.sp, color = accentColor, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun ComingSoonCard(emoji: String, title: String, modifier: Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 28.sp)
            Spacer(Modifier.height(6.dp))
            Text(title, fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier.clip(RoundedCornerShape(100.dp)).background(BorderDark).padding(horizontal = 8.dp, vertical = 3.dp)
            ) { Text("Soon", fontSize = 10.sp, color = TextMuted) }
        }
    }
}
