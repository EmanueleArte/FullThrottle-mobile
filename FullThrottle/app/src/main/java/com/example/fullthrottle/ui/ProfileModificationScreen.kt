package com.example.fullthrottle.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.Utils.isValidMail
import com.example.fullthrottle.Utils.isValidPassword
import com.example.fullthrottle.Utils.isValidUsername
import com.example.fullthrottle.data.DBHelper.checkPassword
import com.example.fullthrottle.data.DBHelper.getUserByMail
import com.example.fullthrottle.data.DBHelper.getUserByUsername
import com.example.fullthrottle.data.DBHelper.updateMail
import com.example.fullthrottle.data.DBHelper.updatePassword
import com.example.fullthrottle.data.DBHelper.updateUsername
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.example.fullthrottle.viewModel.WarningViewModel
import kotlinx.coroutines.*

@Composable
fun ProfileModificationScreen(
    uid: String,
    currentUsername: String,
    currentMail: String,
    settingsViewModel: SettingsViewModel,
    warningViewModel: WarningViewModel
) {
    fun showSnackBar(message: String) {
        warningViewModel.setSimpleSnackBarContent(message)
        warningViewModel.setSimpleSnackBarVisibility(true)
    }

    val usernameSuccess = stringResource(id = R.string.modify_username_success)
    val mailSuccess = stringResource(id = R.string.modify_mail_success)
    val pwSuccess = stringResource(id = R.string.modify_password_success)

    val saveUsernameModify = remember { mutableStateOf(false) }
    val saveMailModify = remember { mutableStateOf(false) }
    val savePwModify = remember { mutableStateOf(false) }

    val openDialogUsername = remember { mutableStateOf(false) }
    val openDialogMail = remember { mutableStateOf(false) }
    val openDialogPw = remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf(-1) }
    var mailError by remember { mutableStateOf(-1) }
    var pwError by remember { mutableStateOf(-1) }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        item {
            SimpleTitle(text = stringResource(id = R.string.profile_modification_title))
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Username change
                val username = outLineTextField(
                    label = "Username",
                    value = currentUsername,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(5.dp))

                SimpleButton(value = stringResource(id = R.string.save) + " username") {
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
                    saveUsernameModify.value = false
                    SimpleAlertDialog(
                        title = stringResource(id = R.string.confirm_title),
                        text = stringResource(id = R.string.modify_username_text),
                        openDialog = openDialogUsername,
                        result = saveUsernameModify,
                        onConfirm = {
                            isValidUsername(username).let { isValid ->
                                if (!isValid) {
                                    usernameError = R.string.username_empty
                                } else if (usernameError == -1) {
                                    coroutineScope.launch {
                                        updateUsername(
                                            uid = uid,
                                            newUsername = username,
                                            settingsViewModel = settingsViewModel
                                        )
                                    }
                                    showSnackBar(usernameSuccess)
                                }
                            }
                        }
                    )
                }
                if (usernameError != -1 && saveUsernameModify.value) {
                    Spacer(modifier = Modifier.size(5.dp))

                    Text(text = stringResource(id = usernameError))
                }

                Spacer(modifier = Modifier.size(15.dp))

                // Mail change
                val mail = outLineTextField(
                    label = "Mail",
                    value = currentMail,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(5.dp))

                SimpleButton(value = stringResource(id = R.string.save) + " mail") {
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
                    saveMailModify.value = false
                    SimpleAlertDialog(
                        title = stringResource(id = R.string.confirm_title),
                        text = stringResource(id = R.string.modify_mail_text),
                        openDialog = openDialogMail,
                        result = saveMailModify,
                        onConfirm = {
                            isValidMail(mail).let { isValid ->
                                if (!isValid) {
                                    mailError = R.string.mail_regex
                                } else if (mailError == -1) {
                                    coroutineScope.launch {
                                        updateMail(
                                            uid = uid,
                                            newMail = mail,
                                            settingsViewModel = settingsViewModel
                                        )
                                    }
                                    showSnackBar(mailSuccess)
                                }
                            }
                        }
                    )
                }
                if (mailError != -1 && saveMailModify.value) {
                    Spacer(modifier = Modifier.size(5.dp))

                    Text(text = stringResource(id = mailError))
                }

                Spacer(modifier = Modifier.size(15.dp))

                // Password change
                val oldPassword = outLinePasswordField(
                    label = stringResource(id = R.string.old_password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(10.dp))

                val password = outLinePasswordField(
                    label = stringResource(id = R.string.new_password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(10.dp))

                val passwordRep = outLinePasswordField(
                    label = stringResource(id = R.string.password_repetition),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(5.dp))

                SimpleButton(value = stringResource(id = R.string.save) + " password") {
                    pwError = -1
                    openDialogPw.value = true
                    isValidPassword(password).let { isValid ->
                        var pwCorrect = false
                        if (isValid && password == passwordRep) {
                            openDialogPw.value = true
                            coroutineScope.async {
                                pwCorrect = checkPassword(uid, oldPassword)
                            }.invokeOnCompletion {
                                if (!pwCorrect) {
                                    pwError = R.string.password_incorrect
                                }
                            }
                        } else {
                            pwError = if (!isValid) {
                                R.string.password_invalid
                            } else {
                                R.string.passwords_not_matching
                            }
                        }
                    }
                }
                if (openDialogPw.value) {
                    savePwModify.value = false
                    SimpleAlertDialog(
                        title = stringResource(id = R.string.confirm_title),
                        text = stringResource(id = R.string.modify_password_text),
                        openDialog = openDialogPw,
                        result = savePwModify,
                        onConfirm = {
                            if (pwError == -1) {
                                coroutineScope.launch {
                                    updatePassword(
                                        uid = uid,
                                        newPassword = password
                                    )
                                }
                                showSnackBar(pwSuccess)
                            }
                        }
                    )
                }
                if (pwError != -1 && savePwModify.value) {
                    Spacer(modifier = Modifier.size(5.dp))

                    Text(text = stringResource(id = pwError))
                }

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = stringResource(id = R.string.password_regex),
                )

                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}