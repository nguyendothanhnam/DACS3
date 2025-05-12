package com.hunglevi.expense_mdc.data.repository

import com.hunglevi.expense_mdc.data.dao.FeedbackDao
import com.hunglevi.expense_mdc.data.model.Feedback
import kotlinx.coroutines.flow.Flow

class FeedbackRepository(private val feedbackDao: FeedbackDao) {

    val allFeedbacks: Flow<List<Feedback>> = feedbackDao.getAllFeedbacks()
    fun getFeedbacksByUserId(userId: Int): Flow<List<Feedback>> {
        return feedbackDao.getAllFeedbacksByUserId(userId)
    }

    suspend fun insertFeedback(feedback: Feedback) {
        feedbackDao.insertFeedback(feedback)
    }

    suspend fun deleteFeedback(feedback: Feedback) {
        feedbackDao.deleteFeedback(feedback)
    }
}