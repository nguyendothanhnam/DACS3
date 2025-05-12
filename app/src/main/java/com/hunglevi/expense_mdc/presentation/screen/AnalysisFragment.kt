package com.hunglevi.expense_mdc.presentation.screen

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.adapter.TransactionsAdapter
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.repository.TransactionRepository
import com.hunglevi.expense_mdc.databinding.FragmentAnalysisBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.TransactionViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class AnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    private val transactionViewModel: TransactionViewModel by viewModels {
        ViewModelFactory(
            transactionRepository = TransactionRepository(
                AppDatabase.getInstance(requireContext()).transactionDao() // Use Activity context here
            )
        )
    }
    private lateinit var transactionAdapter: TransactionsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = context?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref?.getInt("USER_ID", -1)
        transactionViewModel.setUserId(userId ?: -1)
        // Initialize all sections
        updateBarChartForLast7Days()
        setupFinancialSummary()
        setupNavigation()
    }


    private fun setupFinancialSummary() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionViewModel.transactions.collect { transactions ->
                    val totalIncome = transactions.filter { it.type == "Income" }.sumOf { it.amount }
                    val totalExpense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }

                    // Update financial summary UI
                    binding.incomeValue.text = "$${String.format("%.2f", totalIncome)}"
                    binding.expenseValue.text = "$${String.format("%.2f", totalExpense)}"
                    val sharedPref = requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE)
                    val savedBudget = sharedPref.getFloat("USER_BUDGET", 0f)
                    val goalAmount = savedBudget.toDouble()
                    // Update goal progress
                    val currentAmount = totalIncome + totalExpense
                    val progress = ((currentAmount / goalAmount) * 100).coerceIn(0.0, 100.0).toInt()

                    val currentProgress = calculateProgress(currentAmount, goalAmount)

                    binding.progressBar.max = 100
                    binding.progressBar.progress = currentProgress
                    binding.progressPercentage.text = String.format("%d%%", progress)
                    binding.progressGoal.text = "Goal: $${String.format("%.2f", goalAmount)}"
                }
            }
        }
    }
    fun calculateProgress(currentAmount: Double, goalAmount: Double): Int {
        if (goalAmount <= 0) {
            throw IllegalArgumentException("Goal amount must be greater than zero.")
        }
        val progress = ((currentAmount / goalAmount) * 100).coerceIn(0.0, 100.0) // Ensure between 0% and 100%
        return progress.toInt()
    }

    private fun setupNavigation() {
        // Navigate to CalendarFragment when graphIcon is clicked
        binding.graphIcon.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right, // Enter animation
                    R.anim.slide_out_left, // Exit animation
                    R.anim.slide_in_left,  // Pop enter animation
                    R.anim.slide_out_right // Pop exit animation
                )
                .replace(R.id.fragmentContainer, CalendarFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to SearchFragment when searchIcon is clicked
        binding.searchIcon.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right, // Enter animation
                    R.anim.slide_out_left, // Exit animation
                    R.anim.slide_in_left,  // Pop enter animation
                    R.anim.slide_out_right // Pop exit animation
                )
                .replace(R.id.fragmentContainer, SearchFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateBarChartForLast7Days() {
        viewLifecycleOwner.lifecycleScope.launch {
            val (incomeData, expenseData) = transactionViewModel.getTransactionsForLast7Days()
            val incomeDataFloat: List<Float> = incomeData.map { it.toFloat() }
            val expenseDataFloat: List<Float> = expenseData.map { it.toFloat() }

            binding.barChartView.setData(incomeDataFloat, expenseDataFloat, "Last 7 Days")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}