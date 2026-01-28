package com.example.sortify.viewmodel

import android.app.Application
import android.graphics.RectF
import androidx.lifecycle.*
import com.example.sortify.data.AppDatabase
import com.example.sortify.data.ScanEntity
import com.example.sortify.data.ScanItemEntity
import com.example.sortify.model.WasteCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class LiveDetection(
    val classId: Int,
    val name: String,
    val recyclable: Boolean,
    val rect: RectF,
    val score: Float
)

data class HomeStats(
    val totalScans: Int,
    val totalRecyclableFound: Int,
    val scansToday: Int
)

class ScanViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.get(app).scanDao()

    private val _latestDetections = MutableStateFlow<List<LiveDetection>>(emptyList())
    val latestDetections: StateFlow<List<LiveDetection>> = _latestDetections.asStateFlow()

    private val _savedEvent = MutableLiveData<Long>()
    val savedEvent: LiveData<Long> = _savedEvent

    val recentScans = dao.recentScans(limit = 20).asLiveData()

    private val statsFlow: Flow<HomeStats> = combine(
        dao.totalScans(),
        dao.totalRecyclableFound(),
        dao.scansToday(startOfTodayMillis())
    ) { total, recyclable, today ->
        HomeStats(total, recyclable, today)
    }

    val stats: LiveData<HomeStats> = statsFlow.asLiveData()

    fun updateDetections(list: List<LiveDetection>) {
        _latestDetections.value = list
    }

    fun saveCurrentScan() {
        val current = _latestDetections.value
        if (current.isEmpty()) return

        // Count duplicates by classId
        val grouped = current.groupBy { it.classId }.map { (classId, detections) ->
            val name = WasteCatalog.safeName(classId)
            val recyclable = WasteCatalog.safeRecyclable(classId)
            ScanItemEntity(
                scanId = 0,
                classId = classId,
                name = name,
                recyclable = recyclable,
                count = detections.size
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val scanId = dao.insertScan(ScanEntity(timestamp = System.currentTimeMillis()))
            val items = grouped.map { it.copy(scanId = scanId) }
            dao.insertItems(items)

            _savedEvent.postValue(System.currentTimeMillis())
        }
    }

    private fun startOfTodayMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
