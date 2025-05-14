package com.hunglevi.expense_mdc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hunglevi.expense_mdc.data.model.User
import com.hunglevi.expense_mdc.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    // Tìm kiếm
    private val _searchQuery = MutableStateFlow("")
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Kết hợp danh sách người dùng với từ khóa tìm kiếm
    val users: StateFlow<List<User>> = _searchQuery
        .combine(repository.allUsers) { query, users ->
            if (query.isBlank()) users
            else users.filter {
                it.username.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val userId = MutableStateFlow<Int?>(null)
    fun setUserId(id: Int) {
        userId.value = id
    }

    private val _authenticationResult = MutableStateFlow<User?>(null)
    val authenticationResult: StateFlow<User?> get() = _authenticationResult

    private val _registrationResult = MutableStateFlow(false)
    val registrationResult: StateFlow<Boolean> get() = _registrationResult

    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            val user = User(
                username = username,
                email = email,
                password = password,
                role = "user",
                createdAt = System.currentTimeMillis().toString(),
                profileImage = ""
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
            _authenticationResult.value = user
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

    suspend fun getUserByUsername(username: String): User? {
        return repository.getUserByUsername(username)
    }
}
