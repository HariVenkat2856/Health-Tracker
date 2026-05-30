package com.venkat.healthapp.expense.insights

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.venkat.healthapp.MainViewModel
import com.venkat.healthapp.common.*
import com.venkat.healthapp.expense.data.currentMonth

@Composable
fun InsightScreen(vm: MainViewModel) {
    val context      = LocalContext.current
    val allExpenses  by vm.monthExpenses.collectAsState()
    val monthTotal   by vm.monthTotal.collectAsState()

    // Generate insights
    val insights = remember(allExpenses) {
        InsightEngine.generateInsights(allExpenses, currentMonth())
    }

    val thisWeek  = remember(allExpenses) { InsightEngine.getThisWeekExpenses(allExpenses) }
    val lastWeek  = remember(allExpenses) { InsightEngine.getLastWeekExpenses(allExpenses) }
    val summary   = remember(thisWeek, lastWeek) {
        InsightEngine.generateWeeklySummary(thisWeek, lastWeek)
    }

    // Total potential savings
    val totalSavings = insights.sumOf { it.savingAmount.toDouble() }.toFloat()

    LazyColumn(
        Modifier.fillMaxSize().background(BgDark).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }

        // ── Header ────────────────────────────────────────────────────────────
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("💡 Spending Insights",
                        fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("AI analysis of your habits",
                        color = TextMuted, fontSize = 13.sp)
                }
                // Weekly notification toggle
                var notifEnabled by remember { mutableStateOf(false) }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Switch(
                        checked = notifEnabled,
                        onCheckedChange = { on ->
                            notifEnabled = on
                            if (on) scheduleWeeklyInsight(context)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor  = Color.Black,
                            checkedTrackColor  = Accent
                        )
                    )
                    Text("Weekly\nalert", fontSize = 9.sp, color = TextMuted,
                        textAlign = TextAlign.Center)
                }
            }
        }

        // ── Weekly summary card ───────────────────────────────────────────────
        item { WeeklySummaryCard(summary) }

        // ── Potential savings banner ──────────────────────────────────────────
        if (totalSavings > 0) {
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF0D2A1F), Color(0xFF081A13)))
                        )
                        .border(1.dp, Accent.copy(0.5f), RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("💰", fontSize = 36.sp)
                        Column {
                            Text("Potential monthly savings",
                                fontSize = 12.sp, color = TextMuted)
                            Text("₹%.0f".format(totalSavings),
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.Black,
                                color      = Accent)
                            Text("If you follow all tips below",
                                fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            }
        }

        // ── Insights count by type ────────────────────────────────────────────
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InsightTypeChip(
                    "🚨", "${insights.count { it.type == InsightType.WARNING }}",
                    "Warnings", RedPill, Modifier.weight(1f)
                )
                InsightTypeChip(
                    "💡", "${insights.count { it.type == InsightType.TIP }}",
                    "Tips", Gold, Modifier.weight(1f)
                )
                InsightTypeChip(
                    "🎉", "${insights.count { it.type == InsightType.POSITIVE }}",
                    "Positive", Accent, Modifier.weight(1f)
                )
                InsightTypeChip(
                    "📊", "${insights.count { it.type == InsightType.PATTERN || it.type == InsightType.COMPARISON }}",
                    "Patterns", AccentBlue, Modifier.weight(1f)
                )
            }
        }

        // ── Insights list ─────────────────────────────────────────────────────
        if (insights.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🎯", fontSize = 48.sp)
                        Text("No insights yet",
                            color = TextMuted, fontSize = 16.sp,
                            fontWeight = FontWeight.Medium)
                        Text("Add more expenses to get\npersonalized insights",
                            color = TextMuted, fontSize = 13.sp,
                            textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            item {
                Text("${insights.size} Insights for You",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            items(insights) { insight ->
                InsightCard(insight)
            }
        }

        // ── AI tips section ───────────────────────────────────────────────────
        item {
            Text("💡 General Money Tips",
                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        item { GeneralTipsCard() }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Weekly summary card ───────────────────────────────────────────────────────
@Composable
fun WeeklySummaryCard(summary: WeeklySummary) {
    val vsColor = if (summary.vsLastWeek <= 0) Accent else RedPill
    val vsIcon  = if (summary.vsLastWeek <= 0) "📉" else "📈"
    val vsText  = if (summary.vsLastWeek <= 0)
        "%.0f%% less than last week".format(-summary.vsLastWeek)
    else
        "%.0f%% more than last week".format(summary.vsLastWeek)

    Card(
        Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, Purple.copy(0.4f))
    ) {
        Column(
            Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("This Week",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(vsIcon, fontSize = 14.sp)
                    Text(vsText, fontSize = 12.sp, color = vsColor,
                        fontWeight = FontWeight.SemiBold)
                }
            }

            // Big total
            Text("₹%.2f".format(summary.totalSpent),
                fontSize   = 36.sp,
                fontWeight = FontWeight.Black,
                color      = Purple)

            // Stats grid
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeeklyStatItem("📊", "₹%.0f/day".format(summary.avgPerDay),
                    "Daily avg", Modifier.weight(1f))
                WeeklyStatItem("🧾", "${summary.transactionCount}",
                    "Transactions", Modifier.weight(1f))
                WeeklyStatItem("🔮", "₹%.0f".format(summary.projectedMonthly),
                    "Projected", Modifier.weight(1f))
            }

            HorizontalDivider(color = BorderDark)

            // Top stats
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (summary.highestDay.isNotBlank()) {
                    Box(
                        Modifier.weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(RedAlpha)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("📅 Highest Day",
                                fontSize = 10.sp, color = TextMuted)
                            Text(summary.highestDay,
                                fontSize = 13.sp, color = RedPill,
                                fontWeight = FontWeight.Bold)
                            Text("₹%.0f".format(summary.highestDayAmount),
                                fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
                if (summary.topCategory.isNotBlank()) {
                    Box(
                        Modifier.weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldAlpha)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("🏆 Top Category",
                                fontSize = 10.sp, color = TextMuted)
                            Text(summary.topCategory,
                                fontSize = 13.sp, color = Gold,
                                fontWeight = FontWeight.Bold)
                            Text("₹%.0f".format(summary.topCategoryAmount),
                                fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyStatItem(emoji: String, value: String, label: String, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(10.dp)).background(Card2Dark).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 16.sp)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, fontSize = 10.sp, color = TextMuted)
    }
}

// ── Individual insight card ───────────────────────────────────────────────────
@Composable
fun InsightCard(insight: SpendingInsight) {
    var expanded by remember { mutableStateOf(false) }

    val (cardColor, borderColor, bgColor) = when (insight.type) {
        InsightType.WARNING    -> Triple(RedPill, RedPill.copy(0.4f), RedAlpha)
        InsightType.TIP        -> Triple(Gold, Gold.copy(0.4f), GoldAlpha)
        InsightType.POSITIVE   -> Triple(Accent, Accent.copy(0.4f), AccentAlpha)
        InsightType.PATTERN    -> Triple(AccentBlue, AccentBlue.copy(0.4f), BlueAlpha)
        InsightType.COMPARISON -> Triple(Purple, Purple.copy(0.4f), PurpleAlpha)
        InsightType.PREDICTION -> Triple(Purple, Purple.copy(0.4f), PurpleAlpha)
    }

    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Priority indicator
            Box(
                Modifier.size(42.dp).clip(CircleShape).background(cardColor.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(insight.emoji, fontSize = 20.sp)
            }

            Column(Modifier.weight(1f)) {
                Text(insight.title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary)
                Text(insight.description,
                    fontSize = 12.sp,
                    color    = TextMuted,
                    maxLines = if (expanded) Int.MAX_VALUE else 2)
            }

            Column(horizontalAlignment = Alignment.End) {
                if (insight.savingAmount > 0) {
                    Text("Save ₹%.0f".format(insight.savingAmount),
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Accent)
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null,
                    tint     = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Expanded action tip
        AnimatedVisibility(visible = expanded) {
            Column(
                Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider(color = borderColor)
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment     = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🎯", fontSize = 16.sp)
                    Column {
                        Text("Action Tip",
                            fontSize = 11.sp, color = TextMuted)
                        Text(insight.actionTip,
                            fontSize   = 13.sp,
                            color      = TextPrimary,
                            fontWeight = FontWeight.Medium)
                    }
                }

                // Priority badge
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PriorityBadge(insight.priority)
                    TypeBadge(insight.type, cardColor)
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: InsightPriority) {
    val (color, label) = when (priority) {
        InsightPriority.HIGH   -> RedPill to "High Priority"
        InsightPriority.MEDIUM -> Gold to "Medium"
        InsightPriority.LOW    -> TextMuted to "Low"
    }
    Box(
        Modifier.clip(RoundedCornerShape(100.dp))
            .background(color.copy(0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 10.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TypeBadge(type: InsightType, color: Color) {
    val label = when (type) {
        InsightType.WARNING    -> "⚠️ Warning"
        InsightType.TIP        -> "💡 Tip"
        InsightType.POSITIVE   -> "✅ Positive"
        InsightType.PATTERN    -> "📊 Pattern"
        InsightType.COMPARISON -> "📈 Comparison"
        InsightType.PREDICTION -> "🔮 Prediction"
    }
    Box(
        Modifier.clip(RoundedCornerShape(100.dp))
            .background(color.copy(0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, fontSize = 10.sp, color = color)
    }
}

// ── Insight type chip ─────────────────────────────────────────────────────────
@Composable
fun InsightTypeChip(emoji: String, count: String, label: String, color: Color, modifier: Modifier) {
    Box(
        modifier.clip(RoundedCornerShape(12.dp)).background(color.copy(0.1f))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(12.dp)).padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(emoji, fontSize = 16.sp)
            Text(count, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 9.sp, color = TextMuted, textAlign = TextAlign.Center)
        }
    }
}

// ── General money tips card ───────────────────────────────────────────────────
@Composable
fun GeneralTipsCard() {
    val tips = listOf(
        Triple("💊", "Medicine budget", "Set aside ₹2000/month fixed for your hair medicines. Never borrow from this fund."),
        Triple("🥗", "Meal prep Sunday", "Cook 3-4 days food on Sunday. Saves ₹1500-2000/month on dining out."),
        Triple("📱", "UPI cashback", "Use Google Pay/PhonePe UPI for all payments. Cashback adds up to ₹200-500/month."),
        Triple("🚗", "Auto/transport", "Use BMTC pass or share auto. Can save ₹800-1200/month in Bengaluru."),
        Triple("💧", "Water bottle", "Carry water bottle. Stop buying packaged water — saves ₹300-500/month."),
        Triple("🏋️", "Workout at home", "You have a workout plan already! Home workout = ₹0. Gym = ₹1000-2000/month saved."),
        Triple("📊", "50-30-20 rule", "50% needs, 30% wants, 20% savings. Ideal budget split for financial health."),
        Triple("🎯", "Emergency fund", "Save 3 months of expenses (₹15,000-20,000) before investing anywhere.")
    )

    Card(
        Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            tips.forEach { (emoji, title, tip) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment     = Alignment.Top
                ) {
                    Box(
                        Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                            .background(AccentAlpha),
                        contentAlignment = Alignment.Center
                    ) { Text(emoji, fontSize = 18.sp) }
                    Column(Modifier.weight(1f)) {
                        Text(title,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = TextPrimary)
                        Text(tip,
                            fontSize = 12.sp,
                            color    = TextMuted)
                    }
                }
                if (emoji != "🎯") HorizontalDivider(color = BorderDark.copy(0.5f))
            }
        }
    }
}