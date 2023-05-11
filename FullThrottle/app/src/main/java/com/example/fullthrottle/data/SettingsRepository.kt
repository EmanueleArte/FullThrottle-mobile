package com.example.fullthrottle.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Repository that stores the user's preferences for the ui theme
 */
class SettingsRepository(private val context: Context) {

    //object declaration inside a class
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_preferences")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val USER_IMAGE = stringPreferencesKey("user_image")
    }

    val preferenceFlow: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[USERNAME]?:""
            preferences[USER_ID]?:""
            preferences[USER_IMAGE]?:""
        }

    suspend fun saveToDataStore(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }
}

