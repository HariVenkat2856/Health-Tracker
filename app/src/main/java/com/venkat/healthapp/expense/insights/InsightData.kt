package com.venkat.healthapp.expense.insights

// ── Insight types ─────────────────────────────────────────────────────────────
enum class InsightType {
    WARNING,    // spending too much
    TIP,        // save money suggestion
    POSITIVE,   // good behavior
    PATTERN,    // spending pattern detected
    COMPARISON, // this week vs last week
    PREDICTION  // predicted spending
}

enum class InsightPriority { HIGH, MEDIUM, LOW }

// ── Single insight model ──────────────────────────────────────────────────────
data class SpendingInsight(
    val id: String,
    val type: InsightType,
    val priority: InsightPriority,
    val emoji: String,
    val title: String,
    val description: String,
    val actionTip: String = "",
    val savingAmount: Float = 0f,  // potential saving in ₹
    val relatedCategory: String = ""
)

// ── Weekly summary model ──────────────────────────────────────────────────────
data class WeeklySummary(
    val totalSpent: Float,
    val avgPerDay: Float,
    val highestDay: String,
    val highestDayAmount: Float,
    val topCategory: String,
    val topCategoryAmount: Float,
    val transactionCount: Int,
    val vsLastWeek: Float,         // % change vs last week
    val projectedMonthly: Float
)

// ── Category analysis ────────────────────────────────────────────────────────
data class CategoryAnalysis(
    val category: String,
    val amount: Float,
    val percentage: Float,
    val vsLastMonth: Float,        // % change
    val transactionCount: Int,
    val avgPerTransaction: Float
)