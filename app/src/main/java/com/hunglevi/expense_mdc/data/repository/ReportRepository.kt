package com.hunglevi.expense_mdc.data.repository

import com.hunglevi.expense_mdc.data.dao.ReportDao
import com.hunglevi.expense_mdc.data.model.Report
import kotlinx.coroutines.flow.Flow

class ReportRepository(private val reportDao: ReportDao) {
    val allReports: Flow<List<Report>> = reportDao.getAllReports()

    suspend fun insertReport(report: Report) {
        reportDao.insertReport(report)
    }

    suspend fun deleteReport(report: Report) {
        reportDao.deleteReport(report)
    }
}