package com.project.cataxi.datastore

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ThemeViewModel(private val dataStore: SettingsDataStore) : ViewModel() {
    val isDarkTheme: Flow<Boolean> = dataStore.getDarkMode

    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = dataStore.getDarkMode.first()
            dataStore.setDarkMode(!currentTheme)
        }
    }
}

class SearchViewModel(private val dataStore: SearchHistoryDataStore) : ViewModel() {
    private val _searchHistory = MutableStateFlow<List<SearchHistoryDataStore.SearchHistoryItem>>(emptyList())
    val searchHistory: StateFlow<List<SearchHistoryDataStore.SearchHistoryItem>> = _searchHistory

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

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun addToHistory(query: String, point: Point? = null) {
        viewModelScope.launch {
            dataStore.addSearchQuery(query, point)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dataStore.clearHistory()
        }
    }
}

class UserViewModel(private val dataStore: UserDataStore) : ViewModel() {
    val firstName: Flow<String> = dataStore.getFirstName
    val secondName: Flow<String> = dataStore.getSecondName
    val email: Flow<String> = dataStore.getEmail
    val token: Flow<String> = dataStore.getToken

    fun setToken(token: String){
        viewModelScope.launch {
            dataStore.setToken(token)
        }
    }

    fun setFirstName(firstName: String){
        viewModelScope.launch {
            dataStore.setFirstName(firstName)
        }
    }

    fun setSecondName(secondName: String){
        viewModelScope.launch {
            dataStore.setSecondName(secondName)
        }
    }

    fun set(token: String, firstName: String, secondName: String, email: String) {
        viewModelScope.launch {
            dataStore.set(token, firstName, secondName, email)
        }
    }

    fun clear() {
        viewModelScope.launch {
            dataStore.clear()
        }
    }
}