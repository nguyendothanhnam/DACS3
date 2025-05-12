package com.hunglevi.expense_mdc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,           // Auto-increment primary key
    val name: String,      // Category name
    val icon: String,     // Category icon
    val description: String? = null, // Optional description
    val userId: Int? = null // Optional user ID for multi-user support
)