package com.venkat.healthapp.sync

import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.venkat.healthapp.common.AppDatabase
import com.venkat.healthapp.hair.data.*
import com.venkat.healthapp.food.data.*
import com.venkat.healthapp.water.data.*
import com.venkat.healthapp.expense.data.*
import com.venkat.healthapp.sleep.data.SleepLog
import com.venkat.healthapp.workout.data.WorkoutLog
import com.venkat.healthapp.vault.data.VaultItem
import com.venkat.healthapp.vault.data.VaultEncryption
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SyncManager(
    private val db: AppDatabase,
    private val userId: String
) {
    private val firestore = Firebase.firestore
    private fun userDoc() = firestore.collection("users").document(userId)

    // ── Sync all UP to Firestore ──────────────────────────────────────────────
    suspend fun syncAll() = withContext(Dispatchers.IO) {
        try {
            syncHairSummaries()
            syncHairTaskLogs()
            syncFoodLogs()
            syncUserProfile()
            syncWaterLogs()
            syncExpenses()
            syncLendBorrow()
            syncSleepLogs()
            syncWorkoutLogs()
            // NOTE: Vault is NOT synced to cloud for security
            // Vault data stays local only
            userDoc().set(
                mapOf("lastSync" to System.currentTimeMillis()),
                SetOptions.merge()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── Restore all FROM Firestore ────────────────────────────────────────────
    suspend fun restoreAll() = withContext(Dispatchers.IO) {
        try {
            restoreHairSummaries()
            restoreHairTaskLogs()
            restoreFoodLogs()
            restoreUserProfile()
            restoreWaterLogs()
            restoreExpenses()
            restoreLendBorrow()
            restoreSleepLogs()
            restoreWorkoutLogs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ── HAIR SUMMARIES ────────────────────────────────────────────────────────
    private suspend fun syncHairSummaries() {
        val summaries = db.dailySummaryDao().allSummaries().first()
        if (summaries.isEmpty()) return
        val batch = firestore.batch()
        summaries.forEach { summary ->
            val ref = userDoc().collection("hair_summaries").document(summary.date)
            batch.set(ref, mapOf(
                "date"           to summary.date,
                "totalTasks"     to summary.totalTasks,
                "completedTasks" to summary.completedTasks,
                "shampooUsed"    to summary.shampooUsed,
                "notes"          to summary.notes
            ))
        }
        batch.commit().await()
    }

    private suspend fun restoreHairSummaries() {
        val docs = userDoc().collection("hair_summaries").get().await()
        docs.documents.forEach { doc ->
            val data = doc.data ?: return@forEach
            db.dailySummaryDao().upsert(
                DailySummary(
                    date           = data["date"] as? String ?: return@forEach,
                    totalTasks     = (data["totalTasks"] as? Long)?.toInt() ?: 13,
                    completedTasks = (data["completedTasks"] as? Long)?.toInt() ?: 0,
                    shampooUsed    = data["shampooUsed"] as? String ?: "",
                    notes          = data["notes"] as? String ?: ""
                )
            )
        }
    }

    // ── HAIR TASK LOGS ────────────────────────────────────────────────────────
    private suspend fun syncHairTaskLogs() {
        val cutoff = LocalDate.now().minusDays(90)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
        val dates = db.dailySummaryDao().datesWithActivity().first()
        val recentDates = dates.filter { it >= cutoff }

        recentDates.forEach { date ->
            val tasks = db.taskLogDao().getTasksForDate(date).first()
            if (tasks.isNotEmpty()) {
                val taskData = tasks.map { task ->
                    mapOf(
                        "taskId"      to task.taskId,
                        "taskName"    to task.taskName,
                        "section"     to task.section,
                        "completed"   to task.completed,
                        "completedAt" to task.completedAt
                    )
                }
                userDoc().collection("hair_tasks")
                    .document(date)
                    .set(mapOf("tasks" to taskData, "date" to date))
                    .await()
            }
        }
    }

    private suspend fun restoreHairTaskLogs() {
        val docs = userDoc().collection("hair_tasks").get().await()
        docs.documents.forEach { doc ->
            val data  = doc.data ?: return@forEach
            val date  = data["date"] as? String ?: return@forEach
            @Suppress("UNCHECKED_CAST")
            val tasks = data["tasks"] as? List<Map<String, Any>> ?: return@forEach
            tasks.forEach { task ->
                db.taskLogDao().upsert(
                    TaskLog(
                        date        = date,
                        taskId      = task["taskId"] as? String ?: return@forEach,
                        taskName    = task["taskName"] as? String ?: "",
                        section     = task["section"] as? String ?: "",
                        completed   = task["completed"] as? Boolean ?: false,
                        completedAt = (task["completedAt"] as? Long) ?: 0L
                    )
                )
            }
        }
    }

    // ── FOOD LOGS ─────────────────────────────────────────────────────────────
    private suspend fun syncFoodLogs() {
        val cutoff = LocalDate.now().minusDays(30)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
        val dates = db.foodLogDao().allDates().first()
        val recentDates = dates.filter { it >= cutoff }

        recentDates.forEach { date ->
            val logs = db.foodLogDao().logsForDate(date).first()
            if (logs.isNotEmpty()) {
                val logData = logs.map { log ->
                    mapOf(
                        "id"         to log.id,
                        "date"       to log.date,
                        "foodItemId" to log.foodItemId,
                        "foodName"   to log.foodName,
                        "quantity"   to log.quantity,
                        "unit"       to log.unit,
                        "calories"   to log.calories,
                        "protein"    to log.protein,
                        "carbs"      to log.carbs,
                        "fat"        to log.fat,
                        "fiber"      to log.fiber,
                        "mealType"   to log.mealType,
                        "loggedAt"   to log.loggedAt
                    )
                }
                userDoc().collection("food_logs")
                    .document(date)
                    .set(mapOf("logs" to logData, "date" to date))
                    .await()
            }
        }
    }

    private suspend fun restoreFoodLogs() {
        val docs = userDoc().collection("food_logs").get().await()
        docs.documents.forEach { doc ->
            val data = doc.data ?: return@forEach
            @Suppress("UNCHECKED_CAST")
            val logs = data["logs"] as? List<Map<String, Any>> ?: return@forEach
            logs.forEach { log ->
                db.foodLogDao().insert(
                    FoodLog(
                        date       = log["date"] as? String ?: return@forEach,
                        foodItemId = (log["foodItemId"] as? Long)?.toInt() ?: 0,
                        foodName   = log["foodName"] as? String ?: "",
                        quantity   = (log["quantity"] as? Double)?.toFloat() ?: 1f,
                        unit       = log["unit"] as? String ?: "",
                        calories   = (log["calories"] as? Double)?.toFloat() ?: 0f,
                        protein    = (log["protein"] as? Double)?.toFloat() ?: 0f,
                        carbs      = (log["carbs"] as? Double)?.toFloat() ?: 0f,
                        fat        = (log["fat"] as? Double)?.toFloat() ?: 0f,
                        fiber      = (log["fiber"] as? Double)?.toFloat() ?: 0f,
                        mealType   = log["mealType"] as? String ?: "Breakfast",
                        loggedAt   = (log["loggedAt"] as? Long) ?: System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // ── USER PROFILE ──────────────────────────────────────────────────────────
    private suspend fun syncUserProfile() {
        val profile = db.userProfileDao().get().first() ?: return
        userDoc().collection("profile").document("data").set(mapOf(
            "name"           to profile.name,
            "weightKg"       to profile.weightKg,
            "heightCm"       to profile.heightCm,
            "age"            to profile.age,
            "gender"         to profile.gender,
            "activityLevel"  to profile.activityLevel,
            "goal"           to profile.goal,
            "targetWeightKg" to profile.targetWeightKg
        )).await()
    }

    private suspend fun restoreUserProfile() {
        val doc  = userDoc().collection("profile").document("data").get().await()
        val data = doc.data ?: return
        db.userProfileDao().upsert(
            UserProfile(
                name           = data["name"] as? String ?: "User",
                weightKg       = (data["weightKg"] as? Double)?.toFloat() ?: 0f,
                heightCm       = (data["heightCm"] as? Double)?.toFloat() ?: 0f,
                age            = (data["age"] as? Long)?.toInt() ?: 26,
                gender         = data["gender"] as? String ?: "Male",
                activityLevel  = data["activityLevel"] as? String ?: "Moderate",
                goal           = data["goal"] as? String ?: "Aesthetic",
                targetWeightKg = (data["targetWeightKg"] as? Double)?.toFloat() ?: 0f
            )
        )
    }

    // ── WATER LOGS ────────────────────────────────────────────────────────────
    private suspend fun syncWaterLogs() {
        val cutoff    = LocalDate.now().minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE)
        val allDates  = db.waterLogDao().allDates().first()
        val recentDates = allDates.filter { it >= cutoff }

        recentDates.forEach { date ->
            val logs = db.waterLogDao().logsForDate(date).first()
            if (logs.isNotEmpty()) {
                val logData = logs.map { log ->
                    mapOf("amountMl" to log.amountMl, "loggedAt" to log.loggedAt)
                }
                userDoc().collection("water_logs")
                    .document(date)
                    .set(mapOf("logs" to logData, "date" to date))
                    .await()
            }
        }
    }

    private suspend fun restoreWaterLogs() {
        val docs  = userDoc().collection("water_logs").get().await()
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        docs.documents.forEach { doc ->
            val data = doc.data ?: return@forEach
            val date = data["date"] as? String ?: return@forEach
            if (date == today) return@forEach  // Don't restore today — let user log fresh
            @Suppress("UNCHECKED_CAST")
            val logs = data["logs"] as? List<Map<String, Any>> ?: return@forEach
            logs.forEach { log ->
                db.waterLogDao().insert(
                    WaterLog(
                        date     = date,
                        amountMl = (log["amountMl"] as? Long)?.toInt() ?: 0,
                        loggedAt = (log["loggedAt"] as? Long) ?: System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // ── EXPENSES ──────────────────────────────────────────────────────────────
    private suspend fun syncExpenses() {
        val expenses = db.expenseDao().allExpenses().first()
        if (expenses.isEmpty()) return
        val batch = firestore.batch()
        expenses.forEach { expense ->
            val ref = userDoc().collection("expenses").document(expense.id.toString())
            batch.set(ref, mapOf(
                "id"          to expense.id,
                "date"        to expense.date,
                "amount"      to expense.amount,
                "category"    to expense.category,
                "paymentMode" to expense.paymentMode,
                "title"       to expense.title,
                "note"        to expense.note,
                "noteAdded"   to expense.noteAdded,
                "createdAt"   to expense.createdAt,
                "updatedAt"   to expense.updatedAt
            ))
        }
        batch.commit().await()
    }

    private suspend fun restoreExpenses() {
        val docs = userDoc().collection("expenses").get().await()
        docs.documents.forEach { doc ->
            val data = doc.data ?: return@forEach
            db.expenseDao().insert(
                Expense(
                    date        = data["date"] as? String ?: return@forEach,
                    amount      = (data["amount"] as? Double)?.toFloat() ?: 0f,
                    category    = data["category"] as? String ?: "OTHER",
                    paymentMode = data["paymentMode"] as? String ?: "CASH",
                    title       = data["title"] as? String ?: "",
                    note        = data["note"] as? String ?: "",
                    noteAdded   = data["noteAdded"] as? Boolean ?: false,
                    createdAt   = (data["createdAt"] as? Long) ?: System.currentTimeMillis(),
                    updatedAt   = (data["updatedAt"] as? Long) ?: System.currentTimeMillis()
                )
            )
        }
    }

    // ── LEND & BORROW ─────────────────────────────────────────────────────────
    private suspend fun syncLendBorrow() {
        val entries = db.lendBorrowDao().all().first()
        if (entries.isEmpty()) return
        val batch = firestore.batch()
        entries.forEach { entry ->
            val ref = userDoc().collection("lend_borrow").document(entry.id.toString())
            batch.set(ref, mapOf(
                "id"          to entry.id,
                "personName"  to entry.personName,
                "personPhone" to entry.personPhone,
                "amount"      to entry.amount,
                "paidBack"    to entry.paidBack,
                "type"        to entry.type,
                "status"      to entry.status,
                "reason"      to entry.reason,
                "date"        to entry.date,
                "dueDate"     to entry.dueDate,
                "createdAt"   to entry.createdAt,
                "updatedAt"   to entry.updatedAt
            ))
        }
        batch.commit().await()
    }

    private suspend fun restoreLendBorrow() {
        val docs = userDoc().collection("lend_borrow").get().await()
        docs.documents.forEach { doc ->
            val data = doc.data ?: return@forEach
            db.lendBorrowDao().insert(
                LendBorrow(
                    personName  = data["personName"] as? String ?: return@forEach,
                    personPhone = data["personPhone"] as? String ?: "",
                    amount      = (data["amount"] as? Double)?.toFloat() ?: 0f,
                    paidBack    = (data["paidBack"] as? Double)?.toFloat() ?: 0f,
                    type        = data["type"] as? String ?: "LENT",
                    status      = data["status"] as? String ?: "PENDING",
                    reason      = data["reason"] as? String ?: "",
                    date        = data["date"] as? String ?: "",
                    dueDate     = data["dueDate"] as? String ?: "",
                    createdAt   = (data["createdAt"] as? Long) ?: System.currentTimeMillis(),
                    updatedAt   = (data["updatedAt"] as? Long) ?: System.currentTimeMillis()
                )
            )
        }
    }

    // ── SLEEP LOGS ────────────────────────────────────────────────────────────
    private suspend fun syncSleepLogs() {
        val logs = db.sleepLogDao().allLogs().first()
        if (logs.isEmpty()) return
        val batch = firestore.batch()
        logs.forEach { log ->
            val ref = userDoc().collection("sleep_logs").document(log.date)
            batch.set(ref, mapOf(
                "date"            to log.date,
                "bedTimeMillis"   to log.bedTimeMillis,
                "wakeTimeMillis"  to log.wakeTimeMillis,
                "durationMinutes" to log.durationMinutes,
                "quality"         to log.quality,
                "note"            to log.note,
                "loggedAt"        to log.loggedAt
            ))
        }
        batch.commit().await()
    }

    private suspend fun restoreSleepLogs() {
        val docs = userDoc().collection("sleep_logs").get().await()
        docs.documents.forEach { doc ->
            val data = doc.data ?: return@forEach
            db.sleepLogDao().insert(
                SleepLog(
                    date            = data["date"] as? String ?: return@forEach,
                    bedTimeMillis   = (data["bedTimeMillis"] as? Long) ?: 0L,
                    wakeTimeMillis  = (data["wakeTimeMillis"] as? Long) ?: 0L,
                    durationMinutes = (data["durationMinutes"] as? Long)?.toInt() ?: 0,
                    quality         = (data["quality"] as? Long)?.toInt() ?: 3,
                    note            = data["note"] as? String ?: "",
                    loggedAt        = (data["loggedAt"] as? Long) ?: System.currentTimeMillis()
                )
            )
        }
    }

    // ── WORKOUT LOGS ──────────────────────────────────────────────────────────
    private suspend fun syncWorkoutLogs() {
        val cutoff = LocalDate.now().minusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE)
        val dates  = db.workoutLogDao().allDates().first()
        val recent = dates.filter { it >= cutoff }

        recent.forEach { date ->
            val logs = db.workoutLogDao().logsForDate(date).first()
            if (logs.isNotEmpty()) {
                val logData = logs.map { log ->
                    mapOf(
                        "splitName"    to log.splitName,
                        "exerciseName" to log.exerciseName,
                        "sets"         to log.sets,
                        "reps"         to log.reps,
                        "weightKg"     to log.weightKg,
                        "completed"    to log.completed,
                        "loggedAt"     to log.loggedAt
                    )
                }
                userDoc().collection("workout_logs")
                    .document(date)
                    .set(mapOf("logs" to logData, "date" to date))
                    .await()
            }
        }
    }

    private suspend fun restoreWorkoutLogs() {
        val docs = userDoc().collection("workout_logs").get().await()
        docs.documents.forEach { doc ->
            val data = doc.data ?: return@forEach
            val date = data["date"] as? String ?: return@forEach
            @Suppress("UNCHECKED_CAST")
            val logs = data["logs"] as? List<Map<String, Any>> ?: return@forEach
            logs.forEach { log ->
                db.workoutLogDao().insert(
                    WorkoutLog(
                        date         = date,
                        splitName    = log["splitName"] as? String ?: "",
                        exerciseName = log["exerciseName"] as? String ?: "",
                        sets         = (log["sets"] as? Long)?.toInt() ?: 0,
                        reps         = (log["reps"] as? Long)?.toInt() ?: 0,
                        weightKg     = (log["weightKg"] as? Double)?.toFloat() ?: 0f,
                        completed    = log["completed"] as? Boolean ?: false,
                        loggedAt     = (log["loggedAt"] as? Long) ?: System.currentTimeMillis()
                    )
                )
            }
        }
    }
}