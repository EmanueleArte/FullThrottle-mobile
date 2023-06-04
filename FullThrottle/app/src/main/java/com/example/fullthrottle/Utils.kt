package com.example.fullthrottle

import android.content.Context
import android.text.TextUtils
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.example.fullthrottle.data.DataStoreConstants
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
import com.example.fullthrottle.viewModel.SettingsViewModel
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object Utils {
    private const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$"

    fun isValidPassword(password: String): Boolean {
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun isValidMail(mail: String): Boolean {
        return !TextUtils.isEmpty(mail) && android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    fun isValidUsername(username: String): Boolean {
        return !TextUtils.isEmpty(username)
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