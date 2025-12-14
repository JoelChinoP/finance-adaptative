package com.financeadaptative.data.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.financeadaptative.FinanceApp
import com.financeadaptative.data.entity.Category
import com.financeadaptative.data.entity.Expense
import com.financeadaptative.data.entity.MovementType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

data class UiTransaction(
    val id: Long,
    val title: String?,
    val amount: Double,
    val isIncome: Boolean,
    val categoryName: String?,
    val date: LocalDateTime,
    val note: String?
)

class ExpenseViewModel(app: Application) : AndroidViewModel(app) {
    private val repos = (app as FinanceApp).repositories
    private val accountsDao = repos.accounts
    private val categoriesRepo = repos.categories
    private val expensesRepo = repos.expenses

    private val _refreshTrigger = MutableStateFlow(0)

    // Todas las categorías activas en cache
    private val categoriesFlow = categoriesRepo.observeActive().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Flujo de todos los expenses (sin filtro ahora)
    private val expensesFlow = expensesRepo.observeAll().combine(_refreshTrigger) { list, _ -> list }

    val transactions: StateFlow<List<UiTransaction>> = expensesFlow.combine(categoriesFlow) { expenses, categories ->
        val mapCat = categories.associateBy { it.id }
        expenses.map { e ->
            UiTransaction(
                id = e.id,
                title = e.description, // reutilizamos description como título si no hay campo título real
                amount = e.amount,
                isIncome = e.movementType == MovementType.INCOME,
                categoryName = e.categoryId?.let { mapCat[it]?.name },
                date = e.occurredAt,
                note = e.description
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Balance combinado: (initial balances activos) + neto de la primera cuenta (simple)
    val balance: StateFlow<Double> = expensesRepo.observeAll().map { list ->
        list.fold(0.0) { acc, e ->
            acc + if (e.movementType == MovementType.INCOME) e.amount else -e.amount
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    /** Inserta un movimiento. Crea categoría si no existe. Usa cuenta default (crea una si no hay). */
    fun addMovement(title: String, signedAmount: Double, categoryName: String, dateStr: String, note: String?) {
        viewModelScope.launch {
            // Asegurar cuenta
            val accountId = ensureDefaultAccount()

            // Asegurar categoría
            val catId = ensureCategory(categoryName)

            val isIncome = signedAmount > 0
            val movementType = if (isIncome) MovementType.INCOME else MovementType.EXPENSE
            val occurredAt = parseDate(dateStr)

            expensesRepo.upsert(
                Expense(
                    accountId = accountId,
                    categoryId = catId,
                    amount = kotlin.math.abs(signedAmount),
                    movementType = movementType,
                    description = title.ifBlank { note },
                    occurredAt = occurredAt
                )
            )
            _refreshTrigger.update { it + 1 }
        }
    }

    /** Edita metadatos sin tocar el monto ni el tipo. */
    fun editMovementMeta(id: Long, newTitle: String?, newCategoryName: String?, newDateStr: String?, newNote: String?) {
        viewModelScope.launch {
            val existing = transactions.value.firstOrNull { it.id == id } ?: return@launch
            // Buscar Expense original vía listado (ineficiencia aceptable por simplicidad)
            val accountId = ensureDefaultAccount()
            val catId = newCategoryName?.let { ensureCategory(it) } ?: categoriesFlow.value.firstOrNull { it.name == existing.categoryName }?.id
            val occurredAt = newDateStr?.let { parseDate(it) } ?: existing.date
            // Recuperar amount y movementType desde transacción UI
            val movementType = if (existing.isIncome) MovementType.INCOME else MovementType.EXPENSE
            // Upsert conservando monto
            expensesRepo.upsert(
                Expense(
                    id = id,
                    accountId = accountId,
                    categoryId = catId,
                    amount = kotlin.math.abs(existing.amount),
                    movementType = movementType,
                    description = newTitle ?: existing.title,
                    occurredAt = occurredAt
                )
            )
            _refreshTrigger.update { it + 1 }
        }
    }

    private suspend fun ensureDefaultAccount(): Long {
        val dao = (getApplication<FinanceApp>()).db.accountDao()
        val existingId = dao.firstId()
        if (existingId != null) return existingId
        return dao.upsert(com.financeadaptative.data.entity.Account(name = "Principal"))
    }

    private suspend fun ensureCategory(name: String): Long? {
        if (name.isBlank()) return null
        val dao = (getApplication<FinanceApp>()).db.categoryDao()
        val existing = dao.idByName(name)
        if (existing != null) return existing
        return dao.upsert(Category(name = name))
    }

    private fun parseDate(dateStr: String): LocalDateTime {
        return try {
            LocalDate.parse(dateStr).atStartOfDay()
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}
