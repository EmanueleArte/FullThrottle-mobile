package com.example.fullthrottle.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.fullthrottle.data.PushNotificationConstants.ALL_NOTIFICATIONS
import com.example.fullthrottle.data.ThemeConstants.SYSTEM_THEME

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
        private val THEME = stringPreferencesKey("theme")
        private val PUSH_NOTIFICATIONS = stringPreferencesKey("push_notifications")
        private val LOCATION_UPDATES = stringPreferencesKey("location_updates")
    }

    val preferenceFlow: Flow<Map<String, String>> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val prefMap = mutableMapOf<String, String>()
            prefMap[USERNAME.toString()] = preferences[USERNAME]?: ""
            prefMap[USER_ID.toString()] = preferences[USER_ID]?: 0.toString()
            prefMap[USER_IMAGE.toString()] = preferences[USER_IMAGE]?: ""
            prefMap[THEME.toString()] = preferences[THEME]?: SYSTEM_THEME
            prefMap[PUSH_NOTIFICATIONS.toString()] = preferences[PUSH_NOTIFICATIONS]?: ALL_NOTIFICATIONS
            prefMap[LOCATION_UPDATES.toString()] = preferences[LOCATION_UPDATES]?: "false"
            prefMap
        }

    suspend fun saveToDataStore(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}

