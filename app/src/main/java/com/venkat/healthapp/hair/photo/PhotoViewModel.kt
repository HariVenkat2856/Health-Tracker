package com.venkat.healthapp.hair.photo

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.venkat.healthapp.common.AppDatabase
import com.venkat.healthapp.hair.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PhotoViewModel(private val app: Application, db: AppDatabase) : AndroidViewModel(app) {
    private val dao = db.scalpPhotoDao()

    val allPhotos: StateFlow<List<ScalpPhoto>> = dao.allPhotos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestPhoto: StateFlow<ScalpPhoto?> = dao.latestPhoto()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val photosByWeek: StateFlow<Map<Int, List<ScalpPhoto>>> = allPhotos
        .map { list -> list.groupBy { it.weekNumber }.toSortedMap(reverseOrder()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private var tempFile1: File? = null
    private var tempFile2: File? = null
    var capturedUri1: Uri? = null
    var capturedUri2: Uri? = null
    private var pendingWeekNumber: Int = 1

    fun prepareCapture(): Int {
        val photos = allPhotos.value
        pendingWeekNumber = if (photos.isEmpty()) 1 else photos.maxOf { it.weekNumber } + 1
        return pendingWeekNumber
    }

    fun preparePhotoUri(context: Context, viewTag: String): Uri {
        val file = PhotoStorage.newPhotoFile(context, pendingWeekNumber, viewTag)
        if (viewTag == "front") { tempFile1 = file; capturedUri1 = null }
        else                    { tempFile2 = file; capturedUri2 = null }
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun onPhoto1Captured() {
        capturedUri1 = tempFile1?.let { FileProvider.getUriForFile(app, "${app.packageName}.provider", it) }
    }
    fun onPhoto2Captured() {
        capturedUri2 = tempFile2?.let { FileProvider.getUriForFile(app, "${app.packageName}.provider", it) }
    }

    fun saveEntry(label: String) {
        val file1 = tempFile1 ?: return
        if (!file1.exists() || file1.length() == 0L) return
        viewModelScope.launch {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            dao.insert(ScalpPhoto(
                weekLabel  = "Week $pendingWeekNumber",
                date       = today,
                capturedAt = System.currentTimeMillis(),
                photoPath1 = file1.absolutePath,
                photoPath2 = if (tempFile2?.exists() == true && tempFile2!!.length() > 0) tempFile2!!.absolutePath else "",
                label      = label.trim(),
                weekNumber = pendingWeekNumber
            ))
            tempFile1 = null; tempFile2 = null
            capturedUri1 = null; capturedUri2 = null
        }
    }

    fun cancelCapture() {
        tempFile1?.delete(); tempFile2?.delete()
        tempFile1 = null; tempFile2 = null
        capturedUri1 = null; capturedUri2 = null
    }

    fun deletePhoto(photo: ScalpPhoto) {
        viewModelScope.launch {
            PhotoStorage.deleteFiles(photo)
            dao.delete(photo)
        }
    }
}

class PhotoViewModelFactory(private val app: Application, private val db: AppDatabase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PhotoViewModel(app, db) as T
}
