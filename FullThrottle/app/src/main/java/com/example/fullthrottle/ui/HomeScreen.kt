package com.example.fullthrottle.ui

import android.Manifest
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
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
import com.example.fullthrottle.data.DBHelper.checkLike
import com.example.fullthrottle.data.DBHelper.getFolloweds
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getMotorbikeById
import com.example.fullthrottle.data.DBHelper.getRecentPosts
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DBHelper.toggleLike
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.HomeValues.registerFilterValueListener
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.ui.HomeScreenData.firstLoad
import com.example.fullthrottle.ui.HomeScreenData.followedsIdsLoaded
import com.example.fullthrottle.ui.HomeScreenData.likesLoaded
import com.example.fullthrottle.ui.HomeScreenData.load
import com.example.fullthrottle.ui.HomeScreenData.motorbikesLoaded
import com.example.fullthrottle.ui.HomeScreenData.postImagesUrisLoaded
import com.example.fullthrottle.ui.HomeScreenData.postsLoaded
import com.example.fullthrottle.ui.HomeScreenData.userImagesUrisLoaded
import com.example.fullthrottle.ui.HomeScreenData.usersLoaded
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal object HomeScreenData {
    var postsLoaded by mutableStateOf(emptyList<Post>())
    var usersLoaded by mutableStateOf(emptyList<User>())
    var motorbikesLoaded by mutableStateOf(emptyList<Motorbike>())
    var postImagesUrisLoaded by mutableStateOf(emptyList<Uri>())
    var userImagesUrisLoaded by mutableStateOf(emptyList<Uri>())
    var likesLoaded by mutableStateOf(emptyList<Boolean>())
    var followedsIdsLoaded by mutableStateOf(emptyList<String>())
    var firstLoad by mutableStateOf(true)

    fun load(posts: List<Post>, users: List<User>, motorbikes: List<Motorbike>, postImagesUris: List<Uri>, userImagesUris: List<Uri>, likes: List<Boolean>, followedsIds: List<String>) {
        postsLoaded = posts
        usersLoaded = users
        motorbikesLoaded = motorbikes
        postImagesUrisLoaded = postImagesUris
        userImagesUrisLoaded = userImagesUris
        likesLoaded = likes
        followedsIdsLoaded = followedsIds
    }

}

