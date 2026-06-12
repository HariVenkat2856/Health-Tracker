package com.venkat.healthapp.hair.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// ── Entities ──────────────────────────────────────────────────────────────────
@Entity(tableName = "task_logs", indices = [Index(value = ["date","taskId"], unique = true)])
data class TaskLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, val taskId: String, val taskName: String,
    val section: String, val completed: Boolean = false, val completedAt: Long = 0L
)

@Entity(tableName = "daily_summary")
data class DailySummary(
    @PrimaryKey val date: String,
    val totalTasks: Int, val completedTasks: Int,
    val shampooUsed: String = "", val notes: String = ""
)

@Entity(tableName = "scalp_photos")
data class ScalpPhoto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weekLabel: String, val date: String, val capturedAt: Long,
    val photoPath1: String, val photoPath2: String = "",
    val label: String = "", val weekNumber: Int = 1
)

// ── DAOs ──────────────────────────────────────────────────────────────────────
@Dao interface TaskLogDao {
    @Query("SELECT * FROM task_logs WHERE date=:date ORDER BY section,taskId")
    fun getTasksForDate(date: String): Flow<List<TaskLog>>
    @Upsert suspend fun upsert(log: TaskLog)
    @Query("UPDATE task_logs SET completed=:done,completedAt=:at WHERE date=:date AND taskId=:taskId")
    suspend fun updateTask(date: String, taskId: String, done: Boolean, at: Long)

    // Add to TaskLogDao
    @Query("SELECT * FROM task_logs WHERE date = :date")
    suspend fun getTasksForDateSync(date: String): List<TaskLog>
}

@Dao interface DailySummaryDao {
    @Upsert suspend fun upsert(s: DailySummary)
    @Query("SELECT * FROM daily_summary ORDER BY date DESC") fun allSummaries(): Flow<List<DailySummary>>
    @Query("SELECT * FROM daily_summary WHERE date=:date") fun summaryForDate(date: String): Flow<DailySummary?>
    @Query("SELECT COUNT(*) FROM daily_summary WHERE completedTasks=totalTasks AND totalTasks>0") fun perfectDaysCount(): Flow<Int>
    @Query("SELECT COUNT(*) FROM daily_summary WHERE completedTasks>0") fun activeDaysCount(): Flow<Int>
    @Query("SELECT SUM(completedTasks) FROM daily_summary") fun totalCompletedAllTime(): Flow<Int?>
    @Query("SELECT date FROM daily_summary WHERE completedTasks>0 ORDER BY date DESC") fun datesWithActivity(): Flow<List<String>>
}

@Dao interface ScalpPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(p: ScalpPhoto): Long
    @Delete suspend fun delete(p: ScalpPhoto)
    @Query("SELECT * FROM scalp_photos ORDER BY capturedAt DESC") fun allPhotos(): Flow<List<ScalpPhoto>>
    @Query("SELECT COUNT(*) FROM scalp_photos") fun totalCount(): Flow<Int>
    @Query("SELECT * FROM scalp_photos ORDER BY capturedAt DESC LIMIT 1") fun latestPhoto(): Flow<ScalpPhoto?>
}

// ── Task definitions ──────────────────────────────────────────────────────────
data class TaskDef(val id: String, val name: String, val subtitle: String,
                   val section: Section, val pillLabel: String, val pillType: PillType)
enum class Section(val label: String, val emoji: String) {
    MORNING("Morning — After Breakfast","🌅"), AFTERNOON("Afternoon — After Lunch","🌤"),
    NIGHT("Night — After Dinner","🌙"), WEEKLY("Weekly Tasks","📆")
}
enum class PillType { TABLET, APPLY, WASH, WEEKLY }

val ALL_TASKS = listOf(
    TaskDef("m1","T.Neurotec Plus","1 tablet after food",Section.MORNING,"Tablet",PillType.TABLET),
    TaskDef("m2","T.Trichotex","1 tablet after food",Section.MORNING,"Tablet",PillType.TABLET),
    TaskDef("m3","T.Biotree (AM)","1 tablet after food",Section.MORNING,"Tablet",PillType.TABLET),
    TaskDef("m4","Cufin Dusting Powder","Apply on scalp after bath",Section.MORNING,"Apply",PillType.APPLY),
    TaskDef("m5","Rebrote FX 5%","30 drops on scalp — don't rinse",Section.MORNING,"Apply",PillType.APPLY),
    TaskDef("a1","T.Rucal CM","1 tablet after lunch (Calcium)",Section.AFTERNOON,"Tablet",PillType.TABLET),
    TaskDef("n1","T.Sandro","1 tablet after food (DHT blocker)",Section.NIGHT,"Tablet",PillType.TABLET),
    TaskDef("n2","T.Minodez 5mg","1 tablet after food (Oral Minoxidil)",Section.NIGHT,"Tablet",PillType.TABLET),
    TaskDef("n3","T.Biotree (PM)","1 tablet after food",Section.NIGHT,"Tablet",PillType.TABLET),
    TaskDef("n4","Rebrote FX 5% (Night)","30 drops on scalp before bed",Section.NIGHT,"Apply",PillType.APPLY),
    TaskDef("w1","Derma Roller","Gently roll over scalp",Section.WEEKLY,"Weekly",PillType.WEEKLY),
    TaskDef("w2","Stemcello","16 drops after Derma Roller",Section.WEEKLY,"Weekly",PillType.WEEKLY),
    TaskDef("w3","T.Trip-D (Vit D3)","1 tablet at night — once a week",Section.WEEKLY,"Weekly",PillType.WEEKLY),
)

// ── Photo Storage helper ──────────────────────────────────────────────────────
object PhotoStorage {
    fun weekFolder(context: Context, weekNumber: Int): File {
        val f = File(context.filesDir, "scalp_photos/Week_%02d".format(weekNumber))
        if (!f.exists()) f.mkdirs(); return f
    }
    fun newPhotoFile(context: Context, weekNumber: Int, tag: String): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(weekFolder(context, weekNumber), "scalp_${tag}_$ts.jpg")
    }
    fun deleteFiles(p: ScalpPhoto) = listOf(p.photoPath1, p.photoPath2).filter { it.isNotBlank() }.forEach { File(it).delete() }
    fun formatDateTime(ms: Long): String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(ms))
    fun formatDate(d: String): String = try {
        SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            .format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(d)!!)
    } catch (e: Exception) { d }
}
