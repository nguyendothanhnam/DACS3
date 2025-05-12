package com.hunglevi.expense_mdc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,           // Auto-increment primary key
    val userId: Int,      // User's name
    val startDate : String, // Start date of the report
    val endDate : String, // End date of the report
    val note: String, // Note or description of the report
    val createdAt: String
)