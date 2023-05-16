package com.example.fullthrottle.data

import androidx.datastore.preferences.core.stringPreferencesKey

object ThemeConstants {
    val SYSTEM_THEME = "system"
    val DARK_THEME = "dark"
    val LIGHT_THEME = "light"
}

object PushNotificationConstants {
    val ALL_NOTIFICATIONS = "all"
    val POSTS_NOTIFICATIONS = "posts_only"
    val FOLLOWERS_NOTIFICATIONS = "followers_only"
}

object DataStoreConstants {
    val USER_ID_KEY = "user_id"
    val USERNAME_KEY = "username"
    val USER_IMAGE_KEY = "user_image"
    val THEME_KEY = "theme"
    val PUSH_NOTIFICATIONS_KEY = "push_notifications"
    val LOCATION_UPDATES_KEY = "location_updates"
}