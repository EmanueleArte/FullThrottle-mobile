package com.example.fullthrottle.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.fullthrottle.data.DataStoreConstants.LOCATION_UPDATES_KEY
import com.example.fullthrottle.data.DataStoreConstants.MAIL_KEY
import com.example.fullthrottle.data.DataStoreConstants.PUSH_NOTIFICATIONS_KEY
import com.example.fullthrottle.data.DataStoreConstants.THEME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
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
        private val USER_ID = stringPreferencesKey(USER_ID_KEY)
        private val USERNAME = stringPreferencesKey(USERNAME_KEY)
        private val USER_IMAGE = stringPreferencesKey(USER_IMAGE_KEY)
        private val MAIL = stringPreferencesKey(MAIL_KEY)
        private val THEME = stringPreferencesKey(THEME_KEY)
        private val PUSH_NOTIFICATIONS = stringPreferencesKey(PUSH_NOTIFICATIONS_KEY)
        private val LOCATION_UPDATES = stringPreferencesKey(LOCATION_UPDATES_KEY)
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
            prefMap[USERNAME_KEY] = preferences[USERNAME]?: ""
            prefMap[USER_ID_KEY] = preferences[USER_ID]?: ""
            prefMap[USER_IMAGE_KEY] = preferences[USER_IMAGE]?: ""
            prefMap[MAIL_KEY] = preferences[MAIL]?: ""
            prefMap[THEME_KEY] = preferences[THEME]?: SYSTEM_THEME
            prefMap[PUSH_NOTIFICATIONS_KEY] = preferences[PUSH_NOTIFICATIONS]?: ALL_NOTIFICATIONS
            prefMap[LOCATION_UPDATES_KEY] = preferences[LOCATION_UPDATES]?: "false"
            prefMap
        }

    suspend fun saveToDataStore(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}

