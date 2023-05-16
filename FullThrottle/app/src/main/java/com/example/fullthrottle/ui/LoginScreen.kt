package com.example.fullthrottle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.userLogin
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Unconfined

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    settingsViewModel: SettingsViewModel,
    navigateToHomeScreen: () ->Unit
    ) {
    val context = LocalContext.current
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(paddingValues)
                .padding(50.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.fullthrottle_logo_light),
                contentDescription = "app logo",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.size(40.dp))

            val username = OutLineTextField(label = "Username")

            Spacer(modifier = Modifier.size(10.dp))

            val password = OutLinePasswordField(label = "Password")

            Spacer(modifier = Modifier.size(10.dp))

            var loginError by rememberSaveable { mutableStateOf(false) }
            var loginClick by rememberSaveable { mutableStateOf(false) }

            val coroutineScope = rememberCoroutineScope()

            SimpleButton(
                value = "Login",
                onClick = {
                    coroutineScope.async {
                        loginError = !userLogin(username, password, settingsViewModel)
                        loginClick = true
                    }.invokeOnCompletion {
                        if (!loginError && loginClick) {
                            navigateToHomeScreen()
                        }
                    }
                }
            )

            if (loginError && loginClick) {
                Spacer(modifier = Modifier.size(5.dp))

                Text(
                    text = stringResource(id = R.string.login_error),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.size(5.dp))

            Text(
                text = stringResource(id = R.string.not_registered_yet),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.size(5.dp))

            SimpleButton(
                value = stringResource(id = R.string.register),
                onClick = { /*TODO*/ }
            )

        }
    }
}