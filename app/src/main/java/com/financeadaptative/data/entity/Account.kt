package com.financeadaptative.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una cuenta (efectivo, banco, tarjeta) desde donde se registran los movimientos.
 */
@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val initialBalance: Double = 0.0,
    val active: Boolean = true
)
