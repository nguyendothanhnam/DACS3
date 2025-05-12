package com.hunglevi.expense_mdc.data.repository

import com.hunglevi.expense_mdc.data.dao.TransactionDao
import com.hunglevi.expense_mdc.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    fun calculateStartDateForLast7Days(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun getTransactionsForLast7Days(userId: Int): List<Transaction> {
        val startDate = calculateStartDateForLast7Days()
        val endDate = getCurrentDate()
        return transactionDao.getTransactionsForPeriod(startDate, endDate, userId)
    }
    suspend fun getTransactionsForPeriod(startDate: String, endDate: String, userId: Int): List<Transaction> {
        return transactionDao.getTransactionsForPeriod(startDate, endDate, userId)
    }
    suspend fun getTransactionById(id: Int): Transaction? {
        return transactionDao.getTransactionById(id)
    }
    fun getAllTransactionsByUserId(userId: Int): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsByUserId(userId)
    }
}