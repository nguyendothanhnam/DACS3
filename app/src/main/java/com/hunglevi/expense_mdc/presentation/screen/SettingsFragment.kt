package com.hunglevi.expense_mdc.presentation.screen

import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hunglevi.expense_mdc.LoginActivity
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.databinding.ActivityMainBinding
import com.hunglevi.expense_mdc.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val mainBiding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewBinding
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = context?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref?.getInt("USER_ID", -1)
        val username = sharedPref?.getString("USERNAME", null)
        val userRole = sharedPref?.getString("USER_ROLE", null)

        // Example: Set profile image using Glide
        val profileImageUrl = "https://upload.wikimedia.org/wikipedia/vi/4/48/%E1%BA%A2nh_b%C3%ACa_Minecraft.png"
        Glide.with(requireContext())
            .load(profileImageUrl)
            .placeholder(R.drawable.profile) // Default placeholder
            .into(binding.profileImage)

        // Set up click listeners for menu options
        binding.userManage.setOnClickListener {
        if(userRole == "admin") {
            val userFragment = UserFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,  // Enter animation
                    R.anim.slide_out_left,  // Exit animation
                    R.anim.slide_in_left,   // Pop enter animation (when back button is pressed)
                    R.anim.slide_out_right  // Pop exit animation (when back button is pressed)
                )
                .replace(R.id.fragmentContainer, userFragment)
                .addToBackStack(null) // Add fragment to back stack for navigation
                .commit()
        } else {
            Toast.makeText(context, "You do not have permission to access this feature.", Toast.LENGTH_SHORT).show()
        }}

        binding.editPersonalInfo.setOnClickListener {
            val editPersonalInfoFragment = EditSettingsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,  // Enter animation
                    R.anim.slide_out_left,  // Exit animation
                    R.anim.slide_in_left,   // Pop enter animation (when back button is pressed)
                    R.anim.slide_out_right  // Pop exit animation (when back button is pressed)
                )
                .replace(R.id.fragmentContainer, editPersonalInfoFragment)
                .addToBackStack(null) // Add fragment to back stack for navigation
                .commit()
        }
        binding.feedbackEvent.setOnClickListener {
            val feedbackFragment = FeedbackFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,  // Enter animation
                    R.anim.slide_out_left,  // Exit animation
                    R.anim.slide_in_left,   // Pop enter animation (when back button is pressed)
                    R.anim.slide_out_right  // Pop exit animation (when back button is pressed)
                )
                .replace(R.id.fragmentContainer, feedbackFragment)
                .addToBackStack(null) // Add fragment to back stack for navigation
                .commit()
        }
        binding.logoutEvent.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to log out?")
               .setPositiveButton("Yes") { _, _ ->
                    // Perform logout
                    Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        // Example: Dynamically update profile info
        binding.userName.text = username
        binding.userId.text = userId.toString()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}