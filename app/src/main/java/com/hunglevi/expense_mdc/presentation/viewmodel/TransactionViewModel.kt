package com.hunglevi.expense_mdc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.hunglevi.expense_mdc.data.model.Transaction
import com.hunglevi.expense_mdc.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val userId = MutableStateFlow<Int?>(null)

    fun setUserId(id: Int) {
        userId.value = id
    }

    suspend fun getTransactionsForLast7Days(): Pair<List<Double>, List<Double>> {
        val transactions = repository.getTransactionsForLast7Days(userId = userId.value ?: -1)

        val incomeData = transactions.filter { it.type == "Income" }.map { it.amount }
        val expenseData = transactions.filter { it.type == "Expense" }.map { it.amount }

        return Pair(incomeData, expenseData)
    }
    val transactions: Flow<List<Transaction>> = userId.filterNotNull().flatMapLatest { id ->
        repository.getAllTransactionsByUserId(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

//    fun filterTransactionsByCategory(categoryId: Int): Flow<List<Transaction>> {
//        return repository.filterTransactionsByCategory(categoryId)
//    }
//
//    fun filterTransactionsByType(transactionType: String): Flow<List<Transaction>> {
//        return repository.filterTransactionsByType(transactionType)
//    }
//
//    fun filterTransactionsByDateRange(startDate: String, endDate: String): Flow<List<Transaction>> {
//        return repository.filterTransactionsByDateRange(startDate, endDate)
//    }
}