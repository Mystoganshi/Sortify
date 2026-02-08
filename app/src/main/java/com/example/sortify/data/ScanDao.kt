package com.example.sortify.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {

    @Insert
    suspend fun insertScan(scan: ScanEntity): Long

    @Insert
    suspend fun insertItems(items: List<ScanItemEntity>)

    @Transaction
    @Query("SELECT * FROM scans ORDER BY timestamp DESC LIMIT :limit")
    fun recentScans(limit: Int): Flow<List<ScanWithItems>>

    @Query("SELECT COUNT(*) FROM scans")
    fun totalScans(): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(count),0) FROM scan_items
        WHERE recyclable = 1
    """)
    fun totalRecyclableFound(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM scans
        WHERE timestamp >= :startOfDayMillis
    """)
    fun scansToday(startOfDayMillis: Long): Flow<Int>

    @Query("DELETE FROM scan_items WHERE scanId = :scanId")
    suspend fun deleteItems(scanId: Long)

    @Query("DELETE FROM scans WHERE id = :scanId")
    suspend fun deleteScan(scanId: Long)
}
