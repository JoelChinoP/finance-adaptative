package com.financeadaptative.data.dao

import androidx.room.*
import com.financeadaptative.data.entity.Expense
import com.financeadaptative.data.entity.MovementType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

data class CategoryTotal(
    val categoryId: Long?,
    val total: Double
)

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: Expense): Long

    @Delete
    suspend fun delete(expense: Expense)

    @Query(
        "SELECT * FROM expenses WHERE occurredAt BETWEEN :from AND :to ORDER BY occurredAt DESC"
    )
    fun observeByDateRange(from: LocalDateTime, to: LocalDateTime): Flow<List<Expense>>

    @Query(
        "SELECT categoryId AS categoryId, SUM(CASE WHEN movementType = 'INCOME' THEN amount ELSE -amount END) AS total " +
        "FROM expenses WHERE occurredAt BETWEEN :from AND :to GROUP BY categoryId"
    )
    fun observeTotalsByCategory(from: LocalDateTime, to: LocalDateTime): Flow<List<CategoryTotal>>

    @Query(
        "SELECT SUM(CASE WHEN movementType = 'INCOME' THEN amount ELSE -amount END) FROM expenses WHERE accountId = :accountId"
    )
    fun observeNetForAccount(accountId: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM expenses")
    suspend fun count(): Int
}
