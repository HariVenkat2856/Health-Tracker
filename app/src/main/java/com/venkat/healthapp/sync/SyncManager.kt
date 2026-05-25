package com.venkat.healthapp.sync

import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.venkat.healthapp.common.AppDatabase
import com.venkat.healthapp.hair.data.*
import com.venkat.healthapp.food.data.*
import com.venkat.healthapp.water.data.*
import com.venkat.healthapp.expense.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SyncManager(
    private val db: AppDatabase,
    private val userId: String
) {
    private val firestore = Firebase.firestore

    // User's root collection in Firestore
    private fun userDoc() = firestore.collection("users").document(userId)

    // ── Sync all data to Firestore ────────────────────────────────────────────
    suspend fun syncAll() = withContext(Dispatchers.IO) {
        try {
            syncHairLogs()
            syncFoodLogs()
            syncWaterLogs()
            syncExpenses()
            syncLendBorrow()
        } catch (e: Exception) {
            // Handle silently — local data always works
        }
    }

    // ── Restore all data from Firestore ───────────────────────────────────────
    suspend fun restoreAll() = withContext(Dispatchers.IO) {
        try {
            restoreHairLogs()
            restoreFoodLogs()
            restoreWaterLogs()
            restoreExpenses()
            restoreLendBorrow()
        } catch (e: Exception) {
            // Silently handle
        }
    }

    // ── Hair tracker sync ─────────────────────────────────────────────────────
    private suspend fun syncHairLogs() {
        val summaries = db.dailySummaryDao().allSummaries()
        // Use first() to get current value from Flow
        // In production use a coroutine scope properly
        val batch = firestore.batch()
        // We store summaries — compact data
        val docRef = userDoc().collection("hair_summaries")
        batch.commit().await()
    }

    private suspend fun restoreHairLogs() {
        val docs = userDoc().collection("hair_summaries").get().await()
        docs.documents.forEach { doc ->
            val data = doc.data ?: return@forEach
            val summary = DailySummary(
                date           = data["date"] as? String ?: return@forEach,
                totalTasks     = (data["totalTasks"] as? Long)?.toInt() ?: 13,
                completedTasks = (data["completedTasks"] as? Long)?.toInt() ?: 0,
                shampooUsed    = data["shampooUsed"] as? String ?: ""
            )
            db.dailySummaryDao().upsert(summary)
        }
    }

    // ── Food log sync ─────────────────────────────────────────────────────────
    private suspend fun syncFoodLogs() {
        val profile = db.userProfileDao().get()
        // Sync user profile to Firestore
        userDoc().set(mapOf(
            "lastSync" to System.currentTimeMillis()
        ), SetOptions.merge()).await()
    }

    private suspend fun restoreFoodLogs() {
        // Restore from Firestore
    }

    // ── Water log sync ────────────────────────────────────────────────────────
    private suspend fun syncWaterLogs() {
        // Only sync recent 30 days
        val cutoff = LocalDate.now().minusDays(30)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
        // Sync water logs after cutoff
    }

    private suspend fun restoreWaterLogs() {}

    // ── Expense sync ──────────────────────────────────────────────────────────
    private suspend fun syncExpenses() {
        // Sync expenses to Firestore
    }

    private suspend fun restoreExpenses() {}

    // ── Lend/Borrow sync ──────────────────────────────────────────────────────
    private suspend fun syncLendBorrow() {
        // Most important to sync — financial data
    }

    private suspend fun restoreLendBorrow() {}
}