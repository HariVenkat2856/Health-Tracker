package com.venkat.healthapp.hair.alarm

import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

// ── Channel IDs ───────────────────────────────────────────────────────────────
object Channels {
    const val ALARM = "hair_alarm_channel"
}

// ── Alarm Slots ───────────────────────────────────────────────────────────────
enum class AlarmSlot(
    val key: String,
    val label: String,
    val emoji: String,
    val notifTitle: String,
    val notifMessage: String,
    val defaultHour: Int,
    val defaultMinute: Int,
    val alarmId: Int,
    val notifId: Int,
    val isWeekly: Boolean = false
) {
    MORNING(
        key = "morning", label = "Morning Medicines", emoji = "🌅",
        notifTitle = "🌅 Morning Treatment Time!",
        notifMessage = "T.Neurotec Plus • T.Trichotex • T.Biotree • Cufin Powder • Rebrote FX",
        defaultHour = 8, defaultMinute = 0, alarmId = 1001, notifId = 2001
    ),
    AFTERNOON(
        key = "afternoon", label = "Afternoon Tablet", emoji = "🌤",
        notifTitle = "🌤 Afternoon Reminder!",
        notifMessage = "Take T.Rucal CM (Calcium tablet) after lunch.",
        defaultHour = 13, defaultMinute = 0, alarmId = 1002, notifId = 2002
    ),
    NIGHT(
        key = "night", label = "Night Medicines", emoji = "🌙",
        notifTitle = "🌙 Night Treatment Time!",
        notifMessage = "T.Sandro • T.Minodez 5mg • T.Biotree • Rebrote FX before bed",
        defaultHour = 21, defaultMinute = 0, alarmId = 1003, notifId = 2003
    ),
    WEEKLY(
        key = "weekly", label = "Weekly — Derma Roller Day", emoji = "📆",
        notifTitle = "📆 Weekly Treatment Day!",
        notifMessage = "Derma Roller + Stemcello + T.Trip-D (Vitamin D3). Don't skip!",
        defaultHour = 9, defaultMinute = 0, alarmId = 1004, notifId = 2004,
        isWeekly = true
    )
}

// ── BroadcastReceiver — fires when AlarmManager triggers ─────────────────────
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val slotKey = intent.getStringExtra("slotKey") ?: return
        val title   = intent.getStringExtra("title")   ?: "Hair Treatment Alarm"
        val message = intent.getStringExtra("message") ?: "Time for your medicines!"
        val notifId = intent.getIntExtra("notifId", 0)

        createChannel(context)

        // ── Launch AlarmActivity (full-screen, wakes phone) ───────────────────
        val actIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("slotKey", slotKey)
            putExtra("title",   title)
            putExtra("message", message)
            putExtra("notifId", notifId)
        }

        val fullScreenPi = PendingIntent.getActivity(
            context, notifId, actIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, AlarmDismissReceiver::class.java).apply {
            putExtra("notifId", notifId)
            putExtra("slotKey", slotKey)
        }
        val dismissPi = PendingIntent.getBroadcast(
            context, notifId + 9000, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, Channels.ALARM)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPi, true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", dismissPi)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(notifId, notif)
        }

        // Start activity directly — wakes screen even when locked
        context.startActivity(actIntent)

        // Re-schedule for next occurrence
        AlarmScheduler.rescheduleNext(context, slotKey)
    }

    private fun createChannel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ch = NotificationChannel(
            Channels.ALARM,
            "Hair Treatment Alarms",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description       = "Ringing alarm for medicine reminders"
            enableVibration(true)
            vibrationPattern  = longArrayOf(0, 600, 300, 600, 300, 600)
            enableLights(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setBypassDnd(true)
        }
        nm.createNotificationChannel(ch)
    }
}

// ── Dismiss receiver ──────────────────────────────────────────────────────────
class AlarmDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notifId = intent.getIntExtra("notifId", 0)
        NotificationManagerCompat.from(context).cancel(notifId)
        context.sendBroadcast(Intent(AlarmActivity.ACTION_DISMISS))
    }
}

// ── Boot receiver ─────────────────────────────────────────────────────────────
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            AlarmSlot.values().forEach { slot ->
                val hour    = prefs.getInt("${slot.key}_hour", -1)
                val minute  = prefs.getInt("${slot.key}_minute", -1)
                val enabled = prefs.getBoolean("${slot.key}_enabled", false)
                if (enabled && hour >= 0) AlarmScheduler.schedule(context, slot, hour, minute)
            }
        }
    }
}

// ── Scheduler ─────────────────────────────────────────────────────────────────
object AlarmScheduler {

    fun schedule(context: Context, slot: AlarmSlot, hour: Int, minute: Int) {
        val am  = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                if (slot.isWeekly) add(Calendar.WEEK_OF_YEAR, 1)
                else               add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        if (slot.isWeekly) cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val pi = buildPendingIntent(context, slot)

        // setAlarmClock = strongest type: fires in Doze/battery-saver, shows alarm icon in status bar
        am.setAlarmClock(AlarmManager.AlarmClockInfo(cal.timeInMillis, pi), pi)

        context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE).edit()
            .putInt("${slot.key}_hour", hour)
            .putInt("${slot.key}_minute", minute)
            .putBoolean("${slot.key}_enabled", true)
            .apply()
    }

    fun rescheduleNext(context: Context, slotKey: String) {
        val slot    = AlarmSlot.values().find { it.key == slotKey } ?: return
        val prefs   = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val hour    = prefs.getInt("${slot.key}_hour", slot.defaultHour)
        val minute  = prefs.getInt("${slot.key}_minute", slot.defaultMinute)
        val enabled = prefs.getBoolean("${slot.key}_enabled", false)
        if (enabled) schedule(context, slot, hour, minute)
    }

    fun cancel(context: Context, slot: AlarmSlot) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(buildPendingIntent(context, slot))
        context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE).edit()
            .putBoolean("${slot.key}_enabled", false).apply()
    }

    fun isEnabled(context: Context, slot: AlarmSlot) =
        context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
            .getBoolean("${slot.key}_enabled", false)

    fun getSavedTime(context: Context, slot: AlarmSlot): Pair<Int, Int> {
        val p = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        return Pair(p.getInt("${slot.key}_hour", slot.defaultHour),
                    p.getInt("${slot.key}_minute", slot.defaultMinute))
    }

    private fun buildPendingIntent(context: Context, slot: AlarmSlot): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("slotKey", slot.key)
            putExtra("title",   slot.notifTitle)
            putExtra("message", slot.notifMessage)
            putExtra("notifId", slot.notifId)
        }
        return PendingIntent.getBroadcast(
            context, slot.alarmId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
