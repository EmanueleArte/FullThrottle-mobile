package com.example.fullthrottle.data

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.fullthrottle.R

object ThemeConstants {
    const val SYSTEM_THEME = "system"
    const val DARK_THEME = "dark"
    const val LIGHT_THEME = "light"
}

object PushNotificationConstants {
    const val ALL_NOTIFICATIONS = "all"
    const val POSTS_NOTIFICATIONS = "posts_only"
    const val FOLLOWERS_NOTIFICATIONS = "followers_only"
    const val NO_NOTIFICATIONS = "none"
}

object DataStoreConstants {
    const val USER_ID_KEY = "user_id"
    const val USERNAME_KEY = "username"
    const val USER_IMAGE_KEY = "user_image"
    const val MAIL_KEY = "mail"
    const val THEME_KEY = "theme"
    const val PUSH_NOTIFICATIONS_KEY = "push_notifications"
    const val LOCATION_UPDATES_KEY = "location_updates"
}

object TabConstants {
    const val FOLLOWERS_TAB = 0
    const val FOLLOWED_TAB = 1
}

object LogosIds {
    var LOGO_LIGHT = R.drawable.fullthrottle_logo_light
    var LOGO_DARK = R.drawable.fullthrottle_logo_dark
}

object HomeValues {
    private var lifecycleOwner: LifecycleOwner? = null
    private val listenFilterValue: MutableLiveData<Int> = MutableLiveData(R.string.all_posts)

    fun setLifeCycleOwner(value: LifecycleOwner) {
        lifecycleOwner = value
    }
    fun registerFilterValueListener(action: (Int) -> Unit) {
        listenFilterValue.observe(lifecycleOwner as LifecycleOwner) {
            action(it)
        }
    }

    fun getFilterValueListener() = listenFilterValue
}