package com.hunglevi.expense_mdc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,           // Auto-increment primary key
    val categoryId: Int,      // Transaction category
    val userId: Int?,         // User ID
    val amount: Double,        // Transaction amount
    val type: String,          // Income or Expense
    val date: String,          // Transaction date
    val description: String?,   // Optional description
    val createdAt: String?,       // Creation timestamp
)