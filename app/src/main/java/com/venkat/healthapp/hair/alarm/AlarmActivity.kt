package com.venkat.healthapp.hair.alarm

import android.app.KeyguardManager
import android.content.*
import android.media.*
import android.os.*
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.app.NotificationManagerCompat
import com.venkat.healthapp.common.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmActivity : ComponentActivity() {

    companion object {
        const val ACTION_DISMISS = "com.venkat.healthapp.ALARM_DISMISS"
    }

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var notifId = 0

    // Broadcast receiver to dismiss from notification button
    private val dismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_DISMISS) stopAndFinish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ── Wake screen and show over lock screen ─────────────────────────────
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            km.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON        or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD      or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED      or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val title   = intent.getStringExtra("title")   ?: "Hair Treatment Alarm"
        val message = intent.getStringExtra("message") ?: "Time for your medicines!"
        val slotKey = intent.getStringExtra("slotKey") ?: ""
        notifId     = intent.getIntExtra("notifId", 0)

        // ── Start ringtone ────────────────────────────────────────────────────
        startRingtone()

        // ── Start vibration ───────────────────────────────────────────────────
        startVibration()

        // ── Register dismiss receiver ─────────────────────────────────────────
        registerReceiver(dismissReceiver, IntentFilter(ACTION_DISMISS))

        // ── Auto-dismiss after 60 seconds ─────────────────────────────────────
        Handler(Looper.getMainLooper()).postDelayed({ stopAndFinish() }, 60_000)

        setContent {
            HealthAppTheme {
                AlarmScreen(
                    title   = title,
                    message = message,
                    slotKey = slotKey,
                    onDismiss = { stopAndFinish() }
                )
            }
        }
    }

    private fun startRingtone() {
        try {
            // Use alarm ringtone stream — plays even in silent mode
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(this, uri)?.also { rt ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    rt.isLooping = true
                }
                rt.audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                rt.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 800, 400, 800, 400, 800, 400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopAndFinish() {
        ringtone?.stop()
        vibrator?.cancel()
        NotificationManagerCompat.from(this).cancel(notifId)
        runCatching { unregisterReceiver(dismissReceiver) }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone?.stop()
        vibrator?.cancel()
        runCatching { unregisterReceiver(dismissReceiver) }
    }

    override fun onBackPressed() {
        // Block back press — user must tap STOP
    }
}

// ── Full-screen alarm UI ──────────────────────────────────────────────────────
@Composable
fun AlarmScreen(
    title: String,
    message: String,
    slotKey: String,
    onDismiss: () -> Unit
) {
    val currentTime = remember {
        SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())
    }
    val amPm = remember {
        SimpleDateFormat("a", Locale.getDefault()).format(Date())
    }
    val dateStr = remember {
        SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date())
    }

    // Pulsing animation for the alarm icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Glow alpha animation
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val emoji = AlarmSlot.values().find { it.key == slotKey }?.emoji ?: "💊"
    val bgGradient = when (slotKey) {
        "morning"   -> listOf(Color(0xFF1A120B), Color(0xFF2C1810), Color(0xFF0D1117))
        "afternoon" -> listOf(Color(0xFF0A1628), Color(0xFF0D2040), Color(0xFF0D1117))
        "night"     -> listOf(Color(0xFF0A0A1A), Color(0xFF10103A), Color(0xFF0D1117))
        else        -> listOf(Color(0xFF0D1A0D), Color(0xFF102810), Color(0xFF0D1117))
    }
    val accentColor = when (slotKey) {
        "morning"   -> Gold
        "afternoon" -> AccentBlue
        "night"     -> Color(0xFFB57AFF)
        else        -> Accent
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(bgGradient)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // ── Top: time ─────────────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(40.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        currentTime,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        amPm,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(0.7f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                Text(
                    dateStr,
                    fontSize = 16.sp,
                    color = Color.White.copy(0.6f)
                )
            }

            // ── Center: pulsing alarm icon + title ────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Outer glow ring
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = glow * 0.25f))
                    )
                    Box(
                        Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = glow * 0.4f))
                    )
                    Box(
                        Modifier
                            .size(84.dp)
                            .scale(pulse)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emoji, fontSize = 38.sp)
                    }
                }

                Text(
                    title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Medicine list box
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(20.dp)
                ) {
                    Text(
                        message,
                        fontSize = 15.sp,
                        color = Color.White.copy(0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            // ── Bottom: STOP button ───────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Big STOP button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(120.dp)
                        .scale(pulse),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.Black
                    ),
                    elevation = ButtonDefaults.buttonElevation(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Alarm,
                            contentDescription = "Stop Alarm",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Black
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "STOP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                Text(
                    "Tap to stop alarm",
                    fontSize = 13.sp,
                    color = Color.White.copy(0.5f)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
