package com.example.fullthrottle.ui

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.MainActivity.Companion.checkGPS
import com.example.fullthrottle.R
import com.example.fullthrottle.Utils.deleteMemorizedUserData
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.data.DataStoreConstants.LOCATION_UPDATES_KEY
import com.example.fullthrottle.data.DataStoreConstants.PUSH_NOTIFICATIONS_KEY
import com.example.fullthrottle.data.DataStoreConstants.THEME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.PushNotificationConstants.ALL_NOTIFICATIONS
import com.example.fullthrottle.data.PushNotificationConstants.FOLLOWERS_NOTIFICATIONS
import com.example.fullthrottle.data.PushNotificationConstants.POSTS_NOTIFICATIONS
import com.example.fullthrottle.data.ThemeConstants.DARK_THEME
import com.example.fullthrottle.data.ThemeConstants.LIGHT_THEME
import com.example.fullthrottle.data.ThemeConstants.SYSTEM_THEME
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.values

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    //startLocationUpdates: () -> Unit
    methods: Map<String, () -> Unit>
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())

    val themesText = mapOf(
        SYSTEM_THEME to stringResource(id = R.string.system_theme),
        DARK_THEME to stringResource(id = R.string.dark_theme),
        LIGHT_THEME to stringResource(id = R.string.light_theme)
    )

    val notificationsText = mapOf(
        ALL_NOTIFICATIONS to stringResource(id = R.string.all_notifications),
        POSTS_NOTIFICATIONS to stringResource(id = R.string.posts_notifications),
        FOLLOWERS_NOTIFICATIONS to stringResource(id = R.string.followers_notifications)
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        SimpleTitle(text = stringResource(id = R.string.settings_title))

        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Theme settings
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Tema")

                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    var expanded by remember {
                        mutableStateOf(false)
                    }

                    fun expand() {
                        expanded = true
                    }

                    SimpleTextButton(
                        value = themesText[settings["theme"]].toString(),
                        onClick = { expand() }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        themesText.forEach { entry ->
                            DropdownMenuItem(
                                onClick = {
                                    /*Toast.makeText(contextForToast, itemValue, Toast.LENGTH_SHORT)
                                        .show()*/
                                    expanded = false
                                    settingsViewModel.saveData(THEME_KEY, entry.key)
                                },
                                text = {
                                    Text(text = entry.value)
                                }
                            )
                        }
                    }
                }
            }

            // Notifications
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Notifiche push")

                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    var expanded by remember {
                        mutableStateOf(false)
                    }

                    fun expand() {
                        expanded = true
                    }

                    SimpleTextButton(
                        value = notificationsText[settings["push_notifications"]].toString(),
                        onClick = { expand() }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        notificationsText.forEach { entry ->
                            DropdownMenuItem(
                                onClick = {
                                    /*Toast.makeText(contextForToast, itemValue, Toast.LENGTH_SHORT)
                                        .show()*/
                                    expanded = false
                                    settingsViewModel.saveData(PUSH_NOTIFICATIONS_KEY, entry.key)
                                },
                                text = {
                                    Text(text = entry.value)
                                }
                            )
                        }
                    }
                }
            }

            // Gps
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Utilizza posizione attuale")

                Switch(
                    checked = settings["location_updates"] == "true",
                    onCheckedChange = {
                        if (it) {
                            methods["startLocationUpdates"]?.invoke().let {
                                //if (checkGPS(context)) {
                                settingsViewModel.saveData(LOCATION_UPDATES_KEY, "true")
                                //}
                            }
                        } else {
                            methods["stopLocationUpdates"]?.invoke().let {
                                settingsViewModel.saveData(LOCATION_UPDATES_KEY, "false")
                                methods["requestingLocationUpdatesFalse"]?.invoke()
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            // Logout
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
                OutlineTextButton(
                    value = "Logout",
                    onClick = {
                        deleteMemorizedUserData(settingsViewModel)
                    }
                )
            }
        }
    }
}