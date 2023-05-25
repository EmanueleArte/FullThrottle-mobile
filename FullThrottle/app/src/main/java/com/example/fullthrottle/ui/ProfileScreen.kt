package com.example.fullthrottle.ui

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.data.DBHelper.getImage
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.ui.theme.md_theme_light_primary
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.*

@Composable
fun ProfileScreen(
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    val followModifier = Modifier
        .requiredWidth(100.dp)
        .requiredHeight(100.dp)

    var user by remember { mutableStateOf(User()) }
    var imageUri by rememberSaveable { mutableStateOf<Uri>(Uri.EMPTY) }
    LaunchedEffect(
        key1 = "imageUri",
        block = {
            async {
                user = getUserById(settings[USER_ID_KEY]!!) ?: User()
            }
            var imageUrl = ""
            if (settings[USER_IMAGE_KEY].toString().isNotEmpty()) {
                imageUrl = settings[USER_ID_KEY] + "/" + settings[USER_IMAGE_KEY]
            }
            imageUri = getImage(imageUrl)
        }
    )

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = followModifier,
                verticalArrangement = Arrangement.Center
            ) {
                BoldCenterText(text = user.followers.toString())
                SimpleCenterText(text = stringResource(id = R.string.followers_label))
            }

            ShowImage(
                imgUri = imageUri,
                modifier = Modifier
                    .padding(5.dp)
                    .requiredHeight(100.dp)
                    .requiredWidth(100.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = followModifier,
                verticalArrangement = Arrangement.Center
            ) {
                BoldCenterText(text = user.followed.toString())
                SimpleCenterText(text = stringResource(id = R.string.followeds_label))
            }
        }
        Text(text = user.username.toString())
        Text(text = "Mail: " + user.mail.toString())
        Text(text = "Le mie moto:")
        Text(text = "KTM Duke 890")
        Text(text = "Yamaha R1 2022")
        Text(
            text = "Modifica account",
            color = md_theme_light_primary
        )
    }
}