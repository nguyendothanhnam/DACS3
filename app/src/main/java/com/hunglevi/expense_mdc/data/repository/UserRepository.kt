package com.hunglevi.expense_mdc.data.repository

import com.hunglevi.expense_mdc.data.dao.UserDao
import com.hunglevi.expense_mdc.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
    suspend fun authenticateUser(email: String, password: String): User? {
        return userDao.authenticateUser(email, password)
    }
    suspend fun insertExampleUser() {
        val exampleUser = User(
            id = 1,
            username = "exampleUser",
            email = "ad@ad.com",
            password = "123456",
            role = "admin",
            createdAt = System.currentTimeMillis().toString(),
            profileImage = ""
        )
        userDao.insertUser(exampleUser)
    }

}