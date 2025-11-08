package com.financeadaptative.data.repository

import com.financeadaptative.data.dao.CategoryDao
import com.financeadaptative.data.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDao) {
    fun observeActive(): Flow<List<Category>> = dao.observeActive()
    suspend fun upsert(category: Category): Long = dao.upsert(category)
    suspend fun softDelete(id: Long) = dao.softDelete(id)
}
