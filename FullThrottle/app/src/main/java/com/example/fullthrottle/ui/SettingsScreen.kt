package com.example.fullthrottle.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.ui.ThemeConstants.DARK_THEME
import com.example.fullthrottle.ui.ThemeConstants.LIGHT_THEME
import com.example.fullthrottle.ui.ThemeConstants.SYSTEM_THEME
import com.example.fullthrottle.viewModel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    Scaffold { paddingValues ->
        Column() {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                /*Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "arrow back",
                    modifier = Modifier
                        .size(35.dp)
                        .align(Alignment.Bottom),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(id = R.drawable.fullthrottle_logo_light),
                    contentDescription = "app logo",
                    modifier = Modifier,
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "arrow back",
                    modifier = Modifier
                        .size(35.dp)
                        .align(Alignment.Bottom),
                    contentScale = ContentScale.Fit,
                    alpha = 0f
                )*/
            }

            var expanded by remember {
                mutableStateOf(false)
            }

            val themesText = mapOf(
                SYSTEM_THEME to "Predefinito di sistema",
                DARK_THEME to "Scuro",
                LIGHT_THEME to "Chiaro"
            )

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

                    Spacer(modifier = Modifier.size(10.dp))

                }
            }
        }
    }
}