package com.financeadaptative.data.repository

import com.financeadaptative.data.dao.AccountDao
import com.financeadaptative.data.entity.Account
import kotlinx.coroutines.flow.Flow

class AccountRepository(private val dao: AccountDao) {
    fun observeAll(): Flow<List<Account>> = dao.observeAll()
    suspend fun upsert(account: Account): Long = dao.upsert(account)
    suspend fun delete(account: Account) = dao.delete(account)
}
