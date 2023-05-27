package com.example.fullthrottle.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.Utils.isValidMail
import com.example.fullthrottle.Utils.isValidUsername
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.data.DBHelper.getUserByMail
import com.example.fullthrottle.data.DBHelper.getUserByUsername
import com.example.fullthrottle.data.DataStoreConstants
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.*

@Composable
fun ProfileModificationScreen(
    currentUsername: String,
    currentMail: String
) {
    //var currPw by rememberSaveable { mutableStateOf("") }

    var saveUsernameModify = remember { mutableStateOf(false) }
    var saveMailModify = remember { mutableStateOf(false) }
    var savePwModify = remember { mutableStateOf(false) }

    var openDialogUsername = remember { mutableStateOf(false) }
    var openDialogMail = remember { mutableStateOf(false) }
    var openDialogPw = remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf(-1) }
    var mailError by remember { mutableStateOf(-1) }
    var pwError by remember { mutableStateOf(-1) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        SimpleTitle(text = stringResource(id = R.string.profile_modification_title))

        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Username change
            val username = OutLineTextField(
                label = "Username",
                value = currentUsername,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(5.dp))

            SimpleButton(value = stringResource(id = R.string.save)) {
                usernameError = -1
                openDialogUsername.value = true
                var user: User? = null
                coroutineScope.async {
                    user = getUserByUsername(username)
                }.invokeOnCompletion {
                    if (user != null) {
                        usernameError = R.string.username_used
                    }
                }
            }
            if (openDialogUsername.value) {
                SimpleAlertDialog(
                    title = stringResource(id = R.string.confirm_title),
                    text = stringResource(id = R.string.modify_username_text),
                    confirm = stringResource(id = R.string.confirm),
                    dismiss = stringResource(id = R.string.dismiss),
                    openDialog = openDialogUsername,
                    result = saveUsernameModify
                )
            }
            if (saveUsernameModify.value) {
                isValidUsername(username).let { isValid ->
                    if (!isValid) {
                        usernameError = R.string.username_empty
                    }
                }
            }
            if (usernameError != -1 && saveUsernameModify.value) {
                Spacer(modifier = Modifier.size(5.dp))

                Text(text = stringResource(id = usernameError))
            }

            Spacer(modifier = Modifier.size(5.dp))

            // Mail change
            val mail = OutLineTextField(
                label = "Mail",
                value = currentMail,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(5.dp))

            SimpleButton(value = stringResource(id = R.string.save)) {
                mailError = -1
                openDialogMail.value = true
                var user: User? = null
                coroutineScope.async {
                    user = getUserByMail(mail)
                }.invokeOnCompletion {
                    if (user != null) {
                        mailError = R.string.mail_used
                    }
                }
            }
            if (openDialogMail.value) {
                SimpleAlertDialog(
                    title = stringResource(id = R.string.confirm_title),
                    text = stringResource(id = R.string.modify_mail_text),
                    confirm = stringResource(id = R.string.confirm),
                    dismiss = stringResource(id = R.string.dismiss),
                    openDialog = openDialogMail,
                    result = saveMailModify
                )
            }
            if (saveMailModify.value) {
                isValidMail(mail).let { isValid ->
                    if (!isValid) {
                        mailError = R.string.mail_regex
                    }
                }
            }
            if (mailError != -1 && saveMailModify.value) {
                Spacer(modifier = Modifier.size(5.dp))

                Text(text = stringResource(id = mailError))
            }
        }
    }
}