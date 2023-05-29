package com.example.fullthrottle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
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
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.data.DBHelper.getMotorbikeById
import com.example.fullthrottle.data.DBHelper.getUserById
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
    LaunchedEffect(
        key1 = "posts",
        block = {
            async {
                val res = DBHelper.getPostById(postId)
                if(res != null) {
                    user = getUserById(res.userId as String) as User
                    motorbike = getMotorbikeById(res.motorbikeId as String) as Motorbike
                    post = res
                }
            }
        }
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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
        Image(
            painter = painterResource(id = R.drawable.fullthrottle_logo_light),
            contentDescription = "post image",
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)

        )
        Text(
            text = "${post.title}",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Piace a ${post.likesNumber} riders",
            fontWeight = FontWeight.Thin
        )
        Text(text = "Moto: ${motorbike.brand} ${motorbike.model}")
        Text(text = "Lunghezza percorso: ${post.length}km")
        Text(text = "${post.description}")
    }
}