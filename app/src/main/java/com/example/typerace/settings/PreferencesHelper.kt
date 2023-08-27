package com.example.typerace.settings

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)
class PreferencesHelper(context: Context) {

    private val dataSource = context.dataStore

    suspend fun <T> getPreference(
        key: Preferences.Key<T>,
        defaultValue: T
    ): Flow<T> = dataSource.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val result = preferences[key] ?: defaultValue
        result
    }

    suspend fun <T> getFirstPreference(
        key: Preferences.Key<T>,
        defaultValue: T
    ) : T = dataSource.data.first()[key] ?: defaultValue

    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        dataSource.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> removePreference(key: Preferences.Key<T>) {
        dataSource.edit { preferences ->
            preferences.remove(key)
        }
    }

    suspend fun <T> clearAllPreferences() {
        dataSource.edit { preferences ->
            preferences.clear()
        }
    }
}