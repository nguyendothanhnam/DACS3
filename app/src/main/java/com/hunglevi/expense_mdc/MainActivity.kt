package com.hunglevi.expense_mdc

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.repository.TransactionRepository
import com.hunglevi.expense_mdc.databinding.ActivityMainBinding
import com.hunglevi.expense_mdc.presentation.screen.AnalysisFragment
import com.hunglevi.expense_mdc.presentation.screen.CategoryFragment
import com.hunglevi.expense_mdc.presentation.screen.HomeFragment
import com.hunglevi.expense_mdc.presentation.screen.SettingsFragment
import com.hunglevi.expense_mdc.presentation.screen.TransactionFragment
import com.hunglevi.expense_mdc.presentation.viewmodel.TransactionViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlin.getValue

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment and toolbar title
        if (savedInstanceState == null) {
            navigateToFragment(HomeFragment(), "Trang chủ")
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }

        // Bottom Navigation Listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (!isCurrentFragment(HomeFragment::class.java)) {
                        navigateToFragment(HomeFragment(), "Trang chủ")
                    }
                    true
                }

                R.id.nav_transactions -> {
                    if (!isCurrentFragment(TransactionFragment::class.java)) {
                        navigateToFragment(TransactionFragment(), "Giao dịch")
                    }
                    true
                }

                R.id.nav_analytics -> { // Add navigation for Analysis
                    navigateToFragment(AnalysisFragment(), "Phân tích")
                    true
                }

                R.id.nav_settings -> {
                    if (!isCurrentFragment(SettingsFragment::class.java)) {
                        navigateToFragment(SettingsFragment(), "Cài đặt")
                    }
                    true
                }

                R.id.nav_category -> {
                    if (!isCurrentFragment(CategoryFragment::class.java)) {
                        navigateToFragment(CategoryFragment(), "Danh mục")
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment, title: String) {
        // Change toolbar title
        binding.toolbar.title = title

        // Replace fragment with animation
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, // Enter animation
                R.anim.slide_out_left, // Exit animation
                R.anim.slide_in_left,  // Pop enter animation
                R.anim.slide_out_right // Pop exit animation
            )
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null) // Enable back navigation
            .commit()

    }

    private fun isCurrentFragment(fragmentClass: Class<out Fragment>): Boolean {
        // Get the currently visible fragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        return currentFragment?.javaClass == fragmentClass
    }
}