@Composable
fun HomeScreen(
    goToPost: (String) -> Unit,
    goToProfile: (String) -> Unit,
    goToMap: (String) -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    val coroutineScope = rememberCoroutineScope()

    var posts by rememberSaveable { mutableStateOf(postsLoaded) }
    var users by rememberSaveable { mutableStateOf(usersLoaded) }
    var motorbikes by rememberSaveable { mutableStateOf(motorbikesLoaded) }
    var postImagesUris by rememberSaveable { mutableStateOf(postImagesUrisLoaded) }
    var userImagesUris by rememberSaveable { mutableStateOf(userImagesUrisLoaded) }
    var likes by rememberSaveable { mutableStateOf(likesLoaded) }
    var followedsIds by rememberSaveable { mutableStateOf(followedsIdsLoaded) }
    var filteredPosts by rememberSaveable { mutableStateOf(posts) }

    registerFilterValueListener {
        filteredPosts = if (it == R.string.all_posts) {
            posts
        } else {
            posts.filter { post -> followedsIds.contains(post.userId) }
        }
    }

    LaunchedEffect(Unit) {
        async {
            if (usersLoaded.isNotEmpty()) {
                firstLoad = false
            }
            val tPosts = getRecentPosts()
            users = tPosts.map { post -> getUserById(post.userId as String) as User }
            motorbikes = tPosts.map { post -> getMotorbikeById(post.motorbikeId as String) as Motorbike }
            postImagesUris = tPosts.map { post -> getImageUri(post.userId + "/" + post.postImg) }
            userImagesUris = users.map { user ->
                if (user.userImg.toString().isNotEmpty())
                    getImageUri(user.userId + "/" + user.userImg)
                else
                    Uri.EMPTY
            }
            likes = tPosts.map { post -> checkLike(post.postId.toString(), settings[USER_ID_KEY].toString()) }
            followedsIds = getFolloweds(settings[USER_ID_KEY].toString()).map { user -> user.userId.toString() }
            posts = tPosts
        }.invokeOnCompletion {
            load(posts, users, motorbikes, postImagesUris, userImagesUris, likes, followedsIds)
        }
    }

    val baseModifier = Modifier.padding(horizontal = 5.dp)

    if (filteredPosts.isEmpty() || postImagesUris.isEmpty()) {
        LoadingAnimation()
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MAIN_H_PADDING)
        ) {
            Row(modifier = Modifier.heightIn(0.dp, 50.dp)) {
                if (!firstLoad) {
                    LoadingAnimation(2000)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {}
                items(filteredPosts) { post ->
                    Card(
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 10.dp)
                            .fillMaxWidth()
                            .clickable {
                                goToPost(post.postId as String)
                            },
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProfileImage(
                                    imgUri = userImagesUris[posts.indexOf(post)],
                                    contentDescription = "user image",
                                    modifier = baseModifier
                                        .padding(vertical = 8.dp)
                                        .requiredHeight(40.dp)
                                        .requiredWidth(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { goToProfile(users[posts.indexOf(post)].userId as String) },
                                )
                                Column {
                                    Text(
                                        text = "${users[posts.indexOf(post)].username}",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(text = "${post.publishDate}")
                                }
                                Spacer(Modifier.weight(1f))
                                IconButton(onClick = { goToMap(post.position.toString()) }) {
                                    Icon(
                                        Icons.Filled.Place,
                                        contentDescription = "post location",
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .requiredHeight(40.dp)
                                    )
                                }
                            }
                            PostImage(
                                imgUri = postImagesUris[posts.indexOf(post)],
                                contentDescription = "post image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .requiredHeight(200.dp)
                            )
                            Spacer(modifier = Modifier.size(3.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = baseModifier
                                ) {
                                    Text(
                                        text = "${post.title}",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = stringResource(id = R.string.likes_to) + " ${post.likesNumber} riders",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = if (likes[posts.indexOf(post)]) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "like button",
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(CORNER_RADIUS))
                                        .size(30.dp)
                                        .bounceClick()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            var like = false
                                            coroutineScope
                                                .launch {
                                                    like = toggleLike(
                                                        post.postId.toString(),
                                                        settings[USER_ID_KEY].toString()
                                                    )
                                                }
                                                .invokeOnCompletion {
                                                    posts = posts.map { tPost ->
                                                        if (post.postId == tPost.postId) {
                                                            if (like) {
                                                                tPost.copy(
                                                                    likesNumber = (tPost.likesNumber
                                                                        ?.toInt()
                                                                        ?.plus(1)).toString()
                                                                )
                                                            } else {
                                                                tPost.copy(
                                                                    likesNumber = (tPost.likesNumber
                                                                        ?.toInt()
                                                                        ?.minus(1)).toString()
                                                                )
                                                            }
                                                        } else tPost
                                                    }
                                                    likes = posts.map { post ->
                                                        if (posts.indexOf(post) == posts.indexOf(
                                                                post
                                                            )
                                                        ) {
                                                            like
                                                        } else {
                                                            likes[posts.indexOf(post)]
                                                        }
                                                    }
                                                }
                                        }
                                )
                            }
                            Text(
                                text = stringResource(id = R.string.motorbike) + ": ${motorbikes[posts.indexOf(post)].brand} ${
                                    motorbikes[posts.indexOf(
                                        post
                                    )].model
                                }",
                                modifier = baseModifier
                            )
                            Text(text = stringResource(id = R.string.path_length) + ": ${post.length}km", modifier = baseModifier)
                            Text(text = "${post.description}", modifier = baseModifier)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }
    }
}