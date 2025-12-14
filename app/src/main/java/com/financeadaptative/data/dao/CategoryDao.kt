package com.financeadaptative.data.dao

import androidx.room.*
import com.financeadaptative.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE active = 1 ORDER BY name ASC")
    fun observeActive(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: Category): Long

    @Query("UPDATE categories SET active = 0 WHERE id = :id")
    suspend fun softDelete(id: Long)

    @Query("SELECT id FROM categories WHERE name = :name LIMIT 1")
    suspend fun idByName(name: String): Long?
}
