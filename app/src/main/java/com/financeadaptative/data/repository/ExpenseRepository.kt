package com.financeadaptative.data.repository

import com.financeadaptative.data.dao.ExpenseDao
import com.financeadaptative.data.dao.CategoryTotal
import com.financeadaptative.data.entity.Expense
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class ExpenseRepository(private val dao: ExpenseDao) {
    suspend fun upsert(expense: Expense): Long = dao.upsert(expense)
    suspend fun delete(expense: Expense) = dao.delete(expense)
    fun observeByDateRange(from: LocalDateTime, to: LocalDateTime): Flow<List<Expense>> = dao.observeByDateRange(from, to)
    fun observeTotalsByCategory(from: LocalDateTime, to: LocalDateTime): Flow<List<CategoryTotal>> = dao.observeTotalsByCategory(from, to)
    fun observeNetForAccount(accountId: Long): Flow<Double?> = dao.observeNetForAccount(accountId)
}
