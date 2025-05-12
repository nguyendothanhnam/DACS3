package com.hunglevi.expense_mdc.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
        setupButtons()
    }

    private fun setupCalendar() {
        // Handle calendar interactions
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth / ${month + 1} / $year"
            Toast.makeText(requireContext(), "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtons() {
        binding.expenditureButton.setOnClickListener {
            // Handle "Chi Tiêu" button click
            Toast.makeText(requireContext(), "Expenditure clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.typeButton.setOnClickListener {
            // Handle "Loại" button click
            Toast.makeText(requireContext(), "Type clicked!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}