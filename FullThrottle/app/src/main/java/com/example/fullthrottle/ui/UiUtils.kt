package com.example.fullthrottle.ui

import android.app.Activity
import android.content.ClipDescription
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.icu.text.CaseMap.Title
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.fullthrottle.AppScreen
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.viewModel.WarningViewModel
import kotlinx.coroutines.async

object UiConstants {
    val CORNER_RADIUS = 10.dp
    val ANIMATION_DURATION = 100
}

object Logo {
    var logoId = R.drawable.fullthrottle_logo_light
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutLineTextField(
    label: String,
    value: String = "",
    modifier: Modifier = Modifier
): String {
    var text by rememberSaveable { mutableStateOf(value) }
    OutlinedTextField(
        shape = RoundedCornerShape(CORNER_RADIUS),
        value = text,
        label = { Text(text = label) },
        onValueChange = {
            text = it
        },
        modifier = modifier
    )
    return text
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutLinePasswordField(
    label: String,
    modifier: Modifier = Modifier
): String {
    var password by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    OutlinedTextField(
        shape = RoundedCornerShape(CORNER_RADIUS),
        value = password,
        onValueChange = { password = it },
        singleLine = true,
        label = { Text(label) },
        visualTransformation =
        if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val visibilityIcon =
                    if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                // Please provide localized description for accessibility services
                val description = if (passwordHidden) stringResource(id = R.string.show_pw) else stringResource(id = R.string.hide_pw)
                Icon(imageVector = visibilityIcon, contentDescription = description)
            }
        },
        modifier = modifier
    )
    return password
}

@Composable
fun SimpleButton(value: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(CORNER_RADIUS)
    ) {
        Text(value)
    }
}

@Composable
fun SimpleTextButton(value: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(CORNER_RADIUS)
    ) {
        Text(value)
    }
}

@Composable
fun OutlineTextButton(value: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(CORNER_RADIUS),
        modifier = Modifier.height(30.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(value)
    }
}

@Composable
fun ItemTonalButton(
    value: String,
    onClick: () -> Unit,
    imgUri: Uri
) {
    FilledTonalIconButton(
        onClick = onClick,
        shape = RoundedCornerShape(CORNER_RADIUS),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            ShowImage(imgUri = imgUri, modifier = Modifier
                .padding(horizontal = 5.dp)
                .clip(CircleShape))
            Text(value)
        }
    }
}

@Composable
fun ShowImage(
    imgUri: Uri,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
) {
    val paint = if (imgUri != Uri.EMPTY)
        rememberAsyncImagePainter(model = imgUri)
    else
        painterResource(id = R.drawable.standard)

    Image(
        painter = paint,
        contentDescription = contentDescription,
        modifier = modifier,//Modifier.size(50.dp).clip(CircleShape),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun SimpleCenterText(
    text: String,
    modifier: Modifier = Modifier.requiredWidth(100.dp)
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = TextAlign.Center
    )
}

@Composable
fun BoldCenterText(
    text: String,
    modifier: Modifier = Modifier.requiredWidth(100.dp)
) {
    Text(
        modifier = modifier,
        text = text,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SimpleTitle(text: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 20.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun SimpleAlertDialog(
    title: String,
    text: String,
    confirm: String = stringResource(id = R.string.confirm),
    dismiss: String = stringResource(id = R.string.dismiss),
    openDialog: MutableState<Boolean>,
    result: MutableState<Boolean>,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { openDialog.value = false },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            SimpleTextButton(value = confirm) {
                onConfirm()
                result.value = true
                openDialog.value = false
            }
        },
        dismissButton = {
            SimpleTextButton(value = dismiss) {
                onDismiss()
                result.value = false
                openDialog.value = false
            }
        }
    )
}

@Composable
fun SimpleSnackBarComposable(
    snackbarHostState: SnackbarHostState,
    warningViewModel: WarningViewModel,
    message: String
) {
    LaunchedEffect(snackbarHostState) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {}
            SnackbarResult.Dismissed -> {
                warningViewModel.setSimpleSnackBarVisibility(false)
            }
        }
    }
}