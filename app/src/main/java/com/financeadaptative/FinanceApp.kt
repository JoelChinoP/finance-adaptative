package com.financeadaptative

import android.app.Application
import com.financeadaptative.data.FinanceDatabase
import com.financeadaptative.data.repository.AccountRepository
import com.financeadaptative.data.repository.CategoryRepository
import com.financeadaptative.data.repository.ExpenseRepository

class FinanceApp : Application() {
    lateinit var db: FinanceDatabase
        private set

    // Service locator muy simple para el proyecto
    lateinit var repositories: AppRepositories
        private set

    override fun onCreate() {
        super.onCreate()
        db = FinanceDatabase.getInstance(this)
        repositories = AppRepositories(
            accounts = AccountRepository(db.accountDao()),
            categories = CategoryRepository(db.categoryDao()),
            expenses = ExpenseRepository(db.expenseDao())
        )
    }
}

data class AppRepositories(
    val accounts: AccountRepository,
    val categories: CategoryRepository,
    val expenses: ExpenseRepository
)
