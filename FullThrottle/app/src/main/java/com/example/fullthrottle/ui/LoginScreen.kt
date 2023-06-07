package com.example.fullthrottle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.userLogin
import com.example.fullthrottle.ui.Logo.logoId
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.*

@Composable
fun LoginScreen(
    settingsViewModel: SettingsViewModel,
    navigateTo: Map<String, () ->Unit>
    )
{
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(vertical = 50.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = logoId),
            contentDescription = "app logo",
            modifier = Modifier
                .padding(horizontal = 50.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.size(40.dp))

        val username = outLineTextField(
            label = "Username",
            modifier = Modifier
                .padding(horizontal = MAIN_H_PADDING)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(10.dp))

        val password = outLinePasswordField(
            label = "Password",
            modifier = Modifier
                .padding(horizontal = MAIN_H_PADDING)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.size(10.dp))

        var loginError by rememberSaveable { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()

        SimpleButton(
            value = "Login",
            onClick = {
                coroutineScope.async {
                    loginError = !userLogin(username, password, settingsViewModel)
                }.invokeOnCompletion {
                    if (!loginError) {
                        navigateTo["home"]?.invoke()
                    }
                }
            }
        )

        if (loginError) {
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
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(5.dp))

        SimpleButton(
            value = stringResource(id = R.string.register),
            onClick = { navigateTo["registration"]?.invoke() }
        )
    }
}