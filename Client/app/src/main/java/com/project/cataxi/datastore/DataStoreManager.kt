package com.project.cataxi.datastore

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yandex.mapkit.geometry.Point
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
        private val COORDINATES_HISTORY = stringSetPreferencesKey("coordinates_history")
        private const val MAX_HISTORY_SIZE = 10
        private const val COORDINATES_DELIMITER = ":"
    }

    data class SearchHistoryItem(
        val query: String,
        val point: Point?
    )

    val searchHistory: Flow<List<SearchHistoryItem>> = context.searchHistoryDataStore.data
        .map { preferences ->
            val queries = preferences[SEARCH_HISTORY]?.toList() ?: emptyList()
            val coordinates = preferences[COORDINATES_HISTORY]?.toList() ?: emptyList()

            queries.mapIndexed { index, query ->
                val point = coordinates.getOrNull(index)?.let { coordString ->
                    val parts = coordString.split(COORDINATES_DELIMITER)
                    if (parts.size == 2) {
                        Point(parts[0].toDouble(), parts[1].toDouble())
                    } else {
                        null
                    }
                }
                SearchHistoryItem(query, point)
            }
        }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    suspend fun addSearchQuery(query: String, point: Point? = null) {
        context.searchHistoryDataStore.edit { preferences ->
            val currentQueries = preferences[SEARCH_HISTORY]?.toMutableList() ?: mutableListOf()
            val currentCoords = preferences[COORDINATES_HISTORY]?.toMutableList() ?: mutableListOf()

            val existingIndex = currentQueries.indexOf(query)
            if (existingIndex != -1) {
                currentQueries.removeAt(existingIndex)
                if (currentCoords.size > existingIndex) {
                    currentCoords.removeAt(existingIndex)
                }
            }

            currentQueries.add(0, query)
            currentCoords.add(0, point?.let { "${it.latitude}$COORDINATES_DELIMITER${it.longitude}" } ?: "")

            if (currentQueries.size > MAX_HISTORY_SIZE) {
                currentQueries.removeLast()
                if (currentCoords.size > MAX_HISTORY_SIZE) {
                    currentCoords.removeLast()
                }
            }

            preferences[SEARCH_HISTORY] = currentQueries.toSet()
            preferences[COORDINATES_HISTORY] = currentCoords.toSet()
        }
    }

    suspend fun clearHistory() {
        context.searchHistoryDataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY)
            preferences.remove(COORDINATES_HISTORY)
        }
    }
}

val Context.userDataStore by preferencesDataStore(name = "user")

class UserDataStore(private val context: Context) {
    companion object {
        val FIRST_NAME = stringPreferencesKey("first_name")
        val SECOND_NAME = stringPreferencesKey("second_name")
        val TOKEN = stringPreferencesKey("token")
        val EMAIL = stringPreferencesKey("email")
    }

    val getToken: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN]?.toString() ?: ""
        }

    val getFirstName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[FIRST_NAME]?.toString() ?: ""
        }

    val getSecondName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SECOND_NAME]?.toString() ?: ""
        }

    val getEmail: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[EMAIL]?.toString() ?: ""
        }

    suspend fun set(token: String, firstName: String, secondName: String, email: String){
        context.dataStore.edit { preferences ->
            preferences[TOKEN] = token
            preferences[FIRST_NAME] = firstName
            preferences[SECOND_NAME] = secondName
            preferences[EMAIL] = email
        }
    }

    suspend fun setToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun setFirstName(firstName: String) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_NAME] = firstName
        }
    }

    suspend fun setSecondName(secondName: String) {
        context.dataStore.edit { preferences ->
            preferences[SECOND_NAME] = secondName
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences[TOKEN] = ""
            preferences[FIRST_NAME] = ""
            preferences[SECOND_NAME] = ""
        }
    }
}