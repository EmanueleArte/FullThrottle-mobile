package com.example.fullthrottle.ui

import android.annotation.SuppressLint
import android.text.style.LineHeightSpan
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.BottomAppBarFunction
import com.example.fullthrottle.R
import com.example.fullthrottle.TopAppBarFunction
import com.example.fullthrottle.data.DBHelper
import com.example.fullthrottle.data.DBHelper.getRecentPosts
import com.example.fullthrottle.data.DataStoreConstants
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.Post
import kotlinx.coroutines.async

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    var posts by remember { mutableStateOf(emptyList<Post>()) }
    LaunchedEffect(
        key1 = "posts",
        block = {
            async {
                posts = getRecentPosts() as List<Post>
            }
        }
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(posts) { post ->
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column {
                    Row {
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
                                text = "Rider ${post.userId}",
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
                    Text(text = "Moto: ")
                    Text(text = "Lunghezza percorso: ${post.length}")
                    Text(text = "${post.description}")
                }
            }
        }
    }
}