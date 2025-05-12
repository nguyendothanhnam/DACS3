package com.hunglevi.expense_mdc.presentation.screen

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.model.User
import com.hunglevi.expense_mdc.data.repository.UserRepository
import com.hunglevi.expense_mdc.databinding.FragmentEditProfileBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.UserViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.getValue

class EditSettingsFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory(userRepository = UserRepository(AppDatabase.getInstance(requireContext()).userDao()))
    }

    private val PICK_IMAGE_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = context?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref?.getInt("USERID", -1) ?: -1
        val username = sharedPref?.getString("USERNAME", null)
        val email = sharedPref?.getString("EMAIL", null)
        val userImage = sharedPref?.getString("PROFILE_IMAGE_URI", null)
        binding.usernameInput.setText(username)
        binding.emailInput.setText(email)
        // Load profile image
        loadProfileImage()


        // Handle Profile Image Click
        binding.profileImage.setOnClickListener {
            openImagePicker()
        }
        // Handle Update Button Click
        binding.updateButton.setOnClickListener {
            val newUsername = binding.usernameInput.text.toString()
            val newEmail = binding.emailInput.text.toString()

            if (newUsername.isNotBlank() && newEmail.isNotBlank()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val username = sharedPref?.getString("USERNAME", null)
                    val user = userViewModel.getUserByUsername(username.toString())
                    if (user != null) {
                        val updatedUser = user.copy(
                            username = newUsername,
                            email = newEmail
                        )
                        userViewModel.updateUser(updatedUser)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "User not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                sharedPref?.edit()
                    ?.putString("USERNAME", newUsername)
                    ?.putString("EMAIL", newEmail)
                    ?.apply()

                Toast.makeText(
                    requireContext(),
                    "Profile updated: $newUsername, $newEmail",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill out all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data // Get the URI of the selected image
            binding.profileImage.setImageURI(imageUri) // Update the profile image view
            saveProfileImageUri(imageUri) // Save the selected image URI
        }
    }

    private fun saveProfileImageUri(imageUri: Uri?) {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("PROFILE_IMAGE_URI", imageUri.toString()).apply()
    }

    private fun loadProfileImage() {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val imageUriString = sharedPref.getString("PROFILE_IMAGE_URI", null)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.profile) // Add placeholder image
                .into(binding.profileImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}