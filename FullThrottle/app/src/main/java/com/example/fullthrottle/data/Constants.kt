package com.example.fullthrottle.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fullthrottle.R

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
    val MAIL_KEY = "mail"
    val THEME_KEY = "theme"
    val PUSH_NOTIFICATIONS_KEY = "push_notifications"
    val LOCATION_UPDATES_KEY = "location_updates"
}

object TabConstants {
    val FOLLOWERS_TAB = 0
    val FOLLOWED_TAB = 1
}

object LogosIds {
    val LOGO_LIGHT = R.drawable.fullthrottle_logo_light
    val LOGO_DARK = R.drawable.fullthrottle_logo_dark
}