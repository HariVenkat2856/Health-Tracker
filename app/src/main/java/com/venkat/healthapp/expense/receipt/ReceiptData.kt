package com.venkat.healthapp.expense.receipt

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// ── Entity ────────────────────────────────────────────────────────────────────
@Entity(tableName = "receipts")
data class Receipt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expenseId: Int = 0,          // linked expense (0 = standalone)
    val imagePath: String,           // local file path
    val ocrText: String = "",        // raw OCR extracted text
    val detectedAmount: Float = 0f,  // amount detected by OCR
    val detectedDate: String = "",   // date detected by OCR
    val detectedMerchant: String = "", // merchant/shop name detected
    val note: String = "",
    val capturedAt: Long = System.currentTimeMillis(),
    val date: String                 // "2026-05-20"
)

// ── DAO ───────────────────────────────────────────────────────────────────────
@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(receipt: Receipt): Long

    @Update
    suspend fun update(receipt: Receipt)

    @Delete
    suspend fun delete(receipt: Receipt)

    @Query("SELECT * FROM receipts ORDER BY capturedAt DESC")
    fun allReceipts(): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE expenseId = :expenseId")
    fun receiptsForExpense(expenseId: Int): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE date = :date ORDER BY capturedAt DESC")
    fun receiptsForDate(date: String): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE expenseId = 0 ORDER BY capturedAt DESC")
    fun standaloneReceipts(): Flow<List<Receipt>>

    @Query("SELECT COUNT(*) FROM receipts")
    suspend fun count(): Int

    @Query("SELECT * FROM receipts WHERE ocrText LIKE '%' || :q || '%' OR detectedMerchant LIKE '%' || :q || '%' ORDER BY capturedAt DESC")
    fun search(q: String): Flow<List<Receipt>>
}

// ── File storage ──────────────────────────────────────────────────────────────
object ReceiptStorage {

    fun newReceiptFile(context: Context): File {
        val dir = File(context.filesDir, "receipts").also { if (!it.exists()) it.mkdirs() }
        val ts  = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(dir, "receipt_$ts.jpg")
    }

    fun getUri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    fun deleteFile(receipt: Receipt) = File(receipt.imagePath).delete()

    fun formatDateTime(ms: Long): String =
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(ms))
}

// ── OCR result ────────────────────────────────────────────────────────────────
data class OcrResult(
    val rawText: String,
    val detectedAmount: Float,
    val detectedDate: String,
    val detectedMerchant: String
)