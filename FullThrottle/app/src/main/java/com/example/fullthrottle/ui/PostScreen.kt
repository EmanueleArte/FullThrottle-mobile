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
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.data.DBHelper.checkLike
import com.example.fullthrottle.data.DBHelper.getCommentsByPostId
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getMotorbikeById
import com.example.fullthrottle.data.DBHelper.getPostById
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.entities.Comment
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.viewModel.SettingsViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun PostScreen(
    postId : String,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    val coroutineScope = rememberCoroutineScope()

    var post by remember { mutableStateOf(Post()) }
    var user by remember { mutableStateOf(User()) }
    var userImageURI by remember { mutableStateOf(Uri.EMPTY) }
    var motorbike by remember { mutableStateOf(Motorbike()) }
    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
    var like by remember { mutableStateOf(false) }
    var comments by remember { mutableStateOf(emptyList<Comment>()) }
    var commentsUsers by remember { mutableStateOf(emptyList<User>()) }
    var commentsUsersImagesURIS by remember { mutableStateOf(emptyList<Uri>()) }

    LaunchedEffect(
        key1 = "posts",
        block = {
            async {
                val res = getPostById(postId)
                if (res != null) {
                    user = getUserById(res.userId as String) as User
                    if (user.userImg.toString().isNotEmpty()) {
                        userImageURI = getImageUri(res.userId + "/" + user.userImg)
                    }
                    motorbike = getMotorbikeById(res.motorbikeId as String) as Motorbike
                    imageUri = getImageUri(res.userId + "/" + res.postImg)
                    println(imageUri)
                    post = res
                }
            }
            async {
                val tComments = getCommentsByPostId(postId)
                commentsUsers = tComments.map { comment -> getUserById(comment.userId as String) as User }
                commentsUsersImagesURIS = commentsUsers.map { user ->
                    if (user.userImg.toString().isNotEmpty()) {
                        getImageUri(user.userId + "/" + user.userImg)
                    } else {
                        Uri.EMPTY
                    }
                }
                comments = tComments
            }
            async {
                like = checkLike(postId, settings[USER_ID_KEY].toString())
            }
        }
    )

    LazyColumn {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ProfileImage(
                    imgUri = userImageURI,
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
                        text = "${user.username}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "${post.publishDate}")
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Filled.Place,
                    contentDescription = "post location",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp)
                )
            }
        }
        item{
            PostImage(
                imgUri = imageUri,
                contentDescription = "post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            )
        }
        item {
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
                    imageVector = if (like) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "like button",
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp)
                        .clickable {
                            coroutineScope
                                .launch {
                                    like = DBHelper.toggleLike(
                                        postId,
                                        settings[USER_ID_KEY].toString()
                                    )
                                }
                                .invokeOnCompletion {
                                    post = if (like) {
                                        post.copy(
                                            likesNumber = (post.likesNumber
                                                ?.toInt()
                                                ?.plus(1)).toString()
                                        )
                                    } else {
                                        post.copy(
                                            likesNumber = (post.likesNumber
                                                ?.toInt()
                                                ?.minus(1)).toString()
                                        )
                                    }
                                }
                        }
                )
            }
        }
        item{
            Text(text = "Moto: ${motorbike.brand} ${motorbike.model}")
        }
        item{
            Text(text = "Lunghezza percorso: ${post.length}km")
        }
        item{
            Text(text = "${post.description}")
        }
        item{
            Text(
                text = "Commenti",
                fontWeight = FontWeight.Bold
            )
        }
        items(comments) {comment ->
            Card (
                modifier = Modifier.padding(10.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ProfileImage(
                        imgUri = commentsUsersImagesURIS[comments.indexOf(comment)],
                        contentDescription = "comment user image",
                        modifier = Modifier
                            .padding(5.dp)
                            .requiredHeight(40.dp)
                            .requiredWidth(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Column {
                        Row {
                            Text(
                                text = "${commentsUsers[comments.indexOf(comment)].username}",
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "${comment.publishDate}")
                        }
                        Text(text = comment.text as String)
                    }
                }
            }
        }
    }
}