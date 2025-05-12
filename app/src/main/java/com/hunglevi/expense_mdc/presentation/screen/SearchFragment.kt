package com.hunglevi.expense_mdc.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

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
            // Example: Show available options
            Toast.makeText(requireContext(), "Dropdown clicked!", Toast.LENGTH_SHORT).show()
        }

        // Handle date selection
        binding.dateInput.setOnClickListener {
            // Example: Show a date picker dialog
            Toast.makeText(requireContext(), "Date picker clicked!", Toast.LENGTH_SHORT).show()
        }

        // Handle report type selection
        binding.radioIncome.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Income selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.radioExpense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Expense selected", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle search button click
        binding.searchButton.setOnClickListener {
            val selectedType = binding.typeDropdown.text.toString()
            val selectedDate = binding.dateInput.text.toString()
            val reportType = when {
                binding.radioIncome.isChecked -> "Income"
                binding.radioExpense.isChecked -> "Expense"
                else -> "None"
            }

            // Trigger search functionality
            Toast.makeText(requireContext(),
                "Searching for $reportType reports on $selectedDate with type $selectedType",
                Toast.LENGTH_SHORT
            ).show()

            // TODO: Add your search logic here
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}