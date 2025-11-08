package com.financeadaptative.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Categoría de gastos/ingresos (Ej: Alimentación, Salud, Nómina).
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String? = null,
    val active: Boolean = true
)
