package com.venkat.healthapp.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context

// ── Call this after any data change to refresh all widgets ───────────────────
object WidgetUpdater {

    fun updateAll(context: Context) {
        val awm = AppWidgetManager.getInstance(context)

        // Update expense widgets
        awm.getAppWidgetIds(ComponentName(context, ExpenseWidget::class.java))
            .forEach { ExpenseWidget.updateWidget(context, awm, it) }

        // Update water widgets
        awm.getAppWidgetIds(ComponentName(context, WaterWidget::class.java))
            .forEach { WaterWidget.updateWaterWidget(context, awm, it) }

        // Update hair widgets
        awm.getAppWidgetIds(ComponentName(context, HairWidget::class.java))
            .forEach { HairWidget.updateHairWidget(context, awm, it) }
    }

    fun updateExpense(context: Context) {
        val awm = AppWidgetManager.getInstance(context)
        awm.getAppWidgetIds(ComponentName(context, ExpenseWidget::class.java))
            .forEach { ExpenseWidget.updateWidget(context, awm, it) }
    }

    fun updateWater(context: Context) {
        val awm = AppWidgetManager.getInstance(context)
        awm.getAppWidgetIds(ComponentName(context, WaterWidget::class.java))
            .forEach { WaterWidget.updateWaterWidget(context, awm, it) }
    }

    fun updateHair(context: Context) {
        val awm = AppWidgetManager.getInstance(context)
        awm.getAppWidgetIds(ComponentName(context, HairWidget::class.java))
            .forEach { HairWidget.updateHairWidget(context, awm, it) }
    }
}