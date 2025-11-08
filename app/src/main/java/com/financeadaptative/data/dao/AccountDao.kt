package com.financeadaptative.data.dao

import androidx.room.*
import com.financeadaptative.data.entity.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY name ASC")
    fun observeAll(): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getById(id: Long): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(account: Account): Long

    @Delete
    suspend fun delete(account: Account)

    @Query("SELECT SUM(initialBalance) FROM accounts WHERE active = 1")
    fun observeTotalInitialBalance(): Flow<Double?>
}
