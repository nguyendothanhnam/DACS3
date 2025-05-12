package com.hunglevi.expense_mdc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.hunglevi.expense_mdc.data.model.Feedback
import com.hunglevi.expense_mdc.data.repository.FeedbackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class FeedbackViewModel(private val repository: FeedbackRepository) : ViewModel() {

    private val userId = MutableStateFlow<Int?>(null)

    fun setUserId(id: Int) {
        userId.value = id
    }

    val feedbacks: Flow<List<Feedback>> = userId.filterNotNull().flatMapLatest { id ->
        repository.getFeedbacksByUserId(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun insertFeedback(feedback: Feedback) {
        viewModelScope.launch {
            repository.insertFeedback(feedback)
        }
    }

    fun deleteFeedback(feedback: Feedback) {
        viewModelScope.launch {
            repository.deleteFeedback(feedback)
        }
    }
}