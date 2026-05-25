package com.venkat.healthapp.water.ui

import android.app.*
import android.content.*
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

object WaterAlarmChannels {
    const val CHANNEL = "water_reminder_channel"
}

// ── BroadcastReceiver — fires every hour ──────────────────────────────────────
class WaterReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        createChannel(context)

        // ── Auto-reduce water in DB (250ml per hour assumed drunk) ────────────
        // We write directly to shared prefs so MainViewModel picks it up
        // The ViewModel's addWater() uses Room — we trigger it via a service intent
        val autoReduceIntent = Intent(context, WaterAutoReduceReceiver::class.java)
        context.sendBroadcast(autoReduceIntent)

        // ── Fire the reminder notification ────────────────────────────────────
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }
        val pi = PendingIntent.getActivity(
            context, 5001, launchIntent ?: Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, WaterAlarmChannels.CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("💧 Time to Drink Water!")
            .setContentText("1 hour passed! Drink 250ml now to stay on track.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("⏰ One hour has passed!\n💧 Drink at least 250ml now.\n" +
                         "Staying hydrated helps hair growth and overall health!"))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setLights(0xFF00A3FF.toInt(), 500, 500)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(5001, notif)
        }

        // ── Reschedule next alarm 1 hour from now ─────────────────────────────
        WaterAlarmScheduler.scheduleNext(context)
    }

    private fun createChannel(context: Context) {
        val ch = NotificationChannel(
            WaterAlarmChannels.CHANNEL,
            "Water Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Hourly water drinking reminders"
            enableVibration(true)
            enableLights(true)
        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(ch)
    }
}

// ── Auto-reduce receiver — writes 250ml drunk to shared prefs ─────────────────
// MainViewModel reads a "pending water" value on resume and inserts to Room
class WaterAutoReduceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Store pending auto-reduce in SharedPreferences
        // MainViewModel picks this up and calls addWater()
        val prefs = context.getSharedPreferences("water_auto", Context.MODE_PRIVATE)
        val current = prefs.getInt("pending_ml", 0)
        prefs.edit().putInt("pending_ml", current + 250).apply()
    }
}

// ── Boot receiver ─────────────────────────────────────────────────────────────
class WaterBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            if (WaterAlarmScheduler.isEnabled(context)) {
                WaterAlarmScheduler.scheduleNext(context)
            }
        }
    }
}

// ── Scheduler ─────────────────────────────────────────────────────────────────
object WaterAlarmScheduler {

    fun enable(context: Context) {
        context.getSharedPreferences("water_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("water_alarm_enabled", true).apply()
        scheduleNext(context)
    }

    fun disable(context: Context) {
        context.getSharedPreferences("water_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("water_alarm_enabled", false).apply()
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(buildPi(context))
    }

    fun isEnabled(context: Context): Boolean =
        context.getSharedPreferences("water_prefs", Context.MODE_PRIVATE)
            .getBoolean("water_alarm_enabled", false)

    fun scheduleNext(context: Context) {
        if (!isEnabled(context)) return
        val am  = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Fire exactly 1 hour from now
        val triggerAt = System.currentTimeMillis() + 60L * 60L * 1000L
        // setAlarmClock = fires even in Doze/battery saver/app closed
        am.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerAt, buildPi(context)),
            buildPi(context)
        )
    }

    private fun buildPi(context: Context): PendingIntent {
        val intent = Intent(context, WaterReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, 5000, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
