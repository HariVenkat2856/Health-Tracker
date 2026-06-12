package com.venkat.healthapp.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.widget.RemoteViews
import com.venkat.healthapp.R
import com.venkat.healthapp.MainActivity
import com.venkat.healthapp.common.AppDatabase
import com.venkat.healthapp.expense.data.currentDate
import com.venkat.healthapp.expense.data.currentMonth
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class ExpenseWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { widgetId ->
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_REFRESH -> {
                val awm = AppWidgetManager.getInstance(context)
                val ids = awm.getAppWidgetIds(
                    android.content.ComponentName(context, ExpenseWidget::class.java)
                )
                ids.forEach { updateWidget(context, awm, it) }
            }
            ACTION_QUICK_ADD -> {
                val launch = Intent(context, MainActivity::class.java).apply {
                    flags  = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("openQuickAdd", true)
                }
                context.startActivity(launch)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH   = "com.venkat.healthapp.WIDGET_REFRESH"
        const val ACTION_QUICK_ADD = "com.venkat.healthapp.WIDGET_QUICK_ADD"

        @SuppressLint("RemoteViewLayout")
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            widgetId: Int
        ) {
            // Show a basic layout IMMEDIATELY so widget doesn't show error
            val views = RemoteViews(context.packageName, R.layout.widget_expense)
            views.setTextViewText(R.id.widget_today_amount, "₹--")
            views.setTextViewText(R.id.widget_today_label, "Loading...")
            views.setTextViewText(R.id.widget_month_amount, "₹--")
            views.setTextViewText(R.id.widget_budget_text, "Loading...")
            views.setTextViewText(R.id.widget_status, "")

            val dateStr = SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(Date())
            views.setTextViewText(R.id.widget_date, dateStr)

            setupClickListeners(context, views)
            appWidgetManager.updateAppWidget(widgetId, views)

            // Then load real data and update again
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.get(context)
                    val today = currentDate()
                    val month = currentMonth()

                    val todayTotal = db.expenseDao().totalForDateSync(today) ?: 0f
                    val monthTotal = db.expenseDao().totalForMonthSync(month) ?: 0f
                    val todayCount = db.expenseDao().countForDateSync(today)

                    val budget = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
                        .getFloat("monthly_budget", 10000f)
                    val progress = ((monthTotal / budget) * 100).toInt().coerceIn(0, 100)

                    val statusMsg = when {
                        progress >= 90 -> "⚠️ Near limit!"
                        progress >= 70 -> "📊 On track"
                        else           -> "✅ Good"
                    }

                    withContext(Dispatchers.Main) {
                        val updatedViews = RemoteViews(context.packageName, R.layout.widget_expense)
                        updatedViews.setTextViewText(R.id.widget_date, dateStr)
                        updatedViews.setTextViewText(R.id.widget_today_amount, "₹%.2f".format(todayTotal))
                        updatedViews.setTextViewText(R.id.widget_today_label, "Today • $todayCount transactions")
                        updatedViews.setTextViewText(R.id.widget_month_amount, "₹%.0f".format(monthTotal))
                        updatedViews.setProgressBar(R.id.widget_progress, 100, progress, false)
                        updatedViews.setTextViewText(R.id.widget_budget_text, "₹%.0f / ₹%.0f".format(monthTotal, budget))
                        updatedViews.setTextViewText(R.id.widget_status, statusMsg)

                        setupClickListeners(context, updatedViews)
                        appWidgetManager.updateAppWidget(widgetId, updatedViews)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun setupClickListeners(context: Context, views: RemoteViews) {
            val openApp = PendingIntent.getActivity(
                context, 0,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openApp)

            val quickAdd = PendingIntent.getBroadcast(
                context, 1,
                Intent(context, ExpenseWidget::class.java).apply { action = ACTION_QUICK_ADD },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_add_btn, quickAdd)

            val refresh = PendingIntent.getBroadcast(
                context, 2,
                Intent(context, ExpenseWidget::class.java).apply { action = ACTION_REFRESH },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh_btn, refresh)
        }
    }
}