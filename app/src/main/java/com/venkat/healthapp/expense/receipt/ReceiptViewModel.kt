package com.venkat.healthapp.expense.receipt

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.*
import com.venkat.healthapp.common.AppDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReceiptViewModel(val app: Application, db: AppDatabase) : AndroidViewModel(app) {

    private val dao = db.receiptDao()

    val allReceipts = dao.allReceipts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val standaloneReceipts = dao.standaloneReceipts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _ocrResult   = MutableStateFlow<OcrResult?>(null)
    val ocrResult: StateFlow<OcrResult?> = _ocrResult

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private var pendingFile: File? = null

    // ── Prepare camera file ───────────────────────────────────────────────────
    fun prepareCameraFile(context: Context): Uri {
        val file = ReceiptStorage.newReceiptFile(context)
        pendingFile = file
        return ReceiptStorage.getUri(context, file)
    }

    // ── Process captured photo with OCR ───────────────────────────────────────
    fun processPhoto() {
        val file = pendingFile ?: return
        if (!file.exists() || file.length() == 0L) return

        viewModelScope.launch {
            _isProcessing.value = true
            _ocrResult.value = null
            try {
                // ✅ Compress image to reduce file size
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 2  // halves width and height = quarter file size
                }
                val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
                if (bitmap != null) {
                    // Save compressed version back to file
                    file.outputStream().use { out ->
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, out)
                    }
                    val result = OcrProcessor.processImage(bitmap)
                    _ocrResult.value = result
                }
            } catch (e: Exception) {
                _ocrResult.value = OcrResult("", 0f, "", "")
            } finally {
                _isProcessing.value = false
            }
        }
    }

    // ── Save receipt ──────────────────────────────────────────────────────────
    fun saveReceipt(
        expenseId: Int = 0,
        note: String = "",
        ocrResult: OcrResult? = null
    ) {
        val file = pendingFile ?: return
        if (!file.exists()) return

        viewModelScope.launch {
            dao.insert(Receipt(
                expenseId        = expenseId,
                imagePath        = file.absolutePath,
                // ✅ Truncate OCR text to max 500 chars — prevents oversized Firestore docs
                ocrText          = (ocrResult?.rawText ?: "").take(500),
                detectedAmount   = ocrResult?.detectedAmount ?: 0f,
                detectedDate     = ocrResult?.detectedDate ?: "",
                detectedMerchant = ocrResult?.detectedMerchant ?: "",
                note             = note,
                date             = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            ))
            pendingFile = null
            _ocrResult.value = null
        }
    }

    // ── Link receipt to expense ───────────────────────────────────────────────
    fun linkReceiptToExpense(receipt: Receipt, expenseId: Int) {
        viewModelScope.launch {
            dao.update(receipt.copy(expenseId = expenseId))
        }
    }

    // ── Delete receipt ────────────────────────────────────────────────────────
    fun deleteReceipt(receipt: Receipt) {
        viewModelScope.launch {
            ReceiptStorage.deleteFile(receipt)
            dao.delete(receipt)
        }
    }

    fun cancelCapture() {
        pendingFile?.delete()
        pendingFile   = null
        _ocrResult.value = null
    }

    fun clearOcr() { _ocrResult.value = null }

    fun receiptsForExpense(expenseId: Int) = dao.receiptsForExpense(expenseId)

    fun searchReceipts(q: String) = dao.search(q)
}

class ReceiptViewModelFactory(
    private val app: Application,
    private val db: AppDatabase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ReceiptViewModel(app, db) as T
}