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
import kotlinx.coroutines.*

class HairWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { updateHairWidget(context, appWidgetManager, it) }
    }

    companion object {
        fun updateHairWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            widgetId: Int
        ) {
            // Immediate placeholder
            val views = RemoteViews(context.packageName, R.layout.widget_hair)
            views.setTextViewText(R.id.hair_done, "--/13")
            views.setTextViewText(R.id.hair_pct, "--")
            views.setTextViewText(R.id.hair_status, "Loading...")
            setupClicks(context, views)
            appWidgetManager.updateAppWidget(widgetId, views)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db    = AppDatabase.get(context)
                    val today = currentDate()
                    val tasks = db.taskLogDao().getTasksForDateSync(today)
                    val done  = tasks.count { it.completed }
                    val total = if (tasks.isNotEmpty()) tasks.size else 13
                    val pct   = if (total > 0) (done * 100 / total) else 0

                    withContext(Dispatchers.Main) {
                        val updated = RemoteViews(context.packageName, R.layout.widget_hair)
                        updated.setTextViewText(R.id.hair_done, "$done/$total")
                        updated.setTextViewText(R.id.hair_pct, "$pct%")
                        updated.setTextViewText(R.id.hair_status,
                            when {
                                pct == 100 -> "✅ All done!"
                                pct >= 70  -> "💊 Almost done"
                                else       -> "💊 Medicines due"
                            }
                        )
                        updated.setProgressBar(R.id.hair_progress, 100, pct, false)
                        setupClicks(context, updated)
                        appWidgetManager.updateAppWidget(widgetId, updated)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun setupClicks(context: Context, views: RemoteViews) {
            val openApp = PendingIntent.getActivity(
                context, 10,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("openHair", true)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.hair_root, openApp)
            views.setOnClickPendingIntent(R.id.hair_open_btn, openApp)
        }
    }
}