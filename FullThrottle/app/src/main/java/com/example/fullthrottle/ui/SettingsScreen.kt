package com.example.fullthrottle.ui

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.MainActivity.Companion.checkGPS
import com.example.fullthrottle.data.DBHelper
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
        SYSTEM_THEME to "Predefinito di sistema",
        DARK_THEME to "Scuro",
        LIGHT_THEME to "Chiaro"
    )

    val notificationsText = mapOf(
        ALL_NOTIFICATIONS to "Tutte",
        POSTS_NOTIFICATIONS to "Solo riguardanti i post",
        FOLLOWERS_NOTIFICATIONS to "Solo riguardanti i followers",
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Impostazioni",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Theme settings
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
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
                                        settingsViewModel.saveData("theme", entry.key)
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
                        .padding(horizontal = 20.dp)
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
                                        settingsViewModel.saveData("push_notifications", entry.key)
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
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                ) {
                    Text("Utilizza posizione attuale")

                    Switch(
                        checked = settings["location_updates"] == "true",
                        onCheckedChange = {
                            if (it) {
                                methods["startLocationUpdates"]?.invoke().let {
                                    if (checkGPS(context)) {
                                        settingsViewModel.saveData("location_updates", "true")
                                    }
                                }
                            } else {
                                methods["stopLocationUpdates"]?.invoke().let {
                                    settingsViewModel.saveData("location_updates", "false")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}