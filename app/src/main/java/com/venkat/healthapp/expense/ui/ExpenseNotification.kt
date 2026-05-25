package com.venkat.healthapp.expense.ui

import android.app.*
import android.content.*
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

const val EXPENSE_NOTE_CHANNEL = "expense_note_reminder"

class ExpenseNoteReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val expenseId    = intent.getIntExtra("expenseId", 0)
        val expenseTitle = intent.getStringExtra("title") ?: "an expense"
        val amount       = intent.getFloatExtra("amount", 0f)

        // Create channel
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ch = NotificationChannel(
            EXPENSE_NOTE_CHANNEL,
            "Expense Note Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminds you to add notes to expenses"
            enableVibration(true)
        }
        nm.createNotificationChannel(ch)

        // Launch intent to open Add Note tab
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("openExpenseNotes", true)
            }
        val pi = PendingIntent.getActivity(
            context, expenseId, launchIntent ?: Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(context, EXPENSE_NOTE_CHANNEL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📝 Don't forget to add a note!")
            .setContentText("You spent ₹%.0f on %s — add details now".format(amount, expenseTitle))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(
                    "You said you'd add notes later!\n" +
                            "Expense: $expenseTitle\n" +
                            "Amount: ₹%.2f\n".format(amount) +
                            "Tap to add your note now before you forget!"
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(expenseId + 7000, notif)
        }
    }
}

// ── Schedule note reminder ────────────────────────────────────────────────────
fun scheduleNoteReminder(
    context: Context,
    expenseId: Int,
    title: String,
    amount: Float,
    delayMillis: Long = 2 * 60 * 60 * 1000L  // 2 hours default
) {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ExpenseNoteReminderReceiver::class.java).apply {
        putExtra("expenseId", expenseId)
        putExtra("title",     title)
        putExtra("amount",    amount)
    }
    val pi = PendingIntent.getBroadcast(
        context,
        expenseId + 6000,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    am.setAlarmClock(
        AlarmManager.AlarmClockInfo(System.currentTimeMillis() + delayMillis, pi),
        pi
    )
}

fun cancelNoteReminder(context: Context, expenseId: Int) {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ExpenseNoteReminderReceiver::class.java)
    val pi = PendingIntent.getBroadcast(
        context,
        expenseId + 6000,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    am.cancel(pi)
}