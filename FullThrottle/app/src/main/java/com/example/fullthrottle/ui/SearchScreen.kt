package com.example.fullthrottle.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getRecentPosts
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    goToPost: (String) -> Unit,
    goToProfile: (String) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var postResults by remember { mutableStateOf(emptyList<Post>()) }
    var postImagesUris by remember { mutableStateOf(emptyList<Uri>()) }
    var userResults by remember { mutableStateOf(emptyList<User>()) }
    var userImagesUris by remember { mutableStateOf(emptyList<Uri>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        SearchTextField(
            label = "Cerca post e riders",
            onValueChange = fun(text: String) {
                if(text.length >= 3) {
                    coroutineScope.launch {
                        val tPosts = getRecentPosts()
                        postImagesUris = tPosts.map { post ->
                            getImageUri(post.userId + "/" + post.postImg.toString())
                        }
                        postResults = tPosts
                        val tUsers = postResults.map { post ->
                            getUserById(post.userId.toString()) as User
                        }
                        userImagesUris = tUsers.map { user ->
                            if (user.userImg.toString().isNotEmpty())
                                getImageUri(user.userId + "/" + user.userImg)
                            else
                                Uri.EMPTY
                        }
                        userResults = tUsers
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        if (postResults.isNotEmpty()) {
            Text(
                text = "Post",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 5.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(postResults) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                goToPost(post.postId.toString())
                            },
                        elevation = CardDefaults.cardElevation(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(2.dp)
                            ) {
                                PostImage(
                                    imgUri = postImagesUris[postResults.indexOf(post)],
                                    contentDescription = "post image",
                                    modifier = Modifier
                                        .requiredWidth(98.dp)
                                        .requiredHeight(56.dp)
                                        .clip(RoundedCornerShape(UiConstants.CORNER_RADIUS))
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
        if (postResults.isNotEmpty()) {
            Text(
                text = "Riders",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 5.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(userResults) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                goToProfile(user.userId.toString())
                            },
                        elevation = CardDefaults.cardElevation(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(2.dp).height(IntrinsicSize.Max)
                        ) {
                            Column(
                                modifier = Modifier.padding(2.dp)
                            ) {
                                ProfileImage(
                                    imgUri = userImagesUris[userResults.indexOf(user)],
                                    contentDescription = "user image",
                                    modifier = Modifier
                                        .requiredWidth(40.dp)
                                        .requiredHeight(40.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = user.username.toString(),
                                    modifier = Modifier.padding(PaddingValues(start = 5.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}