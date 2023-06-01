package com.example.fullthrottle.ui

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.data.DBHelper.checkLike
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getMotorbikeById
import com.example.fullthrottle.data.DBHelper.getRecentPosts
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DBHelper.toggleLike
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.ui.HomeScreenData.firstLoad
import com.example.fullthrottle.ui.HomeScreenData.likesLoaded
import com.example.fullthrottle.ui.HomeScreenData.load
import com.example.fullthrottle.ui.HomeScreenData.motorbikesLoaded
import com.example.fullthrottle.ui.HomeScreenData.postImagesUrisLoaded
import com.example.fullthrottle.ui.HomeScreenData.postsLoaded
import com.example.fullthrottle.ui.HomeScreenData.userImagesUrisLoaded
import com.example.fullthrottle.ui.HomeScreenData.usersLoaded
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object HomeScreenData {
    var postsLoaded by mutableStateOf(emptyList<Post>())
    var usersLoaded by mutableStateOf(emptyList<User>())
    var motorbikesLoaded by mutableStateOf(emptyList<Motorbike>())
    var postImagesUrisLoaded by mutableStateOf(emptyList<Uri>())
    var userImagesUrisLoaded by mutableStateOf(emptyList<Uri>())
    var likesLoaded by mutableStateOf(emptyList<Boolean>())
    var firstLoad by mutableStateOf(true)

    fun load(posts: List<Post>, users: List<User>, motorbikes: List<Motorbike>, postImagesUris: List<Uri>, userImagesUris: List<Uri>, likes: List<Boolean>) {
        postsLoaded = posts
        usersLoaded = users
        motorbikesLoaded = motorbikes
        postImagesUrisLoaded = postImagesUris
        userImagesUrisLoaded = userImagesUris
        likesLoaded = likes
    }

}

@Composable
fun HomeScreen(
    goToPost: (String) -> Unit,
    goToProfile: (String) -> Unit,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    val coroutineScope = rememberCoroutineScope()

    var posts by rememberSaveable { mutableStateOf(postsLoaded) }
    var users by rememberSaveable { mutableStateOf(usersLoaded) }
    var motorbikes by rememberSaveable { mutableStateOf(motorbikesLoaded) }
    var postImagesUris by rememberSaveable { mutableStateOf(postImagesUrisLoaded) }
    var userImagesUris by rememberSaveable { mutableStateOf(userImagesUrisLoaded) }
    var likes by rememberSaveable { mutableStateOf(likesLoaded) }

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
            posts = tPosts
            //posts.intersect()
            //posts.toMutableList().addAll(tPosts)
        }.invokeOnCompletion {
            load(posts, users, motorbikes, postImagesUris, userImagesUris, likes)
        }
    }

    if (posts.isEmpty() || postImagesUris.isEmpty()) {
        LoadingAnimation()
    } else {
        Column {
            Row(modifier = Modifier.heightIn(0.dp, 50.dp)) {
                if (!firstLoad) {
                    LoadingAnimation(2000)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {}
                itemsIndexed(posts) { i, post ->
                    //val i = posts.indexOf(post)
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .clickable {
                                goToPost(post.postId as String)
                            },
                        elevation = CardDefaults.cardElevation(10.dp)
                    ) {
                        Column {
                            Row {
                                ProfileImage(
                                    imgUri = userImagesUris[i],
                                    contentDescription = "user image",
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .requiredHeight(40.dp)
                                        .requiredWidth(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .clickable { goToProfile(users[i].userId as String) },
                                )
                                Column {
                                    Text(
                                        text = "${users[i].username}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = "${post.publishDate}")
                                }
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    Icons.Filled.Place,
                                    contentDescription = "post location",
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .requiredHeight(40.dp)
                                )
                            }
                            PostImage(
                                imgUri = postImagesUris[i],
                                contentDescription = "post image",
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Row {
                                Column {
                                    Text(
                                        text = "${post.title}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Piace a ${post.likesNumber} riders",
                                        fontWeight = FontWeight.Thin
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = if (likes[i]) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "like button",
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(30.dp)
                                        .clickable {
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
                                                        if (posts.indexOf(post) == i) {
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
                                text = "Moto: ${motorbikes[posts.indexOf(post)].brand} ${
                                    motorbikes[posts.indexOf(
                                        post
                                    )].model
                                }"
                            )
                            Text(text = "Lunghezza percorso: ${post.length}km")
                            Text(text = "${post.description}")
                        }
                    }
                }
            }
        }
    }
}