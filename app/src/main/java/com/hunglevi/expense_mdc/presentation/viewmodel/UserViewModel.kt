package com.hunglevi.expense_mdc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.hunglevi.expense_mdc.data.model.User
import com.hunglevi.expense_mdc.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    val users: Flow<List<User>> = repository.allUsers
    private val userId = MutableStateFlow<Int?>(null)
    fun setUserId(id: Int) {
        userId.value = id
    }

    // Use StateFlow for authentication result
    private val _authenticationResult = MutableStateFlow<User?>(null)
    val authenticationResult: StateFlow<User?> get() = _authenticationResult
    private val _registrationResult = MutableStateFlow(false)
    val registrationResult: StateFlow<Boolean> get() = _registrationResult

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            val user = User(
                username = username,
                email = email,
                password = password, // Consider hashing passwords in real applications
                role = "user", // Default role
                createdAt = System.currentTimeMillis().toString(),
                profileImage = "" // Example timestamp
            )
            try {
                repository.insertUser(user)
                _registrationResult.value = true
            } catch (e: Exception) {
                _registrationResult.value = false
            }
        }
    }

    fun authenticateUser(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.authenticateUser(email, password)
            _authenticationResult.value = user // Update StateFlow with the result
        }
    }


    fun insertUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }
    fun insertExampleUser() {
        viewModelScope.launch {
            repository.insertExampleUser()
        }
    }
    suspend fun getUserById(id: Int): User? {
        return repository.getUserById(id)
    }
    suspend fun getUserByUsername(username : String): User? {
        return repository.getUserByUsername(username)
    }

}