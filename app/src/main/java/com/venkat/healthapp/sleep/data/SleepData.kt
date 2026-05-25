package com.venkat.healthapp.sleep.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ── Entity ────────────────────────────────────────────────────────────────────
@Entity(tableName = "sleep_logs")
data class SleepLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,               // "2026-05-15" (the night's date)
    val bedTimeMillis: Long,        // when you went to bed
    val wakeTimeMillis: Long,       // when you woke up
    val durationMinutes: Int,       // total sleep minutes
    val quality: Int,               // 1-5 (1=terrible, 5=excellent)
    val note: String = "",          // optional note
    val deepSleepMinutes: Int = 0,  // optional
    val loggedAt: Long = System.currentTimeMillis()
)

// ── DAO ───────────────────────────────────────────────────────────────────────
@Dao
interface SleepLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SleepLog): Long

    @Delete
    suspend fun delete(log: SleepLog)

    @Query("SELECT * FROM sleep_logs ORDER BY date DESC")
    fun allLogs(): Flow<List<SleepLog>>

    @Query("SELECT * FROM sleep_logs WHERE date = :date LIMIT 1")
    fun logForDate(date: String): Flow<SleepLog?>

    @Query("SELECT * FROM sleep_logs ORDER BY date DESC LIMIT 7")
    fun lastSevenDays(): Flow<List<SleepLog>>

    @Query("SELECT AVG(durationMinutes) FROM sleep_logs")
    fun avgDurationMinutes(): Flow<Float?>

    @Query("SELECT AVG(quality) FROM sleep_logs")
    fun avgQuality(): Flow<Float?>

    @Query("SELECT COUNT(*) FROM sleep_logs WHERE durationMinutes >= 420")
    fun goodSleepDaysCount(): Flow<Int>   // 420 min = 7 hours
}

// ── Sleep quality helpers ──────────────────────────────────────────────────────
fun qualityLabel(q: Int) = when(q) {
    1 -> "Terrible 😫"
    2 -> "Poor 😴"
    3 -> "Okay 😐"
    4 -> "Good 😊"
    5 -> "Excellent 🌟"
    else -> "—"
}

fun qualityColor(q: Int) = when(q) {
    1 -> android.graphics.Color.parseColor("#FF4D6D")
    2 -> android.graphics.Color.parseColor("#F0B429")
    3 -> android.graphics.Color.parseColor("#00A3FF")
    4 -> android.graphics.Color.parseColor("#00C896")
    5 -> android.graphics.Color.parseColor("#B57AFF")
    else -> android.graphics.Color.parseColor("#8B949E")
}

fun formatDuration(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (m == 0) "${h}h" else "${h}h ${m}m"
}

fun formatTime(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(millis))
}