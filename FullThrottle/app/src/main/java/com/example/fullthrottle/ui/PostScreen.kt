package com.example.fullthrottle.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.getCommentsByPostId
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getMotorbikeById
import com.example.fullthrottle.data.DBHelper.getPostById
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.entities.Comment
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import kotlinx.coroutines.async

@Composable
fun PostScreen(
    postId : String
) {
    val context = LocalContext.current

    var post by remember { mutableStateOf(Post()) }
    var user by remember { mutableStateOf(User()) }
    var motorbike by remember { mutableStateOf(Motorbike()) }
    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
    var comments by remember { mutableStateOf(emptyList<Comment>()) }
    var commentsUsers by remember { mutableStateOf(emptyList<User>()) }

    LaunchedEffect(
        key1 = "posts",
        block = {
            async {
                val res = getPostById(postId)
                if(res != null) {
                    user = getUserById(res.userId as String) as User
                    motorbike = getMotorbikeById(res.motorbikeId as String) as Motorbike
                    imageUri = getImageUri(res.userId + "/" + res.postImg)
                    println(imageUri)
                    post = res
                }
            }
            async {
                val tComments = getCommentsByPostId(postId)
                commentsUsers = tComments.map { comment -> getUserById(comment.userId as String) as User }
                comments = tComments
            }
        }
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.fullthrottle_logo_light),
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
                        .padding(5.dp)
                        .requiredHeight(40.dp)
                )
            }
        }
        item{
            AsyncImage(
                model = imageUri,
                contentDescription = "post image",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            )
        }
        item {
            Text(
                text = "${post.title}",
                fontWeight = FontWeight.Bold
            )
        }
        item{
            Text(
                text = "Piace a ${post.likesNumber} riders",
                fontWeight = FontWeight.Thin
            )
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
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fullthrottle_logo_light),
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