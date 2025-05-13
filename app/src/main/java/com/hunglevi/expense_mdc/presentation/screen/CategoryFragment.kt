package com.hunglevi.expense_mdc.presentation.screen

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.adapter.CategoryAdapter
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.model.Category
import com.hunglevi.expense_mdc.data.repository.CategoryRepository
import com.hunglevi.expense_mdc.data.repository.TransactionRepository
import com.hunglevi.expense_mdc.data.repository.UserRepository
import com.hunglevi.expense_mdc.databinding.FragmentCategoryBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.CategoryViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.TransactionViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.UserViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.jvm.java
import android.widget.ArrayAdapter

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val categoryViewModel: CategoryViewModel by viewModels {
        ViewModelFactory(
            categoryRepository = CategoryRepository(
                AppDatabase.getInstance(requireContext()).categoryDao()
            )
        )
    }
    private val transactionViewModel: TransactionViewModel by viewModels {
        ViewModelFactory(
            transactionRepository = TransactionRepository(
                AppDatabase.getInstance(requireContext())
                    .transactionDao() // Use Activity context here
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = context?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref?.getInt("USER_ID", -1)
        transactionViewModel.setUserId(userId ?: -1) // Set the user ID in the ViewModel
        categoryViewModel.setUserId(userId ?: -1) // Set the user ID in the ViewModel
        setupFinancialSummary()
        setupCategoryList()
        binding.addButton.setOnClickListener {
            openAddCategoryDialog(userId ?: -1) // Pass the user ID to the dialog
        }

    }

    private fun showEditOrDeleteDialog(category: Category) {
        val options = arrayOf("Edit Category", "Delete Category")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose an action for ${category.name}")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> openEditCategoryDialog(category) // Edit option
                    1 -> showDeleteCategoryConfirmation(category) // Delete option
                }
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun openEditCategoryDialog(category: Category) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_category, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        // Access views in the dialog
        val nameInput = dialogView.findViewById<EditText>(R.id.dialogCategoryName)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.dialogCategoryDescription)
        val iconSpinner = dialogView.findViewById<Spinner>(R.id.dialogIconSelector)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        // Pre-fill the dialog fields with current values
        nameInput.setText(category.name)
        descriptionInput.setText(category.description)

        // Thiết lập Spinner với danh sách tên icon từ string-array
        val iconNames = getIconNames(requireContext()) // Lấy danh sách tên icon
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, iconNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        iconSpinner.adapter = adapter

        // Chọn giá trị mặc định dựa trên icon hiện tại
        val currentIcon = category.icon.lowercase()
        val defaultPosition = iconNames.indexOfFirst { it.lowercase() == currentIcon }
        if (defaultPosition >= 0) {
            iconSpinner.setSelection(defaultPosition)
        }

        saveButton.setOnClickListener {
            val updatedName = nameInput.text.toString()
            val updatedDescription = descriptionInput.text.toString()
            val selectedIcon = iconSpinner.selectedItem.toString().trim()

            if (updatedName.isNotBlank() && updatedDescription.isNotBlank() && selectedIcon.isNotBlank()) {
                val updatedCategory = category.copy(
                    id = category.id, // Keep the same ID
                    name = updatedName,
                    description = updatedDescription,
                    icon = selectedIcon // Update with the selected icon
                )
                categoryViewModel.insertCategory(updatedCategory) // Call ViewModel to update
                Toast.makeText(requireContext(), "Category updated!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please fill out all fields!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialog.show()
    }

    private fun showDeleteCategoryConfirmation(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete '${category.name}'?")
            .setPositiveButton("Yes") { dialog, _ ->
                categoryViewModel.deleteCategory(category) // Call ViewModel to delete
                Toast.makeText(requireContext(), "Category deleted!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Cancel deletion
            }
            .create()
            .show()
    }

    private fun insertExampleCategories() {
        val exampleCategories = listOf(
            Category(id = 0, name = "Food", icon = "🍔", description = "Expenses on food", 1),
            Category(
                id = 0,
                name = "Transport",
                icon = "🚗",
                description = "Expenses on transport",
                1
            ),
            Category(id = 0, name = "Salary", icon = "💰", description = "Income from salary", 1),
            Category(
                id = 0,
                name = "Entertainment",
                icon = "🎮",
                description = "Expenses on entertainment",
                1
            )
        )

        lifecycleScope.launch {
            exampleCategories.forEach { category ->
                categoryViewModel.insertCategory(category)
            }
            Toast.makeText(requireContext(), "Example categories added!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCategoryList() {
        // Initialize the RecyclerView with a GridLayoutManager
        val gridLayoutManager = GridLayoutManager(context, 3) // 3 columns
        binding.categoryRecyclerView.layoutManager = gridLayoutManager

        // Set up the adapter
        val categoryAdapter = CategoryAdapter(emptyList()) { category ->
            // Handle item clicks
            showEditOrDeleteDialog(category)
        }
        binding.categoryRecyclerView.adapter = categoryAdapter

        // Collect the Flow of categories from the ViewModel
        lifecycleScope.launch {
            categoryViewModel.categories.collect { categoryList ->
                categoryAdapter.updateData(categoryList) // Dynamically update the adapter's data
            }
        }

        // Set up the FloatingActionButton to open the dialog
    }

    fun calculateProgress(currentAmount: Double, goalAmount: Double): Int {
        if (goalAmount <= 0) {
            throw IllegalArgumentException("Goal amount must be greater than zero.")
        }
        val progress =
            ((currentAmount / goalAmount) * 100).coerceIn(0.0, 100.0) // Ensure between 0% and 100%
        return progress.toInt()
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
                    val sharedPref =
                        requireContext().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE)
                    val savedBudget = sharedPref.getFloat("USER_BUDGET", 0f)
                    val goalAmount = savedBudget.toDouble()
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

    private fun openAddCategoryDialog(userIdAdd: Int) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.dialogCategoryName)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.dialogCategoryDescription)
        val iconSpinner = dialogView.findViewById<Spinner>(R.id.dialogIconSelector)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        // Thiết lập Spinner với danh sách tên icon
        val iconNames = getIconNames(requireContext()) // Lấy danh sách tên icon từ icon_options
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, iconNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        iconSpinner.adapter = adapter

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        // Sự kiện cho nút Save (Add)
        saveButton.setOnClickListener {
            val name = nameInput?.text.toString().trim()
            val description = descriptionInput?.text.toString().trim()
            val selectedIcon = iconSpinner.selectedItem?.toString()?.trim() ?: ""

            if (name.isBlank() || selectedIcon.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill in all required fields!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val newCategory = Category(
                    id = 0,
                    name = name,
                    icon = selectedIcon, // Assign the selected icon
                    description = description,
                    userId = userIdAdd // Optional user ID
                )
                categoryViewModel.insertCategory(newCategory)
                Toast.makeText(requireContext(), "Category Added Successfully!", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
        }

        // Sự kiện cho nút Cancel
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        // Điều chỉnh kích thước dialog
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    // Hàm lấy danh sách tên icon từ drawable
    private fun getIconNames(context: Context): List<String> {
        val resources = context.resources
        val iconArray = resources.getStringArray(R.array.icon_options) // Lấy mảng từ resources
        val iconNames = mutableListOf<String>()

        for (icon in iconArray) {
            // Trích xuất tên icon từ chuỗi @drawable/icon_name
            val iconName =
                icon.split("/").last().split(".").firstOrNull()?.trim()?.lowercase() ?: ""
            if (iconName.isNotEmpty()) {
                iconNames.add(iconName)
            }
        }
        return iconNames.distinct() // Loại bỏ trùng lặp nếu có
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}