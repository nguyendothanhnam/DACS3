package com.hunglevi.expense_mdc.data.repository

import com.hunglevi.expense_mdc.data.dao.BudgetDao
import com.hunglevi.expense_mdc.data.model.Budget
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {
    val allBudgets: Flow<List<Budget>> = budgetDao.getAllBudgets()

    suspend fun insertBudget(budget: Budget) {
        budgetDao.insertBudget(budget)
    }

    suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(budget)
    }

    suspend fun getBudgetById(id: Int): Budget? {
        return budgetDao.getBudgetById(id)
    }
}