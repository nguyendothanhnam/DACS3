package com.hunglevi.expense_mdc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.repository.*

class ViewModelFactory(
    private val userRepository: UserRepository? = null,
    private val categoryRepository: CategoryRepository? = null,
    private val budgetRepository: BudgetRepository? = null,
    private val transactionRepository: TransactionRepository? = null,
    private val reportRepository: ReportRepository? = null,
    private val feedbackRepository: FeedbackRepository? = null


) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(userRepository!!) as T
            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(categoryRepository!!) as T
            modelClass.isAssignableFrom(BudgetViewModel::class.java) -> BudgetViewModel(budgetRepository!!) as T
            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> TransactionViewModel(transactionRepository!!) as T
            modelClass.isAssignableFrom(ReportViewModel::class.java) -> ReportViewModel(reportRepository!!) as T
            modelClass.isAssignableFrom(FeedbackViewModel::class.java) -> FeedbackViewModel(feedbackRepository!!) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")

        }

    }

}