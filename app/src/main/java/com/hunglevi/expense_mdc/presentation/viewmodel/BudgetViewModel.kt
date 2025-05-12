package com.hunglevi.expense_mdc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.hunglevi.expense_mdc.data.model.Budget
import com.hunglevi.expense_mdc.data.repository.BudgetRepository

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {
    val budgets: Flow<List<Budget>> = repository.allBudgets

    fun insertBudget(budget: Budget) {
        viewModelScope.launch {
            repository.insertBudget(budget)
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }
}