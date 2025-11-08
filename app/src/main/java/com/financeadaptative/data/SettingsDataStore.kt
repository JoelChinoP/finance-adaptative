package com.financeadaptative.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "settings"

val Context.settingsDataStore by preferencesDataStore(name = DATASTORE_NAME)

data class Settings(
    val currencySymbol: String = "S/.",
    val defaultCategory: String = "General",
    val defaultPaymentMethod: String = "Efectivo",
    val showTips: Boolean = true
)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val currency = stringPreferencesKey("currency_symbol")
        val category = stringPreferencesKey("default_category")
        val payMethod = stringPreferencesKey("default_payment_method")
        val showTips = booleanPreferencesKey("show_tips")
    }

    val settingsFlow: Flow<Settings> = context.settingsDataStore.data.map { prefs ->
        Settings(
            currencySymbol = prefs[Keys.currency] ?: "S/.",
            defaultCategory = prefs[Keys.category] ?: "General",
            defaultPaymentMethod = prefs[Keys.payMethod] ?: "Efectivo",
            showTips = prefs[Keys.showTips] ?: true
        )
    }

    suspend fun setCurrency(symbol: String) {
        context.settingsDataStore.edit { it[Keys.currency] = symbol }
    }

    suspend fun setDefaultCategory(category: String) {
        context.settingsDataStore.edit { it[Keys.category] = category }
    }

    suspend fun setDefaultPayment(method: String) {
        context.settingsDataStore.edit { it[Keys.payMethod] = method }
    }

    suspend fun setShowTips(show: Boolean) {
        context.settingsDataStore.edit { it[Keys.showTips] = show }
    }
}
