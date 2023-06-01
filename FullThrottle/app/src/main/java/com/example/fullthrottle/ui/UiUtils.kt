package com.example.fullthrottle.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.annotation.IntRange
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.fullthrottle.R
import com.example.fullthrottle.ui.UiConstants.ANIMATION_DURATION_LONG
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.viewModel.WarningViewModel
import kotlinx.coroutines.delay

object UiConstants {
    val CORNER_RADIUS = 10.dp
    val ANIMATION_DURATION = 100
    val ANIMATION_DURATION_LONG = 300
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
fun OutLineTextFieldWithIcon(
    label: String,
    value: String = "",
    icon: ImageVector,
    iconDescription: String,
    modifier: Modifier = Modifier
): String {
    var text by rememberSaveable { mutableStateOf(value) }
    OutlinedTextField(
        shape = RoundedCornerShape(CORNER_RADIUS),
        value = text,
        label = { Text(text = label) },
        leadingIcon = { Icon (icon, iconDescription) },
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
fun TextButtonWithIcon(text: String, icon: ImageVector, iconDescription: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(CORNER_RADIUS),
        modifier = modifier
    ) {
        Icon(icon, iconDescription)
        Spacer(Modifier.width(10.dp))
        Text(text, Modifier)
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
            ProfileImage(imgUri = imgUri, modifier = Modifier
                .padding(horizontal = 5.dp)
                .clip(CircleShape))
            Text(value)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileImage(
    imgUri: Uri,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
) {
    var imgModel = if (imgUri == Uri.EMPTY)
        Uri.parse("android.resource://com.example.fullthrottle/" + R.drawable.standard)
    else
        imgUri

    GlideImage(
        model = imgModel,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PostImage(
    imgUri: Uri,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
) {
    GlideImage(
        model = imgUri,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    ) {
        it.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    }
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

@Composable
internal fun InfiniteTransition.fractionTransition(
    initialValue: Float,
    targetValue: Float,
    @IntRange(from = 1, to = 4) fraction: Int = 1,
    durationMillis: Int,
    delayMillis: Int = 0,
    offsetMillis: Int = 0,
    repeatMode: RepeatMode = RepeatMode.Restart,
    easing: Easing = FastOutSlowInEasing
): State<Float> {
    return animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                this.durationMillis = durationMillis
                this.delayMillis = delayMillis
                initialValue at 0 with easing
                when(fraction){
                    1 ->{
                        targetValue at durationMillis with easing
                    }
                    2 ->{
                        targetValue / fraction at durationMillis / fraction with easing
                        targetValue at durationMillis with easing
                    }
                    3 ->{
                        targetValue / fraction at durationMillis / fraction with easing
                        targetValue / fraction * 2 at durationMillis / fraction * 2 with easing
                        targetValue at durationMillis with easing
                    }
                    4 ->{
                        targetValue / fraction at durationMillis / fraction with easing
                        targetValue / fraction * 2 at durationMillis / fraction * 2 with easing
                        targetValue / fraction * 3 at durationMillis / fraction * 3 with easing
                        targetValue at durationMillis with easing
                    }
                }
            },
            repeatMode,
            StartOffset(offsetMillis)
        )
    )
}

val EaseInOut = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

@Composable
fun ThreeBounce(
    modifier: Modifier = Modifier,
    durationMillis: Int = 1400,
    delayBetweenDotsMillis: Int = 160,
    size: DpSize = DpSize(40.dp, 40.dp),
    color: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = CircleShape
) {
    val transition = rememberInfiniteTransition()

    val sizeMultiplier1 = transition.fractionTransition(
        initialValue = 0f,
        targetValue = 1f,
        fraction = 1,
        durationMillis = durationMillis / 2,
        repeatMode = RepeatMode.Reverse
    )
    val sizeMultiplier2 = transition.fractionTransition(
        initialValue = 0f,
        targetValue = 1f,
        fraction = 1,
        durationMillis = durationMillis / 2,
        offsetMillis = delayBetweenDotsMillis,
        repeatMode = RepeatMode.Reverse
    )
    val sizeMultiplier3 = transition.fractionTransition(
        initialValue = 0f,
        targetValue = 1f,
        fraction = 1,
        durationMillis = durationMillis / 2,
        offsetMillis = delayBetweenDotsMillis * 2,
        repeatMode = RepeatMode.Reverse
    )

    Row(
        modifier = modifier.size(size),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(size * 3 / 11), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(size * 3 / 11 * sizeMultiplier1.value),
                shape = shape,
                color = color
            ) {}
        }
        Spacer(modifier = Modifier.width(size.width / 1 / 11))
        Box(modifier = Modifier.size(size * 3 / 11), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(size * 3 / 11 * sizeMultiplier2.value),
                shape = shape,
                color = color
            ) {}
        }
        Spacer(modifier = Modifier.width(size.width / 1 / 11))
        Box(modifier = Modifier.size(size * 3 / 11), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(size * 3 / 11 * sizeMultiplier3.value),
                shape = shape,
                color = color
            ) {}
        }
    }
}

@Composable
fun LoadingAnimation(durationMillis: Long = -1L) {
    var active by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = active,
        enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(ANIMATION_DURATION_LONG)),
        exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(ANIMATION_DURATION_LONG))
    ) {
        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ThreeBounce()
        }
        if (durationMillis != -1L) {
            LaunchedEffect(Unit) {
                delay(durationMillis)
                active = false
            }
        }
    }
}