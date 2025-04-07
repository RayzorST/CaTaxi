package com.project.cataxi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ThemeViewModelFactory(
    private val DataStore: SettingsDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(DataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SearchHistoryViewModelFactory(
    private val DataStore: SearchHistoryDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(DataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}