package com.hunglevi.expense_mdc.presentation.screen

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunglevi.expense_mdc.LoginActivity
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.adapter.TransactionsAdapter
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.model.Transaction
import com.hunglevi.expense_mdc.data.repository.CategoryRepository
import com.hunglevi.expense_mdc.data.repository.TransactionRepository
import com.hunglevi.expense_mdc.databinding.FragmentHomeBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.CategoryViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.TransactionViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val transactionViewModel: TransactionViewModel by viewModels {
        ViewModelFactory(
            transactionRepository = TransactionRepository(
                AppDatabase.getInstance(requireContext()).transactionDao() // Use Activity context here
            )
        )
    }
    private val categoryViewModel: CategoryViewModel by viewModels {
        ViewModelFactory(
            categoryRepository = CategoryRepository(
                AppDatabase.getInstance(requireContext()).categoryDao() // Use Activity context here
            )
        )
    }

    private lateinit var transactionAdapter: TransactionsAdapter
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefListener: SharedPreferences.OnSharedPreferenceChangeListener
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userPref = context?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = userPref?.getInt("USER_ID", -1) ?: -1
        if (userId == -1) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            return
        }
        transactionViewModel.setUserId(userId)

        // Khởi tạo SharedPreferences và listener
        sharedPref = requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE)
        sharedPrefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "USER_BUDGET_$userId") {
                loadBudget() // Cập nhật UI khi USER_BUDGET của userId này thay đổi
            }
        }
        sharedPref.registerOnSharedPreferenceChangeListener(sharedPrefListener)

        setupAdapter()
        loadBudget()
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }
        setupTransactionList()
        setupFinancialSummary()
        binding.editGoalButton.setOnClickListener {
            openEditGoalDialog()
        }
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

                    // Update goal progress
                    val currentAmount = totalIncome + totalExpense
                    val savedBudget = sharedPref.getFloat("USER_BUDGET", 0f)
                    val goalAmount = savedBudget.toDouble()

                    if (goalAmount <= 0) {
                        binding.progressBar.visibility = View.GONE
                        binding.progressPercentage.text = "Please set a budget"
                    } else {
                        val currentProgress = calculateProgress(currentAmount, goalAmount)
                        binding.progressBar.visibility = View.VISIBLE
                        binding.progressBar.max = 100
                        binding.progressBar.progress = currentProgress
                        binding.progressPercentage.text = String.format("%d%%", currentProgress)
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        val categoryMap = categoryViewModel.categoryMap.value // Preloaded map from ViewModel

        transactionAdapter = TransactionsAdapter(
            transactions = emptyList(),
            onEdit = { transaction -> openEditTransactionDialog(transaction) },
            onDelete = { transaction -> showDeleteConfirmation(transaction) },
            fetchCategoryName = { categoryId -> categoryMap[categoryId] ?: "" }, // Fetch category name dynamically
            fetchCategoryIcon = { categoryId ->
                val category = categoryViewModel.getCategoryById(categoryId)
                category?.icon ?: "category.png"
            }
        )

        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }

        // Observe transactions and update the adapter
        viewLifecycleOwner.lifecycleScope.launch {
            transactionViewModel.transactions.collect { transactions ->
                transactionAdapter.updateTransactions(transactions)
            }
        }
    }
    private fun showDeleteConfirmation(transaction: Transaction) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Yes") { dialog, _ ->
                // Trigger deletion via ViewModel
                transactionViewModel.deleteTransaction(transaction)
                Toast.makeText(requireContext(), "Transaction deleted successfully!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun calculateProgress(currentAmount: Double, goalAmount: Double): Int {
        if (goalAmount <= 0) {
            throw IllegalArgumentException("Goal amount must be greater than zero.")
        }
        val progress = ((currentAmount / goalAmount) * 100).coerceIn(0.0, 100.0) // Ensure between 0% and 100%
        return progress.toInt()
    }

    private fun setupTransactionList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionViewModel.transactions.collect { transactions ->
                    if (transactions.isEmpty()) {
                        binding.emptyView.visibility = View.VISIBLE
                        binding.transactionsRecyclerView.visibility = View.GONE
                    } else {
                        binding.emptyView.visibility = View.GONE
                        binding.transactionsRecyclerView.visibility = View.VISIBLE
                        transactionAdapter.updateTransactions(transactions)
                    }
                }
            }
        }
    }

    private fun openEditTransactionDialog(transaction: Transaction) {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_transaction, null)

        // Create an AlertDialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit Transaction")
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        // Access views in the dialog
        val amountInput = dialogView.findViewById<EditText>(R.id.transactionAmountInput)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.transactionDescriptionInput)
        val dateInput = dialogView.findViewById<EditText>(R.id.transactionDateInput)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        // Pre-fill the dialog fields with current transaction details
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description ?: "")
        dateInput.setText(transaction.date)

        // Set button actions
        saveButton.setOnClickListener {
            val updatedAmount = amountInput.text.toString().toDoubleOrNull()
            val updatedDescription = descriptionInput.text.toString()
            val updatedDate = dateInput.text.toString()

            if (updatedAmount != null && updatedDate.isNotBlank()) {
                val updatedTransaction = transaction.copy(
                    amount = updatedAmount,
                    description = updatedDescription,
                    date = updatedDate
                )
                transactionViewModel.insertTransaction(updatedTransaction) // Trigger ViewModel to update
                Toast.makeText(requireContext(), "Transaction updated!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter valid details!", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss() // Close the dialog without saving
        }

        dialog.show()
    }
    private fun loadBudget() {
        val sharedPref = requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE)
        val savedBudget = sharedPref.getFloat("USER_BUDGET", 0f)
        binding.progressGoal.text = "Goal: $${String.format("%.2f", savedBudget)}"
    }
    private fun updateBudget(goalAmount: Double) {
        val sharedPref = requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putFloat("USER_BUDGET", goalAmount.toFloat()).apply()
    }


    private fun openEditGoalDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_goal, null)
        val goalInputField = dialogView.findViewById<EditText>(R.id.goalAmountInput)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit Financial Goal")
            .setPositiveButton("Save") { dialog, _ ->
                val goalInput = goalInputField?.text.toString().trim()

                if (goalInput.isNotBlank()) {
                    val goalAmount = goalInput.toDoubleOrNull()
                    if (goalAmount != null && goalAmount > 0) {
                        updateBudget(goalAmount) // Save budget update
                        Toast.makeText(requireContext(), "Budget updated successfully!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Invalid amount! Please enter a valid budget value.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter a goal amount!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }
}