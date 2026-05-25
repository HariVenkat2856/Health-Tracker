package com.venkat.healthapp.common

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val BgDark       = Color(0xFF0D1117)
val CardDark     = Color(0xFF161B22)
val Card2Dark    = Color(0xFF1C2330)
val BorderDark   = Color(0xFF30363D)
val Accent       = Color(0xFF00C896)
val AccentBlue   = Color(0xFF00A3FF)
val Gold         = Color(0xFFF0B429)
val RedPill      = Color(0xFFFF4D6D)
val Purple       = Color(0xFFB57AFF)
val TextPrimary  = Color(0xFFE6EDF3)
val TextMuted    = Color(0xFF8B949E)
val AccentAlpha  = Color(0x2200C896)
val BlueAlpha    = Color(0x2200A3FF)
val GoldAlpha    = Color(0x22F0B429)
val RedAlpha     = Color(0x22FF4D6D)
val PurpleAlpha  = Color(0x22B57AFF)

private val DarkColors = darkColorScheme(
    primary          = Accent,
    onPrimary        = Color.Black,
    primaryContainer = AccentAlpha,
    secondary        = AccentBlue,
    background       = BgDark,
    onBackground     = TextPrimary,
    surface          = CardDark,
    onSurface        = TextPrimary,
    surfaceVariant   = Card2Dark,
    onSurfaceVariant = TextMuted,
    outline          = BorderDark,
    error            = RedPill,
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Black,    fontSize = 32.sp, color = TextPrimary),
    titleLarge   = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 22.sp, color = TextPrimary),
    titleMedium  = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = TextPrimary),
    bodyLarge    = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 15.sp, color = TextPrimary),
    bodyMedium   = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 13.sp, color = TextMuted),
    bodySmall    = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 12.sp, color = TextMuted),
    labelLarge   = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary),
    labelMedium  = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 12.sp, color = TextPrimary),
    labelSmall   = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 11.sp, color = TextPrimary, letterSpacing = 0.5.sp),
)

@Composable
fun HealthAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, typography = AppTypography) {
        CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides TextPrimary,
            content = content
        )
    }
}
