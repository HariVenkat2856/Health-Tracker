package com.venkat.healthapp.water.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,          // "2026-05-18"
    val amountMl: Int,         // ml per entry
    val loggedAt: Long = System.currentTimeMillis()
)

@Dao interface WaterLogDao {
    @Insert suspend fun insert(log: WaterLog)
    @Delete suspend fun delete(log: WaterLog)
    @Query("SELECT * FROM water_logs WHERE date=:date ORDER BY loggedAt")
    fun logsForDate(date: String): Flow<List<WaterLog>>
    @Query("SELECT SUM(amountMl) FROM water_logs WHERE date=:date")
    fun totalForDate(date: String): Flow<Int?>



    // Add to WaterLogDao
    @Query("SELECT SUM(amountMl) FROM water_logs WHERE date = :date")
    suspend fun totalForDateSync(date: String): Int?

    @Query("SELECT DISTINCT date FROM water_logs ORDER BY date DESC")
    fun allDates(): Flow<List<String>>



}
