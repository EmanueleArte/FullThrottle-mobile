package com.example.fullthrottle

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern

object ValidityUtils {
    private const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$"

    fun isValidPassword(password: String): Boolean {
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun isValidMail(mail: String): Boolean {
        return !TextUtils.isEmpty(mail) && android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
    }

    fun isValidUsername(username: String): Boolean {
        return !TextUtils.isEmpty(username)
    }

    fun isValidFieldText(text: String): Boolean {
        return !TextUtils.isEmpty(text)
    }

}

fun deleteMemorizedUserData(settingsViewModel: SettingsViewModel) {
    settingsViewModel.saveData(USER_ID_KEY, "")
    settingsViewModel.saveData(USERNAME_KEY, "")
    settingsViewModel.saveData(USER_IMAGE_KEY, "")
}

fun manageNavigateBack(
    userIdStack: MutableList<String> = mutableListOf(),
    postIdStack: MutableList<String> = mutableListOf(),
    currentScreen: String = String(),
) {
    if (currentScreen == AppScreen.Profile.name) {
        userIdStack.removeLast()
    }
    if (currentScreen == AppScreen.Post.name) {
        postIdStack.removeLast()
    }
}

@Composable
fun createPermissionRequest(
    onSuccess: () -> Unit = {},
    onDismiss: () -> Unit = {}
): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onSuccess()
            } else {
                onDismiss()
            }
        }
    )
}

fun uCropContractBuilder(
    aspectRatioX: Float,
    aspectRatioY: Float
) = object : ActivityResultContract<List<Uri>, Uri?>() {
    override fun createIntent(context: Context, input: List<Uri>): Intent {
        val inputUri = input[0]
        val outputUri = input[1]
        val uCrop = UCrop.of(inputUri, outputUri)
            .withAspectRatio(aspectRatioX, aspectRatioY)
        return uCrop.getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode != UCrop.RESULT_ERROR && intent != null)
            UCrop.getOutput(intent!!) else Uri.EMPTY
    }
}

fun saveAndCropTempFile(
    cropImageActivity: ManagedActivityResultLauncher<List<Uri>, Uri?>,
    uri: Uri?
) {
    if (uri != null) {
        val outputFile = File.createTempFile(LocalDateTime.now().toString(), ".jpg")
        cropImageActivity.launch(listOf(uri!!, outputFile.toUri()))
    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}