package com.financeadaptative.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.financeadaptative.data.entity.Account
import com.financeadaptative.data.entity.Category
import com.financeadaptative.data.entity.Expense
import com.financeadaptative.data.entity.MovementType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class FinanceDatabaseInstrumentedTest {

    private lateinit var db: FinanceDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context,
            FinanceDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndQueryExpense() = runBlocking {
        val accountId = db.accountDao().upsert(Account(name = "Efectivo", initialBalance = 100.0))
        val categoryId = db.categoryDao().upsert(Category(name = "Alimentación"))

        val now = LocalDateTime.now()
        db.expenseDao().upsert(
            Expense(
                accountId = accountId,
                categoryId = categoryId,
                amount = 25.0,
                movementType = MovementType.EXPENSE,
                description = "Cena",
                occurredAt = now
            )
        )

        // Totales por categoría en el día
        val from = now.minusDays(1)
        val to = now.plusDays(1)
        val totals = db.expenseDao().observeTotalsByCategory(from, to).first()
        val totalForCategory = totals.first { it.categoryId == categoryId }.total

        assertEquals(-25.0, totalForCategory, 0.001)

        // Balance neto de la cuenta (solo el gasto -25)
        val net = db.expenseDao().observeNetForAccount(accountId).first() ?: 0.0
        assertEquals(-25.0, net, 0.001)
    }
}
