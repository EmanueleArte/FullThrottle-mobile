package com.example.fullthrottle.ui

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.saveAndCropTempFile
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.async
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NewPostScreen(
    settingsViewModel: SettingsViewModel,
    navigateToHome: () -> Unit
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
                    TakePhoto(cropImageActivity)
                    TextButtonWithIcon(
                        text = stringResource(id = R.string.select_an_image),
                        icon = Icons.Outlined.Image,
                        "image icon",
                        modifier = Modifier,
                        onClick = {
                            photoPickerPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    )
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
                                    motorbikeId = motorbike.motorbikeId.orEmpty()
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
                place = outLineTextFieldWithIcon(
                    label =  stringResource(id = R.string.place),
                    icon = Icons.Outlined.MyLocation,
                    iconDescription = "location icon",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(20.dp))
            }
        }
    }
}