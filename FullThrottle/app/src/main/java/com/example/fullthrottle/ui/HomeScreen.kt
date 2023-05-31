package com.example.fullthrottle.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getMotorbikeById
import com.example.fullthrottle.data.DBHelper.getRecentPosts
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import kotlinx.coroutines.async

@Composable
fun HomeScreen(
    goToPost: (String) -> Unit
) {
    val context = LocalContext.current

    var posts by rememberSaveable { mutableStateOf(emptyList<Post>()) }
    var users by rememberSaveable { mutableStateOf(emptyList<User>()) }
    var motorbikes by rememberSaveable { mutableStateOf(emptyList<Motorbike>()) }
    var postImagesUris by rememberSaveable { mutableStateOf(emptyList<Uri>()) }
    var userImagesUris by rememberSaveable { mutableStateOf(emptyList<Uri>()) }

    LaunchedEffect(key1 = "posts") {
        async {
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
            posts = tPosts
        }
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
                            goToPost(post.postId as String)
                        },
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column {
                        Row {
                            ProfileImage(
                                imgUri = userImagesUris[posts.indexOf(post)],
                                contentDescription = "user image",
                                modifier = Modifier
                                    .padding(5.dp)
                                    .requiredHeight(40.dp)
                                    .requiredWidth(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Column {
                                Text(
                                    text = "${users[posts.indexOf(post)].username}",
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
                            imgUri = postImagesUris[posts.indexOf(post)],
                            contentDescription = "post image",
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Text(
                            text = "${post.title}",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Piace a ${post.likesNumber} riders",
                            fontWeight = FontWeight.Thin
                        )
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