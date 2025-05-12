package com.hunglevi.expense_mdc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,           // Auto-increment primary key
    val username: String,      // User's name
    val email: String,         // User's email
    val password: String,      // User's password
    val role: String,      // User's role (e.g., admin, user)
    val createdAt: String,
    val profileImage: String?,     // Account creation date)
     )