package com.hunglevi.expense_mdc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedbacks")
data class Feedback(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,           // Auto-increment primary key
    val userId: Int,      // User's name
    val content : String, // Feedback content
    val createdAt: String, // Feedback creation date
    val response: String? // Response to the feedback
)