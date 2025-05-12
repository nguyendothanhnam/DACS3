package com.hunglevi.expense_mdc.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.hunglevi.expense_mdc.data.model.Feedback

@Dao
interface FeedbackDao {
    @Query("SELECT * FROM feedbacks ORDER BY createdAt DESC")
    fun getAllFeedbacks(): Flow<List<Feedback>>

    @Query("SELECT * FROM feedbacks where userId = :userId ORDER BY createdAt DESC")
    fun getAllFeedbacksByUserId(userId:Int): Flow<List<Feedback>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: Feedback)

    @Delete
    suspend fun deleteFeedback(feedback: Feedback)
}