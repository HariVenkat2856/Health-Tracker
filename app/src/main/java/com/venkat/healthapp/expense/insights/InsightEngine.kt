package com.venkat.healthapp.expense.insights

import com.venkat.healthapp.expense.data.Expense
import com.venkat.healthapp.expense.data.ExpenseCategory
import com.venkat.healthapp.expense.data.getCategoryEnum
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object InsightEngine {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // ── Generate all insights from expense data ───────────────────────────────
    fun generateInsights(
        allExpenses: List<Expense>,
        currentMonth: String  // "2026-05"
    ): List<SpendingInsight> {
        val insights = mutableListOf<SpendingInsight>()

        val thisMonth   = allExpenses.filter { it.date.startsWith(currentMonth) }
        val lastMonth   = allExpenses.filter {
            it.date.startsWith(getPreviousMonth(currentMonth))
        }
        val thisWeek    = getThisWeekExpenses(allExpenses)
        val lastWeek    = getLastWeekExpenses(allExpenses)

        if (allExpenses.isEmpty()) return emptyList()

        // Run all analysis
        insights.addAll(analyzeWeeklySpending(thisWeek, lastWeek))
        insights.addAll(analyzeCategorySpending(thisMonth, lastMonth))
        insights.addAll(detectHighestSpendingDay(thisMonth))
        insights.addAll(detectFrequentSmallSpends(thisMonth))
        insights.addAll(analyzeFoodSpending(thisMonth))
        insights.addAll(detectWeekendSpending(thisMonth))
        insights.addAll(generateSavingTips(thisMonth))
        insights.addAll(analyzePaymentMode(thisMonth))
        insights.addAll(predictMonthlySpend(thisMonth, currentMonth))
        insights.addAll(detectPositiveBehavior(thisMonth, lastMonth))

        // Sort by priority
        return insights.sortedWith(
            compareBy {
                when (it.priority) {
                    InsightPriority.HIGH   -> 0
                    InsightPriority.MEDIUM -> 1
                    InsightPriority.LOW    -> 2
                }
            }
        )
    }

    // ── Weekly comparison ─────────────────────────────────────────────────────
    private fun analyzeWeeklySpending(
        thisWeek: List<Expense>,
        lastWeek: List<Expense>
    ): List<SpendingInsight> {
        val insights   = mutableListOf<SpendingInsight>()
        val thisTotal  = thisWeek.sumOf { it.amount.toDouble() }.toFloat()
        val lastTotal  = lastWeek.sumOf { it.amount.toDouble() }.toFloat()

        if (lastTotal == 0f || thisTotal == 0f) return emptyList()

        val pctChange = ((thisTotal - lastTotal) / lastTotal * 100).toInt()

        when {
            pctChange > 40 -> insights.add(SpendingInsight(
                id          = "weekly_spike",
                type        = InsightType.WARNING,
                priority    = InsightPriority.HIGH,
                emoji       = "🚨",
                title       = "Spending spike this week!",
                description = "You spent ₹%.0f this week — that's %d%% more than last week (₹%.0f)".format(thisTotal, pctChange, lastTotal),
                actionTip   = "Review your expenses and cut back on non-essentials",
                savingAmount = thisTotal - lastTotal
            ))
            pctChange in 20..40 -> insights.add(SpendingInsight(
                id          = "weekly_high",
                type        = InsightType.WARNING,
                priority    = InsightPriority.MEDIUM,
                emoji       = "⚠️",
                title       = "Higher spending this week",
                description = "You spent %d%% more than last week. ₹%.0f vs ₹%.0f".format(pctChange, thisTotal, lastTotal),
                actionTip   = "Be mindful of daily spending",
                savingAmount = thisTotal - lastTotal
            ))
            pctChange < -20 -> insights.add(SpendingInsight(
                id          = "weekly_low",
                type        = InsightType.POSITIVE,
                priority    = InsightPriority.LOW,
                emoji       = "🎉",
                title       = "Great savings this week!",
                description = "You spent %d%% less than last week. You saved ₹%.0f!".format(-pctChange, lastTotal - thisTotal),
                actionTip   = "Keep up the good work!",
                savingAmount = lastTotal - thisTotal
            ))
        }
        return insights
    }

    // ── Category analysis ─────────────────────────────────────────────────────
    private fun analyzeCategorySpending(
        thisMonth: List<Expense>,
        lastMonth: List<Expense>
    ): List<SpendingInsight> {
        val insights = mutableListOf<SpendingInsight>()
        val thisTotal = thisMonth.sumOf { it.amount.toDouble() }.toFloat()

        // Group by category
        val thisByCat = thisMonth.groupBy { it.category }
        val lastByCat = lastMonth.groupBy { it.category }

        thisByCat.forEach { (cat, expenses) ->
            val catTotal   = expenses.sumOf { it.amount.toDouble() }.toFloat()
            val lastCatTotal = lastByCat[cat]?.sumOf { it.amount.toDouble() }?.toFloat() ?: 0f
            val pct        = if (thisTotal > 0) (catTotal / thisTotal * 100).toInt() else 0
            val change     = if (lastCatTotal > 0)
                ((catTotal - lastCatTotal) / lastCatTotal * 100).toInt() else 0

            val catEnum = getCategoryEnum(cat)

            // High category spending
            if (pct >= 40) {
                insights.add(SpendingInsight(
                    id              = "cat_high_$cat",
                    type            = InsightType.WARNING,
                    priority        = InsightPriority.HIGH,
                    emoji           = catEnum.emoji,
                    title           = "${catEnum.label} is $pct% of spending",
                    description     = "You've spent ₹%.0f on ${catEnum.label} this month — $pct%% of total budget".format(catTotal),
                    actionTip       = "Consider setting a budget limit for ${catEnum.label}",
                    savingAmount    = catTotal * 0.2f,
                    relatedCategory = cat
                ))
            }

            // Category increase vs last month
            if (change > 50 && lastCatTotal > 0) {
                insights.add(SpendingInsight(
                    id              = "cat_increase_$cat",
                    type            = InsightType.COMPARISON,
                    priority        = InsightPriority.MEDIUM,
                    emoji           = "📈",
                    title           = "${catEnum.label} up $change% vs last month",
                    description     = "₹%.0f this month vs ₹%.0f last month on ${catEnum.label}".format(catTotal, lastCatTotal),
                    actionTip       = "What changed? Try to identify avoidable expenses",
                    relatedCategory = cat
                ))
            }
        }

        return insights
    }

    // ── Highest spending day ──────────────────────────────────────────────────
    private fun detectHighestSpendingDay(thisMonth: List<Expense>): List<SpendingInsight> {
        if (thisMonth.isEmpty()) return emptyList()

        val byDay = thisMonth.groupBy { expense ->
            try {
                LocalDate.parse(expense.date).dayOfWeek.name
            } catch (e: Exception) { "UNKNOWN" }
        }

        val highestDay = byDay.maxByOrNull { (_, expenses) ->
            expenses.sumOf { it.amount.toDouble() }
        } ?: return emptyList()

        val dayName  = highestDay.key.lowercase().replaceFirstChar { it.uppercase() }
        val dayTotal = highestDay.value.sumOf { it.amount.toDouble() }.toFloat()
        val monthTotal = thisMonth.sumOf { it.amount.toDouble() }.toFloat()
        val dayPct   = if (monthTotal > 0) (dayTotal / monthTotal * 100).toInt() else 0

        if (dayPct < 25) return emptyList()

        return listOf(SpendingInsight(
            id          = "highest_day",
            type        = InsightType.PATTERN,
            priority    = InsightPriority.MEDIUM,
            emoji       = "📅",
            title       = "$dayName is your biggest spending day",
            description = "You spend the most on ${dayName}s — ₹%.0f ($dayPct%% of monthly spending)".format(dayTotal),
            actionTip   = "Plan your $dayName expenses in advance to avoid impulse spending"
        ))
    }

    // ── Frequent small spends ─────────────────────────────────────────────────
    private fun detectFrequentSmallSpends(thisMonth: List<Expense>): List<SpendingInsight> {
        val smallSpends = thisMonth.filter { it.amount < 100 }
        if (smallSpends.size < 10) return emptyList()

        val totalSmall = smallSpends.sumOf { it.amount.toDouble() }.toFloat()

        return listOf(SpendingInsight(
            id          = "small_spends",
            type        = InsightType.TIP,
            priority    = InsightPriority.MEDIUM,
            emoji       = "☕",
            title       = "${smallSpends.size} small purchases adding up!",
            description = "You made ${smallSpends.size} transactions under ₹100 — totaling ₹%.0f this month".format(totalSmall),
            actionTip   = "Small daily purchases (chai, snacks, auto) add up fast. Track them carefully.",
            savingAmount = totalSmall * 0.3f
        ))
    }

    // ── Food spending analysis ─────────────────────────────────────────────────
    private fun analyzeFoodSpending(thisMonth: List<Expense>): List<SpendingInsight> {
        val foodExpenses = thisMonth.filter {
            it.category == "FOOD" || it.category == "GROCERIES"
        }
        if (foodExpenses.isEmpty()) return emptyList()

        val foodTotal  = foodExpenses.sumOf { it.amount.toDouble() }.toFloat()
        val monthTotal = thisMonth.sumOf { it.amount.toDouble() }.toFloat()
        val foodPct    = if (monthTotal > 0) (foodTotal / monthTotal * 100).toInt() else 0

        val insights = mutableListOf<SpendingInsight>()

        if (foodPct > 35) {
            insights.add(SpendingInsight(
                id          = "food_high",
                type        = InsightType.TIP,
                priority    = InsightPriority.HIGH,
                emoji       = "🍽",
                title       = "Food is $foodPct% of your budget",
                description = "You spent ₹%.0f on food & groceries this month".format(foodTotal),
                actionTip   = "Cook at home more often. Meal prep on Sunday can save ₹%.0f/month".format(foodTotal * 0.25f),
                savingAmount = foodTotal * 0.25f,
                relatedCategory = "FOOD"
            ))
        }

        // Dining out frequency
        val diningCount = foodExpenses.count { expense ->
            expense.title.lowercase().let { title ->
                title.contains("restaurant") || title.contains("hotel") ||
                        title.contains("zomato") || title.contains("swiggy") ||
                        title.contains("cafe") || title.contains("biryani") ||
                        title.contains("dosa") || title.contains("lunch") ||
                        title.contains("dinner")
            }
        }

        if (diningCount >= 8) {
            val diningEstimate = foodTotal * 0.6f
            insights.add(SpendingInsight(
                id          = "dining_frequent",
                type        = InsightType.TIP,
                priority    = InsightPriority.MEDIUM,
                emoji       = "🍕",
                title       = "Dining out $diningCount times this month",
                description = "Frequent restaurant visits estimated at ₹%.0f this month".format(diningEstimate),
                actionTip   = "Cooking at home even 3 extra days/week can save ₹%.0f monthly".format(diningEstimate * 0.4f),
                savingAmount = diningEstimate * 0.4f,
                relatedCategory = "FOOD"
            ))
        }

        return insights
    }

    // ── Weekend vs weekday spending ───────────────────────────────────────────
    private fun detectWeekendSpending(thisMonth: List<Expense>): List<SpendingInsight> {
        val weekendExpenses = thisMonth.filter { expense ->
            try {
                val day = LocalDate.parse(expense.date).dayOfWeek.value
                day == 6 || day == 7  // Saturday or Sunday
            } catch (e: Exception) { false }
        }

        val weekdayExpenses = thisMonth - weekendExpenses.toSet()

        val weekendTotal  = weekendExpenses.sumOf { it.amount.toDouble() }.toFloat()
        val weekdayTotal  = weekdayExpenses.sumOf { it.amount.toDouble() }.toFloat()
        val weekendDays   = 8  // approx weekends in a month
        val weekdayDays   = 22

        val weekendAvg = if (weekendDays > 0) weekendTotal / weekendDays else 0f
        val weekdayAvg = if (weekdayDays > 0) weekdayTotal / weekdayDays else 0f

        if (weekendAvg > weekdayAvg * 2 && weekendTotal > 500) {
            return listOf(SpendingInsight(
                id          = "weekend_spending",
                type        = InsightType.PATTERN,
                priority    = InsightPriority.MEDIUM,
                emoji       = "🎉",
                title       = "Weekend spending is 2x higher",
                description = "You spend ₹%.0f/day on weekends vs ₹%.0f/day on weekdays".format(weekendAvg, weekdayAvg),
                actionTip   = "Plan free or low-cost weekend activities to reduce impulse spending",
                savingAmount = (weekendAvg - weekdayAvg) * weekendDays
            ))
        }
        return emptyList()
    }

    // ── Saving tips ───────────────────────────────────────────────────────────
    private fun generateSavingTips(thisMonth: List<Expense>): List<SpendingInsight> {
        val insights   = mutableListOf<SpendingInsight>()
        val monthTotal = thisMonth.sumOf { it.amount.toDouble() }.toFloat()

        // Transport tips
        val transportTotal = thisMonth
            .filter { it.category == "TRANSPORT" }
            .sumOf { it.amount.toDouble() }.toFloat()

        if (transportTotal > 1500) {
            insights.add(SpendingInsight(
                id          = "transport_tip",
                type        = InsightType.TIP,
                priority    = InsightPriority.LOW,
                emoji       = "🚗",
                title       = "Save on transport: ₹%.0f/month".format(transportTotal * 0.3f),
                description = "You spent ₹%.0f on transport this month".format(transportTotal),
                actionTip   = "Use monthly bus pass or carpool to save up to 30%",
                savingAmount = transportTotal * 0.3f,
                relatedCategory = "TRANSPORT"
            ))
        }

        // Entertainment tips
        val entTotal = thisMonth
            .filter { it.category == "ENTERTAINMENT" }
            .sumOf { it.amount.toDouble() }.toFloat()

        if (entTotal > 1000) {
            insights.add(SpendingInsight(
                id          = "entertainment_tip",
                type        = InsightType.TIP,
                priority    = InsightPriority.LOW,
                emoji       = "🎬",
                title       = "Entertainment at ₹%.0f — can reduce".format(entTotal),
                description = "Look for free events, OTT sharing, or early bird show discounts",
                actionTip   = "Share OTT subscriptions with family to save ₹300-500/month",
                savingAmount = minOf(entTotal * 0.4f, 500f),
                relatedCategory = "ENTERTAINMENT"
            ))
        }

        // No notes on expenses
        val noNoteCount = thisMonth.count { it.note.isBlank() }
        if (noNoteCount > thisMonth.size / 2 && thisMonth.size > 5) {
            insights.add(SpendingInsight(
                id          = "add_notes",
                type        = InsightType.TIP,
                priority    = InsightPriority.LOW,
                emoji       = "📝",
                title       = "$noNoteCount expenses without notes",
                description = "Adding notes helps you understand where money actually goes",
                actionTip   = "Spend 2 minutes daily adding notes to your expenses"
            ))
        }

        return insights
    }

    // ── Payment mode analysis ─────────────────────────────────────────────────
    private fun analyzePaymentMode(thisMonth: List<Expense>): List<SpendingInsight> {
        val cashExpenses = thisMonth.filter { it.paymentMode == "CASH" }
        val cashTotal    = cashExpenses.sumOf { it.amount.toDouble() }.toFloat()
        val monthTotal   = thisMonth.sumOf { it.amount.toDouble() }.toFloat()
        val cashPct      = if (monthTotal > 0) (cashTotal / monthTotal * 100).toInt() else 0

        if (cashPct > 50) {
            return listOf(SpendingInsight(
                id          = "cash_heavy",
                type        = InsightType.TIP,
                priority    = InsightPriority.LOW,
                emoji       = "💵",
                title       = "$cashPct% spending in cash — hard to track",
                description = "Cash spending of ₹%.0f is difficult to monitor and control".format(cashTotal),
                actionTip   = "Switch to UPI for easier tracking and cashback rewards"
            ))
        }
        return emptyList()
    }

    // ── Monthly projection ────────────────────────────────────────────────────
    private fun predictMonthlySpend(
        thisMonth: List<Expense>,
        currentMonth: String
    ): List<SpendingInsight> {
        if (thisMonth.isEmpty()) return emptyList()

        val today      = LocalDate.now()
        val dayOfMonth = today.dayOfMonth
        val daysInMonth = today.lengthOfMonth()
        val monthTotal = thisMonth.sumOf { it.amount.toDouble() }.toFloat()
        val dailyAvg   = monthTotal / dayOfMonth
        val projected  = dailyAvg * daysInMonth

        if (dayOfMonth < 5) return emptyList()  // Too early to project

        return listOf(SpendingInsight(
            id          = "monthly_projection",
            type        = InsightType.PREDICTION,
            priority    = InsightPriority.MEDIUM,
            emoji       = "🔮",
            title       = "Projected spend: ₹%.0f this month".format(projected),
            description = "Based on ₹%.0f/day average, you'll spend ₹%.0f by month end".format(dailyAvg, projected),
            actionTip   = if (projected > 15000)
                "On track to overspend. Cut back now to stay under ₹%.0f".format(projected * 0.8f)
            else
                "You're on a good track this month! 👍"
        ))
    }

    // ── Positive behavior detection ───────────────────────────────────────────
    private fun detectPositiveBehavior(
        thisMonth: List<Expense>,
        lastMonth: List<Expense>
    ): List<SpendingInsight> {
        val insights   = mutableListOf<SpendingInsight>()
        val thisTotal  = thisMonth.sumOf { it.amount.toDouble() }.toFloat()
        val lastTotal  = lastMonth.sumOf { it.amount.toDouble() }.toFloat()

        if (lastTotal > 0 && thisTotal < lastTotal * 0.85f) {
            val saved = lastTotal - thisTotal
            insights.add(SpendingInsight(
                id          = "positive_reduction",
                type        = InsightType.POSITIVE,
                priority    = InsightPriority.LOW,
                emoji       = "🏆",
                title       = "Spending down! Saved ₹%.0f vs last month".format(saved),
                description = "Great job! You reduced spending by %.0f%% compared to last month".format((saved / lastTotal) * 100),
                actionTip   = "Keep this momentum going!",
                savingAmount = saved
            ))
        }

        // Good tracking behavior
        val withNotes = thisMonth.count { it.note.isNotBlank() }
        val noteRate  = if (thisMonth.isNotEmpty()) withNotes * 100 / thisMonth.size else 0

        if (noteRate > 70 && thisMonth.size > 5) {
            insights.add(SpendingInsight(
                id          = "good_tracking",
                type        = InsightType.POSITIVE,
                priority    = InsightPriority.LOW,
                emoji       = "⭐",
                title       = "Excellent tracking! $noteRate% expenses have notes",
                description = "You're tracking expenses very well this month",
                actionTip   = "This habit will help you understand your spending better!"
            ))
        }

        return insights
    }

    // ── Generate weekly summary ───────────────────────────────────────────────
    fun generateWeeklySummary(
        thisWeek: List<Expense>,
        lastWeek: List<Expense>
    ): WeeklySummary {
        val total     = thisWeek.sumOf { it.amount.toDouble() }.toFloat()
        val lastTotal = lastWeek.sumOf { it.amount.toDouble() }.toFloat()
        val avgPerDay = total / 7f

        // Highest spending day
        val byDay = thisWeek.groupBy { it.date }
        val highestDay = byDay.maxByOrNull { (_, e) -> e.sumOf { it.amount.toDouble() } }
        val highestDayName = highestDay?.key?.let {
            runCatching {
                LocalDate.parse(it).dayOfWeek.name
                    .lowercase().replaceFirstChar { c -> c.uppercase() }
            }.getOrDefault(it)
        } ?: "—"
        val highestDayAmt = highestDay?.value?.sumOf { it.amount.toDouble() }?.toFloat() ?: 0f

        // Top category
        val byCat = thisWeek.groupBy { it.category }
        val topCat = byCat.maxByOrNull { (_, e) -> e.sumOf { it.amount.toDouble() } }
        val topCatName = topCat?.key?.let { getCategoryEnum(it).label } ?: "—"
        val topCatAmt  = topCat?.value?.sumOf { it.amount.toDouble() }?.toFloat() ?: 0f

        val vsLastWeek  = if (lastTotal > 0) ((total - lastTotal) / lastTotal * 100) else 0f
        val projMonthly = avgPerDay * 30f

        return WeeklySummary(
            totalSpent         = total,
            avgPerDay          = avgPerDay,
            highestDay         = highestDayName,
            highestDayAmount   = highestDayAmt,
            topCategory        = topCatName,
            topCategoryAmount  = topCatAmt,
            transactionCount   = thisWeek.size,
            vsLastWeek         = vsLastWeek,
            projectedMonthly   = projMonthly
        )
    }

    // ── Helper: get this week's expenses ─────────────────────────────────────
    fun getThisWeekExpenses(expenses: List<Expense>): List<Expense> {
        val today    = LocalDate.now()
        val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        return expenses.filter { expense ->
            runCatching { LocalDate.parse(expense.date) >= weekStart }.getOrDefault(false)
        }
    }

    fun getLastWeekExpenses(expenses: List<Expense>): List<Expense> {
        val today     = LocalDate.now()
        val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        val lastStart = weekStart.minusDays(7)
        return expenses.filter { expense ->
            runCatching {
                val d = LocalDate.parse(expense.date)
                d >= lastStart && d < weekStart
            }.getOrDefault(false)
        }
    }

    private fun getPreviousMonth(month: String): String {
        return try {
            val d = LocalDate.parse("$month-01")
            d.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
        } catch (e: Exception) { "" }
    }
}