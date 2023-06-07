package com.example.fullthrottle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
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
import com.example.fullthrottle.ValidityUtils.isValidMail
import com.example.fullthrottle.ValidityUtils.isValidPassword
import com.example.fullthrottle.ValidityUtils.isValidUsername
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.ui.Logo.logoId
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.async

@Composable
fun RegisterScreen(
    settingsViewModel: SettingsViewModel,
    navigateTo: Map<String, () ->Unit>
) {
    val fieldModifier = Modifier
        .padding(horizontal = MAIN_H_PADDING)
        .fillMaxWidth()

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

        val username = outLineTextField(label = "Username", modifier = fieldModifier)

        Spacer(modifier = Modifier.size(10.dp))

        val mail = outLineTextField(label = "Mail", modifier = fieldModifier)

        Spacer(modifier = Modifier.size(10.dp))

        val password = outLinePasswordField(label = "Password", modifier = fieldModifier)

        Spacer(modifier = Modifier.size(10.dp))

        val passwordRep = outLinePasswordField(label = stringResource(id = R.string.password_repetition), modifier = fieldModifier)

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