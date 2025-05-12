package com.hunglevi.expense_mdc.data.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.hunglevi.expense_mdc.data.model.Budget
import com.hunglevi.expense_mdc.data.model.Category
import com.hunglevi.expense_mdc.data.model.Feedback
import com.hunglevi.expense_mdc.data.model.Report
import com.hunglevi.expense_mdc.data.model.Transaction
import com.hunglevi.expense_mdc.data.model.User

//@Database(entities = [Transaction::class, Feedback::class, Budget::class, Report::class, Category::class, User::class], version = 1, exportSchema = false)
@Database(
    entities = [Budget::class, Category::class, Feedback::class, Report::class, Transaction::class, User::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun reportDao(): ReportDao
    abstract fun transactionDao(): TransactionDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_database"
                ).fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}