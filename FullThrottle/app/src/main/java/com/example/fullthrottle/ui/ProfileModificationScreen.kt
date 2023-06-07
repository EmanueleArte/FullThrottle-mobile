package com.example.fullthrottle.ui

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fullthrottle.R
import com.example.fullthrottle.ValidityUtils.isValidMail
import com.example.fullthrottle.ValidityUtils.isValidPassword
import com.example.fullthrottle.ValidityUtils.isValidUsername
import com.example.fullthrottle.createPermissionRequest
import com.example.fullthrottle.data.DBHelper.addMotorbike
import com.example.fullthrottle.data.DBHelper.changeProfileImage
import com.example.fullthrottle.data.DBHelper.checkPassword
import com.example.fullthrottle.data.DBHelper.deleteMotorbikeById
import com.example.fullthrottle.data.DBHelper.getMotorbikesByUserId
import com.example.fullthrottle.data.DBHelper.getUserByMail
import com.example.fullthrottle.data.DBHelper.getUserByUsername
import com.example.fullthrottle.data.DBHelper.updateMail
import com.example.fullthrottle.data.DBHelper.updatePassword
import com.example.fullthrottle.data.DBHelper.updateUsername
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.saveAndCropTempFile
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var motorbikes by remember { mutableStateOf(emptyList<Motorbike>()) }

    LaunchedEffect(key1 = "motorbikes") {
        motorbikes = getMotorbikesByUserId(uid)
    }

    val profileImageSuccess = stringResource(id = R.string.modify_profile_image_success)
    val usernameSuccess = stringResource(id = R.string.modify_username_success)
    val mailSuccess = stringResource(id = R.string.modify_mail_success)
    val pwSuccess = stringResource(id = R.string.modify_password_success)
    val motorbikeAdded = stringResource(id = R.string.motorbike_added)
    val motoInvalid = stringResource(id = R.string.all_moto_fields)
    val motorbikeDeleted = stringResource(id = R.string.motorbike_deleted)
    val permissionDeniedLabel = stringResource(id = R.string.permission_denied)

    val saveUsernameModify = remember { mutableStateOf(false) }
    val saveMailModify = remember { mutableStateOf(false) }
    val savePwModify = remember { mutableStateOf(false) }
    val saveMotorbikeAdd = remember { mutableStateOf(false) }
    val saveMotorbikeDelete = remember { mutableStateOf(false) }
    val saveImage = remember { mutableStateOf(false) }

    val profileImageUri = rememberSaveable { mutableStateOf<Uri?>(Uri.EMPTY) }

    val openDialogUsername = remember { mutableStateOf(false) }
    val openDialogMail = remember { mutableStateOf(false) }
    val openDialogPw = remember { mutableStateOf(false) }
    val openDialogMotorbikeAdd = remember { mutableStateOf(false) }
    val openDialogMotorbikeDelete = remember { mutableStateOf(false) }
    val openDialogImage = remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf(-1) }
    var mailError by remember { mutableStateOf(-1) }
    var pwError by remember { mutableStateOf(-1) }

    val cropImageActivity = cropImageActivityBuilder(profileImageUri, 1F, 1F)

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            saveAndCropTempFile(cropImageActivity, uri)
        }
    )

    val photoPickerPermission = createPermissionRequest(
        onSuccess = {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onDismiss = {
            Toast.makeText(context, permissionDeniedLabel, Toast.LENGTH_SHORT).show()
        }
    )

    if (profileImageUri.value != Uri.EMPTY) {
        openDialogImage.value = true
    }

    if (openDialogImage.value) {
        ImageAlertDialog(
            title = stringResource(id = R.string.confirm_title),
            body = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(id = R.string.modify_image_text))

                    Spacer(modifier = Modifier.size(15.dp))

                    ProfileImage(
                        imgUri = profileImageUri.value as Uri,
                        modifier = Modifier
                            .requiredHeight(250.dp)
                            .requiredWidth(250.dp)
                            .clip(CircleShape)
                    )
                }
            },
            openDialog = openDialogImage,
            result = saveImage,
            onConfirm = {
                changeProfileImage(uid, profileImageUri.value as Uri)
                profileImageUri.value = Uri.EMPTY
                showSnackBar(warningViewModel, profileImageSuccess)
            },
            onDismiss = {
                profileImageUri.value = Uri.EMPTY
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = MAIN_H_PADDING)
            .fillMaxWidth()
    ) {
        item {
            SimpleTitle(text = stringResource(id = R.string.profile_modification_title))
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Profile image change
                Text(
                    text = stringResource(id = R.string.profile_image_change),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.size(5.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TakePhoto(cropImageActivity = cropImageActivity, modifier = Modifier.weight(0.49F))

                    Spacer(modifier = Modifier.weight(0.02F))

                    ButtonWithIcon(
                        text = stringResource(id = R.string.select_an_image),
                        icon = Icons.Outlined.Image,
                        iconDescription = "image icon",
                        modifier = Modifier.weight(0.49F)
                    ) {
                        photoPickerPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }

                Spacer(modifier = Modifier.size(15.dp))

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
                                    showSnackBar(warningViewModel, usernameSuccess)
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
                                    showSnackBar(warningViewModel, mailSuccess)
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
                                showSnackBar(warningViewModel, pwSuccess)
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
            }
        }
        item {
            if (motorbikes.isNotEmpty()) {
                SimpleTitle(text = stringResource(id = R.string.my_motorbikes))

                motorbikes.forEach { motorbike ->
                    if (!motorbike.deleted!!) {
                        Spacer(modifier = Modifier.size(5.dp))

                        Card {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${motorbike.brand} ${motorbike.model} ${motorbike.productionYear}",
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = "delete motorbike",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(CORNER_RADIUS))
                                        .clickable {
                                            openDialogMotorbikeDelete.value = true
                                        }
                                )
                                if (openDialogMotorbikeDelete.value) {
                                    SimpleAlertDialog(
                                        title = stringResource(id = R.string.confirm_title),
                                        text = stringResource(id = R.string.delete_motorbike_text),
                                        openDialog = openDialogMotorbikeDelete,
                                        result = saveMotorbikeDelete,
                                        onConfirm = {
                                            coroutineScope.launch {
                                                deleteMotorbikeById(motorbike.motorbikeId.orEmpty())
                                                motorbikes = getMotorbikesByUserId(uid)
                                            }
                                            showSnackBar(warningViewModel, motorbikeDeleted)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.size(5.dp))
                    }
                }
            }
        }

        item {
            SimpleButton(value = stringResource(id = R.string.add_motorbike)) {
                openDialogMotorbikeAdd.value = true
            }
            if (openDialogMotorbikeAdd.value) {
                val brand = remember { mutableStateOf(String()) }
                val model = remember { mutableStateOf(String()) }
                val productionYear = remember { mutableStateOf(String()) }
                val fieldNames = listOf(
                    stringResource(id = R.string.brand),
                    stringResource(id = R.string.model),
                    stringResource(id = R.string.production_year)
                )
                FieldsAlertDialog(
                    title = stringResource(id = R.string.add_motorbike),
                    text = "",
                    fields = mapOf(
                        fieldNames[0] to brand,
                        fieldNames[1] to model,
                        fieldNames[2] to productionYear
                    ),
                    openDialog = openDialogMotorbikeAdd,
                    result = saveMotorbikeAdd,
                    confirm = stringResource(id = R.string.add),
                    dismiss = stringResource(id = R.string.cancel),
                    onConfirm = {
                        val isValid = brand.value.isNotEmpty() && model.value.isNotEmpty() && productionYear.value.isNotEmpty()
                        if (isValid) {
                            coroutineScope.launch {
                                addMotorbike(uid, brand.value, model.value, productionYear.value)
                                motorbikes = getMotorbikesByUserId(uid)
                            }
                            showSnackBar(warningViewModel, motorbikeAdded)
                        } else {
                            showSnackBar(warningViewModel, motoInvalid)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.size(15.dp))
        }
    }
}