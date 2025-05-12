package com.hunglevi.expense_mdc.presentation.screen

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.adapter.FeedbackAdapter
import com.hunglevi.expense_mdc.adapter.UserAdapter
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.model.Feedback
import com.hunglevi.expense_mdc.data.model.User
import com.hunglevi.expense_mdc.data.repository.FeedbackRepository
import com.hunglevi.expense_mdc.data.repository.UserRepository
import com.hunglevi.expense_mdc.databinding.FragmentFeedbackBinding
import com.hunglevi.expense_mdc.databinding.FragmentUserBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.FeedbackViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.UserViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private lateinit var userAdapter: UserAdapter
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory(userRepository = UserRepository(AppDatabase.getInstance(requireContext()).userDao()))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = context?.getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref?.getInt("USER_ID", -1)
        userViewModel.setUserId(userId ?: -1)

        // Initialize the adapter
        val userAdapter = UserAdapter(
            users = emptyList(), // Start with an empty list
            onItemClick = { user ->
                println("Clicked on user: ${user.username}")
            },
            onEditClick = { user ->
                openEditUserDialog(user)
            },
            onDeleteClick = { user ->
                showDeleteConfirmationDialog(user)
            }
        )

        // Set up RecyclerView
        binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.userRecyclerView.adapter = userAdapter

        // Observe user list and update the adapter dynamically
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.users.collect { userList ->
                userAdapter.updateUsers(userList) // Call updateUsers to refresh the list
            }
        }
    }

    private fun openEditUserDialog(user: User) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_user, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit User")
            .setPositiveButton(null, null) // Null to prevent automatic closing
            .create()

        val usernameInput = dialogView.findViewById<EditText>(R.id.usernameInput)
        val emailInput = dialogView.findViewById<EditText>(R.id.emailInput)
        val roleInput = dialogView.findViewById<EditText>(R.id.roleInput)

        // Pre-fill fields with current user data
        usernameInput.setText(user.username)
        emailInput.setText(user.email)
        roleInput.setText(user.role)



        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save") { _, _ ->
            val editedUsername = usernameInput.text.toString()
            val editedEmail = emailInput.text.toString()

            if (editedUsername.isNotBlank() && editedEmail.isNotBlank()) {
                val updatedUser = user.copy(
                    username = editedUsername,
                    email = editedEmail
                )
                userViewModel.updateUser(updatedUser) // Update via ViewModel
                Toast.makeText(requireContext(), "User updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please fill out all fields!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete ${user.username}?")
            .setPositiveButton("Yes") { _, _ ->
                userViewModel.deleteUser(user) // Delete via ViewModel
                Toast.makeText(requireContext(), "User deleted successfully!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun getUserId(): Int {
        // Retrieve user ID from SharedPreferences or other sources
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE)
        return sharedPref.getInt("USER_ID", -1)
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }
}