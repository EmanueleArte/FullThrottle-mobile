package com.example.fullthrottle.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getMotorbikesByUserId
import com.example.fullthrottle.data.DBHelper.getPostsByUserId
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    settingsViewModel: SettingsViewModel,
    navigateTo: Map<String, () -> Unit>,
    userId: String
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    val followModifier = Modifier
        .requiredWidth(100.dp)
        .requiredHeight(100.dp)
    val centerArrangement = Arrangement.Center

    var user by remember { mutableStateOf(User()) }
    var posts by rememberSaveable { mutableStateOf(emptyList<Post>()) }
    var postImagesUris by rememberSaveable { mutableStateOf(emptyList<Uri>()) }
    var imageUri by rememberSaveable { mutableStateOf<Uri>(Uri.EMPTY) }
    var motorbikes by remember { mutableStateOf(emptyList<Motorbike>()) }
    LaunchedEffect(key1 = "imageUri") {
        async {
            user = getUserById(userId) ?: User()
        }.invokeOnCompletion {
            if (user.userImg?.isNotEmpty()!!) {
                val imageUrl = userId + "/" + user.userImg
                launch {
                    imageUri = getImageUri(imageUrl)
                }
            }
        }
        async {
            motorbikes = getMotorbikesByUserId(userId)
            posts = getPostsByUserId(userId)
        }.invokeOnCompletion {
            launch {
                postImagesUris = posts.map { post -> getImageUri(post.userId + "/" + post.postImg) }
            }
        }
    }

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
                modifier = followModifier
                    .clickable { navigateTo["followers"]?.invoke() },
                verticalArrangement = centerArrangement
            ) {
                BoldCenterText(text = user.followers.toString())
                SimpleCenterText(text = stringResource(id = R.string.followers_label))
            }

            ProfileImage(
                imgUri = imageUri,
                modifier = Modifier
                    .requiredHeight(100.dp)
                    .requiredWidth(100.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = followModifier
                        .clickable { navigateTo["followed"]?.invoke() },
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
            if (userId == settings[USER_ID_KEY]) {
                Row(
                    horizontalArrangement = leftArrangement,
                    modifier = baseModifier
                ) {
                    Text(text = "Mail: ", fontWeight = FontWeight.SemiBold)
                    Text(text = user.mail.toString())
                }
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
            
            Spacer(modifier = Modifier.size(10.dp))

            if (userId == settings[USER_ID_KEY]) {
                Row(
                    horizontalArrangement = leftArrangement,
                    modifier = baseModifier
                ) {
                    OutlineTextButton(value = stringResource(id = R.string.modify_profile)) {
                        navigateTo["profileModification"]?.invoke()
                    }
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
            if (posts.isEmpty() || postImagesUris.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ThreeBounce()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(posts) { post ->
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .clickable {
                                    /* TODO: Navigate to post */
                                    //goToPost(post.postId as String)
                                },
                            elevation = CardDefaults.cardElevation(10.dp)
                        ) {
                            Row {
                                PostImage(
                                    imgUri = postImagesUris[posts.indexOf(post)],
                                    contentDescription = "post image",
                                    modifier = Modifier
                                        .width(200.dp)
                                )

                                Text(text = post.title.orEmpty())
                            }
                        }
                    }
                }
            }
        }

    }
}