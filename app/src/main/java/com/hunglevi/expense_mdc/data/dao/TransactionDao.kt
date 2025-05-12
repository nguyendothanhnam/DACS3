package com.hunglevi.expense_mdc.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hunglevi.expense_mdc.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions where userId = :userId ORDER BY createdAt DESC")
    fun getAllTransactionsByUserId(userId: Int): Flow<List<Transaction>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId")
    fun filterTransactionsByCategory(categoryId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :transactionType")
    fun filterTransactionsByType(transactionType: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE createdAt BETWEEN :startDate AND :endDate")
    fun filterTransactionsByDateRange(startDate: String, endDate: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date <= :endDate AND userId = :userId")
    suspend fun getTransactionsForPeriod(startDate: String, endDate: String,userId: Int): List<Transaction>
}