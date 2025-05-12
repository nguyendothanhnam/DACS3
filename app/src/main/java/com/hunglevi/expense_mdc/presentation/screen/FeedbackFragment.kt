package com.hunglevi.expense_mdc.presentation.screen

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunglevi.expense_mdc.adapter.FeedbackAdapter
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.model.Feedback
import com.hunglevi.expense_mdc.data.repository.FeedbackRepository
import com.hunglevi.expense_mdc.databinding.FragmentFeedbackBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.FeedbackViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedbackFragment : Fragment() {
    private lateinit var binding: FragmentFeedbackBinding
    private lateinit var feedbackAdapter: FeedbackAdapter
    private val feedbackViewModel: FeedbackViewModel by viewModels { 
        ViewModelFactory(feedbackRepository = FeedbackRepository(AppDatabase.getInstance(requireContext()).feedbackDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = context?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref?.getInt("USER_ID", -1)
        feedbackViewModel.setUserId(userId ?: -1)
        feedbackAdapter = FeedbackAdapter(emptyList())
        binding.feedbackRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feedbackRecyclerView.adapter = feedbackAdapter

        // Observe feedback history
        viewLifecycleOwner.lifecycleScope.launch {
            feedbackViewModel.feedbacks.collect { feedbackList ->
                feedbackAdapter.updateFeedbacks(feedbackList)
            }
        }

        // Handle feedback submission
        binding.submitFeedbackButton.setOnClickListener {
            val content = binding.feedbackContentInput.text.toString()
            if (content.isNotBlank()) {
                feedbackViewModel.insertFeedback(
                    Feedback(
                        userId = getUserId(),
                        content = content,
                        createdAt = getCurrentDate(),
                        response = null
                    )
                )
                binding.feedbackContentInput.setText("") // Clear input
                Toast.makeText(requireContext(), "Feedback submitted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter feedback content", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserId(): Int {
        // Retrieve user ID from SharedPreferences or other sources
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("USER_ID", -1)
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }
}