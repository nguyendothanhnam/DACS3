package com.hunglevi.expense_mdc.data.dao
import androidx.room.*
import androidx.room.OnConflictStrategy
import com.hunglevi.expense_mdc.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories where userId = :userId ORDER BY name ASC")
    fun getAllCategoriesByUserId(userId: Int): Flow<List<Category>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?

    @Query("SELECT * FROM categories WHERE name LIKE '%' || :searchTerm || '%'")
    fun searchCategoriesByName(searchTerm: String): Flow<List<Category>>
}