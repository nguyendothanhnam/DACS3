package com.hunglevi.expense_mdc.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.hunglevi.expense_mdc.data.model.Category
import com.hunglevi.expense_mdc.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {
    private val userId = MutableStateFlow<Int?>(null)
    fun setUserId(id: Int) {
        userId.value = id
    }
    val categoryMap: StateFlow<Map<Int, String>> = repository.getCategoriesByUserId(userId.value ?: 0)
        .map { categories -> categories.associate { it.id to it.name } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    val categories: Flow<List<Category>> = userId.filterNotNull().flatMapLatest { id ->
        repository.getCategoriesByUserId(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    suspend fun getCategoryById(id: Int): Category? {
        return repository.getCategoryById(id)
    }
//    fun getCategoriesByUserId(userId: Int): Flow<List<Category>> {
//        return repository.getCategoriesByUserId(userId)
//    }
    fun getAllCategories(): Flow<List<Category>> {
        return repository.allCategories
    }
    fun insertCategory(category: Category) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
    fun insertExampleCategory(){
        viewModelScope.launch {
            repository.insertExampleCategory()
        }
    }
}