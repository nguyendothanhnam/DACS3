package com.hunglevi.expense_mdc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hunglevi.expense_mdc.LoginActivity
import com.hunglevi.expense_mdc.data.dao.AppDatabase
import com.hunglevi.expense_mdc.data.repository.UserRepository
import com.hunglevi.expense_mdc.databinding.ActivityRegisterBinding
import com.hunglevi.expense_mdc.presentation.viewmodel.UserViewModel
import com.hunglevi.expense_mdc.presentation.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory(userRepository = UserRepository(AppDatabase.getInstance(applicationContext).userDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register Button Logic
        binding.registerButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            // Validate Input
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Register User via ViewModel
            if(userViewModel.registerUser(username, email, password) == null) {
                Toast.makeText(this, "Đăng ký không thành công!", Toast.LENGTH_SHORT).show()
            } else {
                userViewModel.authenticateUser(email, password) // Trigger authentication
            }

        }

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
                            Toast.makeText(this@RegisterActivity, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
                            startActivity(
                                Intent(
                                    this@RegisterActivity,
                                    MainActivity::class.java
                                )
                            ) // Navigate to Admin Dashboard
                        }

                        "user" -> {
                            Toast.makeText(this@RegisterActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                            startActivity(
                                Intent(
                                    this@RegisterActivity,
                                    MainActivity::class.java
                                )
                            ) // Navigate to Main Activity
                        }

                        else -> {
                            Toast.makeText(this@RegisterActivity, "Invalid role detected!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Thông tin đăng nhập không hợp lệ!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe Registration Result
        lifecycleScope.launch {
            userViewModel.registrationResult.collect { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Đăng ký không thành công!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Redirect to Login
        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Apply reverse animations
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    // Utility Method: Email Validation
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}