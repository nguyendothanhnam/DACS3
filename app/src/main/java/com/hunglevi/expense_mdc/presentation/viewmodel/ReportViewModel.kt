package com.hunglevi.expense_mdc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.hunglevi.expense_mdc.data.model.Report
import com.hunglevi.expense_mdc.data.repository.ReportRepository

class ReportViewModel(private val repository: ReportRepository) : ViewModel() {
    val reports: Flow<List<Report>> = repository.allReports

    fun insertReport(report: Report) {
        viewModelScope.launch {
            repository.insertReport(report)
        }
    }

    fun deleteReport(report: Report) {
        viewModelScope.launch {
            repository.deleteReport(report)
        }
    }
}