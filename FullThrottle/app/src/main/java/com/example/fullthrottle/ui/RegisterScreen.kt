package com.example.fullthrottle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.Utils.isValidMail
import com.example.fullthrottle.Utils.isValidPassword
import com.example.fullthrottle.Utils.isValidUsername
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.ui.Logo.logoId
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.async
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    settingsViewModel: SettingsViewModel,
    navigateTo: Map<String, () ->Unit>
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = logoId),
            contentDescription = "app logo",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.size(40.dp))

        val username = OutLineTextField(label = "Username")

        Spacer(modifier = Modifier.size(10.dp))

        val mail = OutLineTextField(label = "Mail")

        Spacer(modifier = Modifier.size(10.dp))

        val password = OutLinePasswordField(label = "Password")

        Spacer(modifier = Modifier.size(10.dp))

        val passwordRep = OutLinePasswordField(label = stringResource(id = R.string.password_repetition))

        Spacer(modifier = Modifier.size(10.dp))

        var registrationError by rememberSaveable { mutableStateOf(-1) }

        val coroutineScope = rememberCoroutineScope()

        SimpleButton(
            value = stringResource(id = R.string.register),
            onClick = {
                isValidPassword(password).let { isValid ->
                    registrationError = -1
                    if (password != passwordRep) {
                        registrationError = R.string.passwords_not_matching
                    }
                    if (!isValid) {
                        registrationError = R.string.password_invalid
                    }
                }
                isValidMail(mail).let { isValid ->
                    if (!isValid) {
                        registrationError = R.string.mail_regex
                    }
                }
                isValidUsername(username).let { isValid ->
                    if (!isValid) {
                        registrationError = R.string.username_empty
                    }
                }
                if (registrationError == -1) {
                    coroutineScope.async {
                        registrationError = DBHelper.userRegistration(username, mail, password, settingsViewModel)
                    }.invokeOnCompletion {
                        if (registrationError == -1) {
                            navigateTo["home"]?.invoke()
                        }
                    }
                }
            }
        )

        if (registrationError != -1) {
            Spacer(modifier = Modifier.size(5.dp))

            Text(text = stringResource(id = registrationError))
        }

        Spacer(modifier = Modifier.size(5.dp))

        Text(
            text = stringResource(id = R.string.password_regex),
            textAlign = TextAlign.Center
        )
    }
}