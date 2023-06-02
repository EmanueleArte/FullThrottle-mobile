package com.example.fullthrottle.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getMotorbikesByUserId
import com.example.fullthrottle.data.DBHelper.getPostsByUserId
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.ui.ProfileScreenData.load
import com.example.fullthrottle.ui.ProfileScreenData.postsLoaded
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal object ProfileScreenData {
    var postsLoaded by mutableStateOf(emptyList<Post>())
    var postImagesUrisLoaded by mutableStateOf(emptyList<Uri>())

    fun load(posts: List<Post>, postImagesUris: List<Uri>) {
        postsLoaded = posts
        postImagesUrisLoaded = postImagesUris
    }

}

@Composable
fun ProfileScreen(
    settingsViewModel: SettingsViewModel,
    navigateTo: Map<String, () -> Unit>,
    userId: String,
    goToPost: (String) -> Unit
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())

    var user by remember { mutableStateOf(User()) }
    var posts by rememberSaveable { mutableStateOf(postsLoaded) }
    var postImagesUris by rememberSaveable { mutableStateOf(ProfileScreenData.postImagesUrisLoaded) }
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
                load(posts, postImagesUris)
            }
        }
    }

    val centerArrangement = Arrangement.Center
    val leftArrangement = Arrangement.Start
    val baseModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = MAIN_H_PADDING)
    val followModifier = Modifier
        .requiredWidth(100.dp)
        .requiredHeight(100.dp)


    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
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
                BoldCenterText(text = user.followers.toString(), Modifier.requiredWidth(100.dp))
                SimpleCenterText(text = stringResource(id = R.string.followers_label), Modifier.requiredWidth(100.dp))
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
                BoldCenterText(text = user.followed.toString(), Modifier.requiredWidth(100.dp))
                SimpleCenterText(text = stringResource(id = R.string.followeds_label), Modifier.requiredWidth(100.dp))
            }
        }

        Column(
            modifier = baseModifier
        ) {
            Row(
                horizontalArrangement = leftArrangement,
            ) {
                Text(text = user.username.toString(), fontWeight = FontWeight.SemiBold)
            }
            if (userId == settings[USER_ID_KEY]) {
                Row(
                    horizontalArrangement = leftArrangement,
                ) {
                    Text(text = "Mail: ", fontWeight = FontWeight.SemiBold)
                    Text(text = user.mail.toString())
                }
            }
            Row(
                horizontalArrangement = leftArrangement,
            ) {
                Text(text = stringResource(id = R.string.my_motorbikes), fontWeight = FontWeight.SemiBold)
            }
            motorbikes.forEach {
                Row(
                    horizontalArrangement = leftArrangement,
                ) {
                    Text(text = it.brand + " " + it.model + " " + it.productionYear)
                }
            }
            
            Spacer(modifier = Modifier.size(10.dp))

            if (userId == settings[USER_ID_KEY]) {
                Row(
                    horizontalArrangement = leftArrangement,
                ) {
                    OutlineTextButton(value = stringResource(id = R.string.modify_profile)) {
                        navigateTo["profileModification"]?.invoke()
                    }
                }
            }
        }
        
        Column(
            modifier = baseModifier
        ) {
            Row(
                horizontalArrangement = leftArrangement,
            ) {
                Text(text = stringResource(id = R.string.my_posts), fontWeight = FontWeight.Bold)
            }
            if (posts.isEmpty() || postImagesUris.isEmpty()) {
                LoadingAnimation()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
                ) {
                    items(posts) { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    goToPost(post.postId as String)
                                }
                            //elevation = CardDefaults.cardElevation(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    PostImage(
                                        imgUri = postImagesUris[posts.indexOf(post)],
                                        contentDescription = "post image",
                                        modifier = Modifier
                                            .requiredWidth(98.dp)
                                            .requiredHeight(56.dp)
                                            .clip(RoundedCornerShape(CORNER_RADIUS))
                                    )
                                }

                                Column(
                                    modifier = Modifier.padding(start = 5.dp)
                                ) {
                                    Text(text = post.publishDate.orEmpty())
                                    Text(text = post.title.orEmpty())
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}