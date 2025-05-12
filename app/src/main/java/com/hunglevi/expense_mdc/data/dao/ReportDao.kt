package com.hunglevi.expense_mdc.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.hunglevi.expense_mdc.data.model.Report

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<Report>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: Report)

    @Delete
    suspend fun deleteReport(report: Report)
}