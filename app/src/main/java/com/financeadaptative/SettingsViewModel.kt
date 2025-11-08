package com.financeadaptative

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.financeadaptative.data.Settings
import com.financeadaptative.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SettingsRepository(app)

    private val _settings = MutableStateFlow(Settings())
    val settings: StateFlow<Settings> = _settings.asStateFlow()

    init {
        repo.settingsFlow
            .onEach { _settings.value = it }
            .launchIn(viewModelScope)
    }

    fun setCurrency(symbol: String) = viewModelScope.launch { repo.setCurrency(symbol) }
    fun setDefaultCategory(category: String) = viewModelScope.launch { repo.setDefaultCategory(category) }
    fun setDefaultPayment(method: String) = viewModelScope.launch { repo.setDefaultPayment(method) }
    fun setShowTips(show: Boolean) = viewModelScope.launch { repo.setShowTips(show) }
}
