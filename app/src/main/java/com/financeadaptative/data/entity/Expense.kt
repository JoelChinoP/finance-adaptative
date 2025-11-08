package com.financeadaptative.data.entity

import androidx.room.*
import java.time.LocalDateTime

/**
 * Registro de un movimiento (gasto o ingreso) asociado a cuenta y categoría.
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("accountId"), Index("categoryId")]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val categoryId: Long?,
    val amount: Double,
    val movementType: MovementType,
    val description: String? = null,
    val occurredAt: LocalDateTime = LocalDateTime.now()
)
