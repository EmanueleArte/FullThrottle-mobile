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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.searchPosts
import com.example.fullthrottle.data.DBHelper.searchUsers
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    goToPost: (String) -> Unit,
    goToProfile: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    var postResults by remember { mutableStateOf(emptyList<Post>()) }
    var postImagesUris by remember { mutableStateOf(emptyList<Uri>()) }
    var userResults by remember { mutableStateOf(emptyList<User>()) }
    var userImagesUris by remember { mutableStateOf(emptyList<Uri>()) }
    var searching by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        searchTextField(
            label = stringResource(id = R.string.search_label),
            onValueChange = fun(text: String) {
                if(text.length >= 3) {
                    coroutineScope.launch {
                        searching++
                        val tPosts = searchPosts(text)
                        postImagesUris = tPosts.map { post ->
                            getImageUri(post.userId + "/" + post.postImg.toString())
                        }
                        postResults = tPosts
                        val tUsers = searchUsers(text)
                        userImagesUris = tUsers.map { user ->
                            if (user.userImg.orEmpty().isNotEmpty())
                                getImageUri(user.userId + "/" + user.userImg)
                            else
                                Uri.EMPTY
                        }
                        userResults = tUsers
                    }.invokeOnCompletion { searching-- }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        if (searching > 0) LoadingAnimation()
        else {
            if (postResults.isNotEmpty()) {
                Spacer(modifier = Modifier.size(10.dp))

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
                                            .requiredWidth(71.dp)
                                            .requiredHeight(40.dp)
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
            if (userResults.isNotEmpty()) {
                Spacer(modifier = Modifier.size(10.dp))

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
                                modifier = Modifier
                                    .padding(2.dp)
                                    .height(IntrinsicSize.Max)
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
}