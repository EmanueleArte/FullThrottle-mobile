package com.example.fullthrottle.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fullthrottle.R
import com.example.fullthrottle.ValidityUtils.isValidFieldText
import com.example.fullthrottle.createPermissionRequest
import com.example.fullthrottle.data.DBHelper.createPost
import com.example.fullthrottle.data.DBHelper.getMotorbikesByUserId
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.LocationDetails
import com.example.fullthrottle.data.PushNotificationValues.localDbViewModel
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.saveAndCropTempFile
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.example.fullthrottle.viewModel.WarningViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun NewPostScreen(
    settingsViewModel: SettingsViewModel,
    warningViewModel: WarningViewModel,
    navigateToHome: () -> Unit,
    navigateToSettings: () -> Unit,
    location: MutableState<LocationDetails>
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var motorbikes by rememberSaveable { mutableStateOf(listOf<Motorbike>()) }

    LaunchedEffect(Unit) {
        motorbikes = getMotorbikesByUserId(settings[USER_ID_KEY].orEmpty())
    }

    val permissionDeniedLabel = stringResource(id = R.string.permission_denied)
    val fillAllFieldsError = stringResource(id = R.string.fill_all_fields_error)

    val selectedImageUri = rememberSaveable { mutableStateOf<Uri?>(Uri.EMPTY) }

    val cropImageActivity = cropImageActivityBuilder(selectedImageUri)

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

    lateinit var title: String
    lateinit var description: String
    lateinit var length: String
    lateinit var place: String
    var motorbikeId = String()

    Column (
        modifier = Modifier
            .padding(horizontal = MAIN_H_PADDING)
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                ) {
                    SimpleTitle(text = stringResource(id = R.string.new_post))
                    Spacer(Modifier.weight(1f))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        SimpleTextButton(value = stringResource(id = R.string.publish), fontSize = 18.sp)
                        {
                            if (isValidFieldText(title)
                                && isValidFieldText(description)
                                && isValidFieldText(length)
                                && isValidFieldText(place)
                                && isValidFieldText(motorbikeId)
                                && selectedImageUri.value != Uri.EMPTY
                            ) {
                                coroutineScope.async {
                                    createPost(
                                        settings[USER_ID_KEY].orEmpty(),
                                        title,
                                        description,
                                        selectedImageUri.value as Uri,
                                        motorbikeId,
                                        place,
                                        length
                                    )
                                    GlobalScope.launch(Dispatchers.IO) {
                                        localDbViewModel.addNewMotorbike(motorbikes.first { it.motorbikeId == motorbikeId })
                                    }
                                }.invokeOnCompletion {
                                    navigateToHome()
                                }
                            } else {
                                Toast.makeText(context, fillAllFieldsError, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            // Image Selection
            item {
                PreviewImage(
                    imgUri = if(selectedImageUri.value != null) selectedImageUri.value as Uri else Uri.EMPTY,
                    contentDescription = "selected image",
                    modifier = Modifier
                        .clip(RoundedCornerShape(CORNER_RADIUS))
                        .fillMaxWidth()
                )
            }

            item {
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
                        photoPickerPermission.launch(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                Manifest.permission.READ_MEDIA_IMAGES
                            else
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
            }

            // Post fields
            item {
                title = outLineTextField(label =  stringResource(id = R.string.title), modifier = Modifier.fillMaxWidth())
            }
            item {
                description = outLineTextField(label =  stringResource(id = R.string.description), modifier = Modifier.fillMaxWidth())
            }
            item {
                Text(text = stringResource(id = R.string.motorbike) + ":", fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.size(5.dp))

                val motorbikeLabel = stringResource(id = R.string.chose_motorbike)
                var motorbikeName by rememberSaveable { mutableStateOf(motorbikeLabel) }
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    var expanded by remember {
                        mutableStateOf(false)
                    }

                    fun expand() {
                        expanded = true
                    }

                    OutlinedButton(
                        shape = RoundedCornerShape(CORNER_RADIUS),
                        modifier = Modifier
                            .height(55.dp)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        onClick = { expand() }
                    ) {
                        Text(motorbikeName, textAlign = TextAlign.Left)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        modifier = Modifier.fillParentMaxWidth(),
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        motorbikes.forEach { motorbike ->
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    motorbikeName = "${motorbike.brand} ${motorbike.model} ${motorbike.productionYear}"
                                    motorbikeId = motorbike.motorbikeId
                                },
                                text = {
                                    Text(text = "${motorbike.brand} ${motorbike.model} ${motorbike.productionYear}")
                                }
                            )
                        }
                    }
                }
            }
            item {
                length = outLineNumberTextField(
                    label =  stringResource(id = R.string.path_length_input),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                place = locationPicker(
                    label =  stringResource(id = R.string.place),
                    warningViewModel = warningViewModel,
                    modifier = Modifier.fillMaxWidth(),
                    location = location,
                    settings = settings,
                    action = navigateToSettings
                )
                Spacer(modifier = Modifier.size(20.dp))
            }
        }
    }
}