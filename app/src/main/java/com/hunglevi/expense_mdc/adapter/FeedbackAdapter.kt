package com.hunglevi.expense_mdc.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.data.model.Feedback
import com.hunglevi.expense_mdc.databinding.ItemFeedbackBinding

class FeedbackAdapter(private var feedbacks: List<Feedback>) :
    RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    inner class FeedbackViewHolder(val binding: ItemFeedbackBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val binding = ItemFeedbackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedbackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbacks[position]
        holder.binding.feedbackContent.text = feedback.content
        holder.binding.feedbackCreatedAt.text = "Created on: ${feedback.createdAt}"

        if (!feedback.response.isNullOrBlank()) {
            holder.binding.feedbackResponse.visibility = View.VISIBLE
            holder.binding.feedbackResponse.text = "Response: ${feedback.response}"
        } else {
            holder.binding.feedbackResponse.visibility = View.GONE
        }
    }

    fun updateFeedbacks(newFeedbacks: List<Feedback>) {
        feedbacks = newFeedbacks
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = feedbacks.size
}