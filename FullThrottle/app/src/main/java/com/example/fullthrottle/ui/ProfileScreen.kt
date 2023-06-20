package com.example.fullthrottle.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.deletePost
import com.example.fullthrottle.data.DBHelper.followUser
import com.example.fullthrottle.data.DBHelper.getFollowers
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getMotorbikesByUserId
import com.example.fullthrottle.data.DBHelper.getPostsByUserId
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DBHelper.unfollowUser
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.ui.ProfileScreenData.postImagesUrisLoaded
import com.example.fullthrottle.ui.ProfileScreenData.postsLoaded
import com.example.fullthrottle.ui.ProfileScreenData.uid
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal object ProfileScreenData {
    var uid by mutableStateOf(String())
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
    val coroutineScope = rememberCoroutineScope()

    if (uid != userId) {
        uid = userId
        postsLoaded = emptyList()
        postImagesUrisLoaded = emptyList()
    }
    var user by remember { mutableStateOf(User()) }
    var posts by rememberSaveable { mutableStateOf(postsLoaded) }
    var postImagesUris by rememberSaveable { mutableStateOf(postImagesUrisLoaded) }
    var imageUri by rememberSaveable { mutableStateOf<Uri>(Uri.EMPTY) }
    var motorbikes by rememberSaveable { mutableStateOf(emptyList<Motorbike>()) }
    val followLabel = stringResource(id = R.string.follow)
    val unfollowLabel = stringResource(id = R.string.unfollow)
    var followButtonState by rememberSaveable { mutableStateOf(followLabel) }
    var nFollowers by rememberSaveable { mutableStateOf(String()) }

    LaunchedEffect(key1 = "imageUri") {
        async {
            user = getUserById(userId) ?: User()
            val followers = getFollowers(user.userId.orEmpty())
            if (followers.map { follower -> follower.userId }.contains(settings[USER_ID_KEY])) {
                followButtonState = unfollowLabel
            }
        }.invokeOnCompletion {
            if (user.followers != null) {
                nFollowers = user.followers.toString()
            }
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
                //load(posts, postImagesUris)
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
        .clip(RoundedCornerShape(CORNER_RADIUS))

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
                verticalArrangement = centerArrangement,
            ) {
                BoldCenterText(text = nFollowers, Modifier.requiredWidth(100.dp))
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = user.username.toString(), fontWeight = FontWeight.SemiBold)
                if (userId != settings[USER_ID_KEY]) {
                    Spacer(modifier = Modifier.size(5.dp))
                    SimpleTextButton(
                        value = followButtonState,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        if (followButtonState == followLabel) {
                            followUser(userId, settings[USER_ID_KEY].orEmpty())
                            nFollowers = nFollowers.toInt().plus(1).toString()
                            followButtonState = unfollowLabel
                        } else {
                            unfollowUser(userId, settings[USER_ID_KEY].orEmpty())
                            nFollowers = nFollowers.toInt().plus(-1).toString()
                            followButtonState = followLabel
                        }
                    }
                }
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
            motorbikes.forEach { motorbike ->
                if (!motorbike.deleted!!) {
                    Row(
                        horizontalArrangement = leftArrangement,
                    ) {
                        Text(text = "${motorbike.brand} ${motorbike.model} ${motorbike.productionYear}")
                    }
                }
            }
            
            Spacer(modifier = Modifier.size(10.dp))

            if (userId == settings[USER_ID_KEY]) {
                Row(
                    horizontalArrangement = leftArrangement,
                ) {
                    OutlineTextButton(
                        value = stringResource(id = R.string.modify_profile),
                        modifier = Modifier.height(30.dp)
                    ) {
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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp)
                ) {
                    items(posts) { post ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    goToPost(post.postId as String)
                                },
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
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

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(start = 5.dp)
                                    ) {
                                        Text(text = post.publishDate.orEmpty())
                                        Text(text = post.title.orEmpty())
                                    }

                                    if (userId == settings[USER_ID_KEY]) {
                                        val openDialogDeletePost =
                                            rememberSaveable { mutableStateOf(false) }
                                        val saveDeletePost = rememberSaveable { mutableStateOf(false) }
                                        val deletePostTitle =
                                            stringResource(id = R.string.confirm_delete_title)

                                        if (openDialogDeletePost.value) {
                                            SimpleAlertDialog(
                                                title = deletePostTitle,
                                                text = "",
                                                openDialog = openDialogDeletePost,
                                                result = saveDeletePost,
                                                onConfirm = {
                                                    coroutineScope.launch {
                                                        deletePost(post.postId.orEmpty())
                                                        posts = getPostsByUserId(userId)
                                                    }
                                                }
                                            )
                                        }

                                        Column(
                                            modifier = Modifier.padding(start = 5.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    openDialogDeletePost.value = true
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Delete,
                                                    contentDescription = "Delete post"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}