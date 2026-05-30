package com.venkat.healthapp.expense.insights

import android.app.*
import android.content.*
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

const val INSIGHT_CHANNEL = "spending_insights"

// ── Broadcast receiver — fires weekly ────────────────────────────────────────
class InsightNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title   = intent.getStringExtra("title") ?: "Weekly Spending Insight"
        val message = intent.getStringExtra("message") ?: "Check your spending summary"

        createChannel(context)

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }
        val pi = PendingIntent.getActivity(
            context, 9001, launchIntent ?: Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, INSIGHT_CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(9001, notif)
        }

        // Reschedule next week
        scheduleWeeklyInsight(context)
    }

    private fun createChannel(context: Context) {
        val ch = NotificationChannel(
            INSIGHT_CHANNEL,
            "Spending Insights",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Weekly spending analysis and tips" }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(ch)
    }
}

// ── Schedule weekly insight — every Sunday 8 PM ───────────────────────────────
fun scheduleWeeklyInsight(
    context: Context,
    title: String   = "💡 Your Weekly Spending Insight",
    message: String = "Tap to see where your money went this week"
) {
    val am  = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val cal = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        set(Calendar.HOUR_OF_DAY, 20)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) add(Calendar.WEEK_OF_YEAR, 1)
    }

    val intent = Intent(context, InsightNotificationReceiver::class.java).apply {
        putExtra("title",   title)
        putExtra("message", message)
    }
    val pi = PendingIntent.getBroadcast(
        context, 9001, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    am.setAlarmClock(AlarmManager.AlarmClockInfo(cal.timeInMillis, pi), pi)
}