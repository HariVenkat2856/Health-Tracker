package com.venkat.healthapp.hair.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.venkat.healthapp.hair.data.*
import com.venkat.healthapp.common.*

// ── Progress Ring ────────────────────────────────────────────────────────────
@Composable
fun ProgressRing(
    done: Int, total: Int,
    modifier: Modifier = Modifier,
    size: Dp = 110.dp,
    strokeWidth: Dp = 10.dp
) {
    val pct = if (total == 0) 0f else done.toFloat() / total
    val animPct by animateFloatAsState(
        targetValue = pct,
        animationSpec = tween(1000, easing = FastOutSlowInEasing), label = "ring"
    )
    val ringColor = when {
        pct >= 1f -> Gold
        pct >= 0.5f -> Accent
        else -> AccentBlue
    }

    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(size)) {
            val stroke = strokeWidth.toPx()
            val radius = (this.size.minDimension - stroke) / 2f
            val center = Offset(this.size.width / 2, this.size.height / 2)

            // Background ring
            drawCircle(color = BorderDark, radius = radius, style = Stroke(stroke))

            // Progress arc
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * animPct,
                useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${(pct * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ringColor
                )
            )
            Text("DONE", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
        }
    }
}

// ── Stat Card ────────────────────────────────────────────────────────────────
@Composable
fun StatCard(
    emoji: String, value: String, label: String,
    accentColor: Color = Accent,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 26.sp)
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = accentColor, fontWeight = FontWeight.Bold, fontSize = 24.sp
                )
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ── Task Card ────────────────────────────────────────────────────────────────
@Composable
fun TaskCard(
    def: TaskDef,
    log: TaskLog?,
    onToggle: () -> Unit
) {
    val done = log?.completed == true
    val bgColor by animateColorAsState(
        if (done) Color(0xFF00C896).copy(alpha = 0.06f) else CardDark,
        animationSpec = tween(300), label = "bg"
    )
    val borderColor by animateColorAsState(
        if (done) Accent.copy(alpha = 0.3f) else BorderDark,
        animationSpec = tween(300), label = "border"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(14.dp))
            .clickable { onToggle() }
    ) {
        Row(
            Modifier.padding(14.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Checkbox
            val checkBg by animateColorAsState(
                if (done) Accent else Color.Transparent, label = "check"
            )
            Box(
                Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.5.dp, if (done) Accent else BorderDark, RoundedCornerShape(8.dp))
                    .background(checkBg),
                contentAlignment = Alignment.Center
            ) {
                if (done) {
                    Icon(
                        Icons.Default.Check, null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Info
            Column(Modifier.weight(1f)) {
                Text(
                    text = def.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (done) TextMuted else TextPrimary,
                    textDecoration = if (done)
                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                    else
                        androidx.compose.ui.text.style.TextDecoration.None
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = def.subtitle,
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            // Pill
            val (pillBg, pillFg) = when (def.pillType) {
                PillType.TABLET  -> BlueAlpha  to AccentBlue
                PillType.APPLY   -> AccentAlpha to Accent
                PillType.WASH    -> GoldAlpha   to Gold
                PillType.WEEKLY  -> RedAlpha    to RedPill
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(pillBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = def.pillLabel,
                    color = pillFg,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ── Section Header ───────────────────────────────────────────────────────────
@Composable
fun SectionHeader(section: Section, done: Int, total: Int) {
    val iconBg = when (section) {
        Section.MORNING   -> GoldAlpha
        Section.AFTERNOON -> BlueAlpha
        Section.NIGHT     -> Color(0x227C3AED)
        Section.WEEKLY    -> RedAlpha
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(section.emoji, fontSize = 18.sp)
        }
        Text(
            section.label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 15.sp,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.weight(1f)
        )
        Box(
            Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(Card2Dark)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                "$done/$total",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Shampoo Selector ─────────────────────────────────────────────────────────
@Composable
fun ShampooSelector(selected: String, onSelect: (String) -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Card2Dark, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                "🚿  Hair Wash Today",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp, color = TextPrimary
                )
            )
            Text(
                "Alternate shampoos each day",
                color = TextMuted,
                fontSize = 12.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("kone", "K-One CT", Gold),
                    Triple("hairex", "Hairex", Accent),
                    Triple("skip", "No Wash", TextMuted)
                ).forEach { (key, label, color) ->
                    val active = selected == key
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, if (active) color else BorderDark, RoundedCornerShape(8.dp))
                            .background(if (active) color.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { onSelect(key) }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (active) color else TextMuted,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
