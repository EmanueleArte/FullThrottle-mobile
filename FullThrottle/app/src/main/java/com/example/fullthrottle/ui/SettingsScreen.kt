package com.example.fullthrottle.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.ValidityUtils
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.data.DataStoreConstants.LOCATION_UPDATES_KEY
import com.example.fullthrottle.data.DataStoreConstants.PUSH_NOTIFICATIONS_KEY
import com.example.fullthrottle.data.DataStoreConstants.THEME_KEY
import com.example.fullthrottle.data.PushNotificationConstants.ALL_NOTIFICATIONS
import com.example.fullthrottle.data.PushNotificationConstants.FOLLOWERS_NOTIFICATIONS
import com.example.fullthrottle.data.PushNotificationConstants.NO_NOTIFICATIONS
import com.example.fullthrottle.data.PushNotificationConstants.POSTS_NOTIFICATIONS
import com.example.fullthrottle.data.ThemeConstants.DARK_THEME
import com.example.fullthrottle.data.ThemeConstants.LIGHT_THEME
import com.example.fullthrottle.data.ThemeConstants.SYSTEM_THEME
import com.example.fullthrottle.deleteMemorizedUserData
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    methods: Map<String, () -> Unit>
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())

    val themesText = mapOf(
        SYSTEM_THEME to stringResource(id = R.string.system_theme),
        DARK_THEME to stringResource(id = R.string.dark_theme),
        LIGHT_THEME to stringResource(id = R.string.light_theme)
    )

    val notificationsText = mapOf(
        ALL_NOTIFICATIONS to stringResource(id = R.string.all_notifications),
        POSTS_NOTIFICATIONS to stringResource(id = R.string.posts_notifications),
        FOLLOWERS_NOTIFICATIONS to stringResource(id = R.string.followers_notifications),
        NO_NOTIFICATIONS to stringResource(id = R.string.no_notifications)
    )

    Column(
        modifier = Modifier
            .padding(horizontal = MAIN_H_PADDING)
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
                Text( stringResource(id = R.string.theme))

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
                Text( stringResource(id = R.string.push_notification))

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
                Text( stringResource(id = R.string.use_current_position))

                Switch(
                    checked = settings["location_updates"] == "true",
                    onCheckedChange = {
                        if (it) {
                            settingsViewModel.saveData(LOCATION_UPDATES_KEY, "true")
                        } else {
                            settingsViewModel.saveData(LOCATION_UPDATES_KEY, "false")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            // Logout
            val openDialogLogout = rememberSaveable { mutableStateOf(false) }
            val logoutConfirm = rememberSaveable { mutableStateOf(false) }

            if (openDialogLogout.value) {
                logoutConfirm.value = false
                SimpleAlertDialog(
                    title = stringResource(id = R.string.confirm_logout),
                    text = "",
                    openDialog = openDialogLogout,
                    result = logoutConfirm,
                    onConfirm = {
                        deleteMemorizedUserData(settingsViewModel)
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlineTextButton(
                    value = "Logout",
                    modifier = Modifier.height(30.dp)
                ) {
                    openDialogLogout.value = true
                }
            }
        }
    }
}