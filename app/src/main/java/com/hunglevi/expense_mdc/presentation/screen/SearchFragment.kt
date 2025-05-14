package com.hunglevi.expense_mdc.presentation.screen

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.repository.CategoryRepository
import com.hunglevi.expense_mdc.databinding.FragmentSearchBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.CategoryViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var selectedType: String = "All"
    private var selectedDate: String = "30 / APR / 2023"

    // Chia sẻ cùng instance của CategoryViewModel với CategoryFragment
    private val categoryViewModel: CategoryViewModel by activityViewModels {
        ViewModelFactory(
            categoryRepository = CategoryRepository(
                AppDatabase.getInstance(requireActivity()).categoryDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchUI()
    }

    private fun setupSearchUI() {
        // Handle dropdown menu interaction
        binding.typeDropdown.setOnClickListener {
            showTypeDropdown()
        }

        // Handle date selection
        binding.dateInput.setOnClickListener {
            showDatePicker()
        }

        // Handle report type selection
//        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.radioIncome -> {
//                    selectedType = "Income"
//                    Toast.makeText(requireContext(), "Selected: Income", Toast.LENGTH_SHORT).show()
//                }
//                R.id.radioExpense -> {
//                    selectedType = "Expense"
//                    Toast.makeText(requireContext(), "Selected: Expense", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        // Handle search button click
        binding.searchButton.setOnClickListener {
            val searchText = binding.searchBar.text.toString().trim()
            performSearch(searchText, selectedType, selectedDate)
        }
    }

    // Hiển thị dropdown cho type, lấy dữ liệu từ CategoryViewModel
    private fun showTypeDropdown() {
        // Lấy danh sách category từ ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            categoryViewModel.categories.collect { categoryList ->
                val categoryNames = categoryList.map { it.name } // Lấy tên các category
                val options = listOf("All") + categoryNames // Thêm tùy chọn "All"

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Select Type")
                    .setItems(options.toTypedArray()) { _, which ->
                        selectedType = options[which]
                        binding.typeDropdown.text = selectedType
                        Toast.makeText(requireContext(), "Selected Type: $selectedType", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    // Hiển thị DatePicker
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = SimpleDateFormat("dd / MMM / yyyy", Locale.ENGLISH).format(
                    Calendar.getInstance().apply {
                        set(selectedYear, selectedMonth, selectedDay)
                    }.time
                )
                selectedDate = formattedDate
                binding.dateInput.text = formattedDate
                Toast.makeText(requireContext(), "Selected Date: $formattedDate", Toast.LENGTH_SHORT).show()
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    // Thực hiện tìm kiếm
    private fun performSearch(searchText: String, type: String, date: String) {
        if (searchText.isNotBlank()) {
            Toast.makeText(
                requireContext(),
                "Searching: Text='$searchText', Type='$type', Date='$date'",
                Toast.LENGTH_SHORT
            ).show()
            // TODO: Add your search logic here (e.g., call ViewModel, query database)
        } else {
            Toast.makeText(requireContext(), "Please enter a search term!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}