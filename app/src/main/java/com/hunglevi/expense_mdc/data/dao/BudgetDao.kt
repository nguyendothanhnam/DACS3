package com.hunglevi.expense_mdc.data.dao

import androidx.room.*
import androidx.room.OnConflictStrategy
import com.hunglevi.expense_mdc.data.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets ORDER BY createdAt DESC")
    fun getAllBudgets(): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: Int): Budget?

    @Query("SELECT * FROM budgets WHERE userId = :id")
    suspend fun getBudgetByUserId(id: Int): Budget?


    @Query("SELECT SUM(amount) FROM budgets")
    fun getTotalBudget(): Flow<Double>

    @Query("SELECT * FROM budgets WHERE createdAt BETWEEN :startDate AND :endDate")
    fun filterBudgetsByDate(startDate: String, endDate: String): Flow<List<Budget>>
}