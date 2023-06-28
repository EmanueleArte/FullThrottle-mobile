package com.example.fullthrottle.ui

import android.net.Uri
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
import com.example.fullthrottle.data.LocalDbViewModel
import com.example.fullthrottle.data.entities.LikeBool
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.ui.HomeScreenData.firstLoad
import com.example.fullthrottle.ui.HomeScreenData.usersLoaded
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

internal object HomeScreenData {
    var usersLoaded by mutableStateOf(emptyList<User>())
    var firstLoad by mutableStateOf(true)
}

@Composable
fun HomeScreen(
    goToPost: (String) -> Unit,
    goToProfile: (String) -> Unit,
    goToMap: (String) -> Unit,
    settingsViewModel: SettingsViewModel,
    localDbViewModel: LocalDbViewModel
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    val coroutineScope = rememberCoroutineScope()

    var posts by rememberSaveable { mutableStateOf(emptyList<Post>()) }
    var users by rememberSaveable { mutableStateOf(emptyList<User>()) }
    var motorbikes by rememberSaveable { mutableStateOf(emptyList<Motorbike>()) }
    var postImagesUris by rememberSaveable { mutableStateOf(emptyList<Uri>()) }
    var userImagesUris by rememberSaveable { mutableStateOf(emptyList<Uri>()) }
    var likes by rememberSaveable { mutableStateOf(emptyList<Boolean>()) }
    var followedsIds by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var filteredPosts by rememberSaveable { mutableStateOf(posts) }

    // Loads home posts from local room db
    val tPosts = localDbViewModel.posts.collectAsState(initial = listOf()).value
    if (tPosts.isNotEmpty()) {
        LaunchedEffect(Unit) {
            users = tPosts.map { post ->
                localDbViewModel.getUserById(post.userId.orEmpty())
            }
            postImagesUris = tPosts.map { post -> getImageUri(post.userId + "/" + post.postImg) }
            userImagesUris = users.map { user ->
                if (user.userImg.toString().isNotEmpty())
                    getImageUri(user.userId + "/" + user.userImg)
                else
                    Uri.EMPTY
            }
            motorbikes = tPosts.map { post -> localDbViewModel.getMotorbikeById(post.motorbikeId as String) }
            likes = tPosts.map { post -> localDbViewModel.getLike(post.postId) }
            posts = tPosts
        }
    }

    // Posts filter (All/Only followeds)
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
                if (user.userImg.orEmpty().isNotEmpty())
                    getImageUri(user.userId + "/" + user.userImg)
                else
                    Uri.EMPTY
            }
            likes = tPosts.map { post -> checkLike(post.postId, settings[USER_ID_KEY].toString()) }
            followedsIds = getFolloweds(settings[USER_ID_KEY].toString()).map { user -> user.userId }
            posts = tPosts
        }.invokeOnCompletion {
            coroutineScope.launch(Dispatchers.IO) {
                localDbViewModel.deleteAllPosts()
                posts.forEach { post ->
                    localDbViewModel.addNewPost(post)
                }
                users.forEach { user ->
                    localDbViewModel.addNewUser(user)
                }
                motorbikes.forEach { motorbike ->
                    localDbViewModel.addNewMotorbike(motorbike)
                }
                posts.forEachIndexed { index, post ->
                    localDbViewModel.addNewLike(LikeBool(
                        likeId = UUID.randomUUID().toString(),
                        postId = post.postId,
                        value = likes[index]
                    ))
                }
            }
        }
    }

    val baseModifier = Modifier.padding(horizontal = 10.dp)

    if (filteredPosts.isEmpty() || postImagesUris.isEmpty() || likes.isEmpty()) {
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
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(filteredPosts) { post ->
                    val index = posts.indexOf(post)
                    Card(
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 10.dp)
                            .fillMaxWidth()
                            .clickable {
                                goToPost(post.postId)
                            },
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProfileImage(
                                    imgUri = if (userImagesUris.size > index) userImagesUris[index] else Uri.EMPTY,
                                    contentDescription = "user image",
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                        .requiredHeight(40.dp)
                                        .requiredWidth(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { if (users.size > index) goToProfile(users[index].userId) },
                                )
                                Column {
                                    Text(
                                        text = if (users.size > index) "${users[posts.indexOf(post)].username}" else "",
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
                                imgUri = if (postImagesUris.size > index) postImagesUris[index] else Uri.EMPTY,
                                contentDescription = "post image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .requiredHeight(200.dp)
                            )
                            Spacer(modifier = Modifier.size(3.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
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
                                    imageVector = if (likes.size > index && likes[index]) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
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
                                                        post.postId,
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
                                                    likes = posts.map { tPost ->
                                                        if (post.postId == tPost.postId) {
                                                            like
                                                        } else {
                                                            likes[posts.indexOf(tPost)]
                                                        }
                                                    }
                                                }
                                        }
                                )
                            }
                            Text(
                                text = if (motorbikes.size > index) stringResource(id = R.string.motorbike) + ": ${motorbikes[index].brand} ${motorbikes[index].model}" else "",
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