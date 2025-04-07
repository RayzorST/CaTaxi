package com.project.cataxi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ThemeViewModel(private val SettingsDataStore: SettingsDataStore) : ViewModel() {
    val isDarkTheme: Flow<Boolean> = SettingsDataStore.getDarkMode

    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = SettingsDataStore.getDarkMode.first()
            SettingsDataStore.setDarkMode(!currentTheme)
        }
    }
}

class SearchViewModel(private val dataStore: SearchHistoryDataStore) : ViewModel() {
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            dataStore.searchHistory
                .collect { history ->
                    _searchHistory.value = history.reversed()
                }
        }
    }

    fun addToHistory(query: String) {
        viewModelScope.launch {
            dataStore.addSearchQuery(query)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dataStore.clearHistory()
        }
    }
}