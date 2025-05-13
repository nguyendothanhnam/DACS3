package com.hunglevi.expense_mdc.presentation.screen

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.adapter.TransactionsAdapter
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.model.Transaction
import com.hunglevi.expense_mdc.data.repository.CategoryRepository
import com.hunglevi.expense_mdc.data.repository.TransactionRepository
import com.hunglevi.expense_mdc.databinding.FragmentTransactionBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.CategoryViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.TransactionViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.getValue

class TransactionFragment : Fragment() {
    private lateinit var binding: FragmentTransactionBinding
    private val transactionViewModel: TransactionViewModel by viewModels {
        ViewModelFactory(
            transactionRepository = TransactionRepository(
                AppDatabase.getInstance(requireContext())
                    .transactionDao() // Use TransactionDao here
            )
        )
    }
    private val categoryViewModel: CategoryViewModel by viewModels {
        ViewModelFactory(
            categoryRepository = CategoryRepository(
                AppDatabase.getInstance(requireContext()).categoryDao() // Use TransactionDao here
            )
        )
    }


    private lateinit var transactionAdapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize ViewBinding
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = context?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref?.getInt("USER_ID", -1)
        // Initialize adapter with an empty list
        transactionViewModel.setUserId(userId ?: -1) // Set user ID in ViewModel
        setupAdapter()
        // Set up RecyclerView
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter
        }

        // Observe and dynamically update the transactions list
        setupTransactionList()
        binding.addTransactionButton.setOnClickListener {
            openAddTransactionDialog(userId)
        }
        setupFinancialSummary()

    }

    private fun setupFinancialSummary() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                transactionViewModel.transactions.collect { transactions ->
                    val totalIncome =
                        transactions.filter { it.type == "Income" }.sumOf { it.amount }
                    val totalExpense =
                        transactions.filter { it.type == "Expense" }.sumOf { it.amount }

                    // Update financial summary UI
                    binding.incomeValue.text = "$${String.format("%.2f", totalIncome)}"
                    binding.expenseValue.text = "$${String.format("%.2f", totalExpense)}"

                    // Update goal progress
                    val currentAmount = totalIncome + totalExpense
                    binding.totalIncomeValue.text = currentAmount.toString()
                }
            }
        }
    }

    private fun setupDatePicker(editText: EditText) {
        editText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )
                    editText.setText(formattedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    suspend fun fetchCategoryName(categoryId: Int): String {
        val category = categoryViewModel.getCategoryById(categoryId)
        return category?.name ?: ""
    }

    private fun setupAdapter() {
        transactionAdapter = TransactionsAdapter(
            transactions = emptyList(),
            onEdit = { transaction -> openEditTransactionDialog(transaction) },
            onDelete = { transaction -> showDeleteConfirmation(transaction) },
            fetchCategoryName = { categoryId ->
                var categoryName = ""
                viewLifecycleOwner.lifecycleScope.launch {
                    categoryName = fetchCategoryName(categoryId)
                }
                categoryName
            },
            fetchCategoryIcon = { categoryId ->
                val category = categoryViewModel.getCategoryById(categoryId)
                category?.icon ?: "category.png"
            }
        )

        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }

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
                Toast.makeText(
                    requireContext(),
                    "Transaction deleted successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun openEditTransactionDialog(transaction: Transaction) {
        // Inflate the dialog layout
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_transaction, null)

        // Create an AlertDialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        // Access views in the dialog
        val amountInput = dialogView.findViewById<EditText>(R.id.transactionAmountInput)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.transactionDescriptionInput)
        val dateInput = dialogView.findViewById<EditText>(R.id.transactionDateInput)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        // Set up the date picker for the date input
        setupDatePicker(dateInput)

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
                Toast.makeText(requireContext(), "Please enter valid details!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss() // Close the dialog without saving
        }

        dialog.show()
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

    private fun openAddTransactionDialog(userIdAdd: Int?) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_transaction, null)

        // Create an AlertDialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

            .setCancelable(false)

        val dialog = dialogBuilder.create()

        // Access views in the dialog
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.transactionCategorySpinner)
        val amountInput = dialogView.findViewById<EditText>(R.id.transactionAmountInput)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.transactionDescriptionInput)
        val dateInput = dialogView.findViewById<EditText>(R.id.transactionDateInput)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
        setupDatePicker(dateInput)
        // Fetch categories from the ViewModel or Repository
        categoryViewModel.setUserId(userIdAdd ?: -1) // Set user ID in ViewModel
        var categories: List<String> = emptyList() // Declare categories outside

        viewLifecycleOwner.lifecycleScope.launch {
            categoryViewModel.categories.collect { categoryList ->
                categories = categoryList.map { it.name } // Update categories dynamically
                val categoryAdapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = categoryAdapter
            }
        }
        // Save transaction on Save button click
        saveButton.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()
            val date = dateInput.text.toString()

            if (amount != null && date.isNotBlank()) {
                val newTransaction = Transaction(
                    id = 0, // Auto-generated in the database
                    categoryId = categories.indexOf(selectedCategory) + 1, // Map to the actual category ID from database
                    userId = userIdAdd, // Use default or current user's ID
                    amount = amount,
                    description = description,
                    type = if (amount >= 0) "Income" else "Expense",
                    date = date,
                    createdAt = date
                )
                transactionViewModel.insertTransaction(newTransaction)
                Toast.makeText(requireContext(), "Transaction added!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Invalid input!", Toast.LENGTH_SHORT).show()
            }
        }
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dialog.dismiss() // Close the dialog without saving
        }

        dialog.show()
    }
}