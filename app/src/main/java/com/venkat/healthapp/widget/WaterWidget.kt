package com.venkat.healthapp.widget

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
import com.venkat.healthapp.water.data.WaterLog
import kotlinx.coroutines.*

class WaterWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { updateWaterWidget(context, appWidgetManager, it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_ADD_250 -> addWaterAndUpdate(context, 250)
            ACTION_ADD_500 -> addWaterAndUpdate(context, 500)
            ACTION_REFRESH -> refreshAll(context)
        }
    }

    private fun addWaterAndUpdate(context: Context, ml: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.get(context)
            db.waterLogDao().insert(WaterLog(date = currentDate(), amountMl = ml))
            val awm = AppWidgetManager.getInstance(context)
            val ids = awm.getAppWidgetIds(
                android.content.ComponentName(context, WaterWidget::class.java)
            )
            ids.forEach { updateWaterWidget(context, awm, it) }
        }
    }

    private fun refreshAll(context: Context) {
        val awm = AppWidgetManager.getInstance(context)
        val ids = awm.getAppWidgetIds(
            android.content.ComponentName(context, WaterWidget::class.java)
        )
        ids.forEach { updateWaterWidget(context, awm, it) }
    }

    companion object {
        const val ACTION_ADD_250 = "com.venkat.healthapp.WATER_ADD_250"
        const val ACTION_ADD_500 = "com.venkat.healthapp.WATER_ADD_500"
        const val ACTION_REFRESH = "com.venkat.healthapp.WATER_REFRESH"

        fun updateWaterWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            widgetId: Int
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                val db      = AppDatabase.get(context)
                val today   = currentDate()
                val total   = db.waterLogDao().totalForDateSync(today) ?: 0
                val target  = 3000
                val pct     = ((total.toFloat() / target) * 100).toInt().coerceIn(0, 100)
                val remaining = (target - total).coerceAtLeast(0)

                withContext(Dispatchers.Main) {
                    val views = RemoteViews(context.packageName, R.layout.widget_water)

                    views.setTextViewText(R.id.water_total, "${total}ml")
                    views.setTextViewText(R.id.water_target, "/ ${target}ml")
                    views.setTextViewText(R.id.water_remaining,
                        if (total >= target) "✅ Goal complete!" else "${remaining}ml more to go")
                    views.setProgressBar(R.id.water_progress, 100, pct, false)

                    // Open app
                    val openApp = PendingIntent.getActivity(
                        context, 0,
                        Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            putExtra("openWater", true)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.water_root, openApp)

                    // Add 250ml
                    val add250 = PendingIntent.getBroadcast(
                        context, 1,
                        Intent(context, WaterWidget::class.java).apply {
                            action = ACTION_ADD_250
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.water_add_250, add250)

                    // Add 500ml
                    val add500 = PendingIntent.getBroadcast(
                        context, 2,
                        Intent(context, WaterWidget::class.java).apply {
                            action = ACTION_ADD_500
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.water_add_500, add500)

                    appWidgetManager.updateAppWidget(widgetId, views)
                }
            }
        }
    }
}