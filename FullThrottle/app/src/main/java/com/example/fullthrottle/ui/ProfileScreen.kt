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
import androidx.compose.ui.Alignment
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
import com.example.fullthrottle.data.DBHelper.getMotorbikesByUserId
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
import com.example.fullthrottle.data.entities.Motorbike
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
    val centerArrangement = Arrangement.Center

    var user by remember { mutableStateOf(User()) }
    var imageUri by rememberSaveable { mutableStateOf<Uri>(Uri.EMPTY) }
    var motorbikes by remember { mutableStateOf(emptyList<Motorbike>()) }
    LaunchedEffect(
        key1 = "imageUri",
        block = {
            async {
                user = getUserById(settings[USER_ID_KEY]!!) ?: User()
            }
            async {
                motorbikes = getMotorbikesByUserId(settings[USER_ID_KEY]!!) as List<Motorbike>
                println(motorbikes)
            }
            if (settings[USER_IMAGE_KEY].toString().isNotEmpty()) {
                val imageUrl = settings[USER_ID_KEY] + "/" + settings[USER_IMAGE_KEY]
                imageUri = getImage(imageUrl)
            }
        }
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = followModifier,
                verticalArrangement = centerArrangement
            ) {
                BoldCenterText(text = user.followers.toString())
                SimpleCenterText(text = stringResource(id = R.string.followers_label))
            }

            ShowImage(
                imgUri = imageUri,
                modifier = Modifier
                    .requiredHeight(100.dp)
                    .requiredWidth(100.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = followModifier,
                verticalArrangement = centerArrangement
            ) {
                BoldCenterText(text = user.followed.toString())
                SimpleCenterText(text = stringResource(id = R.string.followeds_label))
            }
        }

        val leftArrangement = Arrangement.Start
        val baseModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
        Column {
            Row(
                horizontalArrangement = leftArrangement,
                modifier = baseModifier
            ) {
                Text(text = user.username.toString(), fontWeight = FontWeight.SemiBold)
            }
            Row(
                horizontalArrangement = leftArrangement,
                modifier = baseModifier
            ) {
                Text(text = "Mail: ", fontWeight = FontWeight.SemiBold)
                Text(text = user.mail.toString())
            }
            Row(
                horizontalArrangement = leftArrangement,
                modifier = baseModifier
            ) {
                Text(text = stringResource(id = R.string.my_motorbikes), fontWeight = FontWeight.SemiBold)
            }
            motorbikes.forEach {
                Row(
                    horizontalArrangement = leftArrangement,
                    modifier = baseModifier
                ) {
                    Text(text = it.brand + " " + it.model + " " + it.productionYear)
                }
            }
        }
        
        Column {
            Row(
                horizontalArrangement = leftArrangement,
                modifier = baseModifier
            ) {
                Text(text = stringResource(id = R.string.my_posts), fontWeight = FontWeight.Bold)
            }
        }

    }
}