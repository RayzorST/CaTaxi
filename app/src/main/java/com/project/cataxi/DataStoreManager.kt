package com.project.cataxi

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yandex.mapkit.GeoObjectCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val getDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE] ?: false
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }
}

val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_history")

class SearchHistoryDataStore(private val context: Context) {
    companion object {
        private val SEARCH_HISTORY = stringSetPreferencesKey("search_history")
        private const val MAX_HISTORY_SIZE = 10
    }

    val searchHistory: Flow<List<String>> = context.searchHistoryDataStore.data
        .map { preferences ->
            preferences[SEARCH_HISTORY]?.toList() ?: emptyList()
        }

    suspend fun addSearchQuery(query: String) {
        context.searchHistoryDataStore.edit { preferences ->
            val currentHistory = preferences[SEARCH_HISTORY]?.toMutableSet() ?: mutableSetOf()

            currentHistory.remove(query)

            currentHistory.add(query)

            if (currentHistory.size > MAX_HISTORY_SIZE) {
                val oldestItem = currentHistory.first()
                currentHistory.remove(oldestItem)
            }

            preferences[SEARCH_HISTORY] = currentHistory
        }
    }

    suspend fun clearHistory() {
        context.searchHistoryDataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY)
        }
    }
}