package com.hunglevi.expense_mdc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.model.User
import com.hunglevi.expense_mdc.data.repository.UserRepository
import com.hunglevi.expense_mdc.databinding.ActivityLoginBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.UserViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding // Declare binding for the login screen
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory(userRepository = UserRepository(AppDatabase.getInstance(applicationContext).userDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            userViewModel.insertExampleUser()
        }
        lifecycleScope.launch {
            userViewModel.insertExampleUser()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput?.text.toString()
            val password = binding.passwordInput.text.toString()

            var isValid = true

            // Validate Email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInput?.error = "Invalid email format"
                binding.emailErrorText?.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.emailInput?.error = null
                binding.emailErrorText?.visibility = View.GONE
            }

            // Validate Password
            if (password.length < 6) {
                binding.passwordInput.error = "Password must be at least 6 characters"
                binding.passwordErrorText?.visibility = View.VISIBLE
                isValid = false
            } else {
                binding.passwordInput.error = null
                binding.passwordErrorText?.visibility = View.GONE
            }

            if (isValid) {
                userViewModel.authenticateUser(email, password) // Trigger authentication

                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                // Proceed with authentication logic
            }
        }
        // Observe authentication result using StateFlow
        lifecycleScope.launch {
            userViewModel.authenticationResult.collect { user ->
                if (user != null) {
                    val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("USER_ID", user.id)
                        putString("USERNAME", user.username)
                        putString("EMAIL", user.email)
                        putString("USER_ROLE", user.role)
                        apply()
                    }
                    when (user.role) {
                        "admin" -> {
                            Toast.makeText(this@LoginActivity, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                            ) // Navigate to Admin Dashboard
                        }

                        "user" -> {
                            Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                            ) // Navigate to Main Activity
                        }

                        else -> {
                            Toast.makeText(this@LoginActivity, "Invalid role detected!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Thông tin đăng nhập không hợp lệ!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Handle signup button click
        binding.signupButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // Handle social login button clicks
        binding.facebookLoginButton.setOnClickListener {
            Toast.makeText(this, "Login with Facebook is under development!", Toast.LENGTH_SHORT).show()
        }

        binding.googleLoginButton.setOnClickListener {
            Toast.makeText(this, "Login with Google is under development!", Toast.LENGTH_SHORT).show()
        }
    }
}