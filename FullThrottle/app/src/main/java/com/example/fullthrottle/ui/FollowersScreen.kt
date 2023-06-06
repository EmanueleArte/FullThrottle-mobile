package com.example.fullthrottle.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.getFolloweds
import com.example.fullthrottle.data.DBHelper.getFollowers
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.TabConstants.FOLLOWED_TAB
import com.example.fullthrottle.data.TabConstants.FOLLOWERS_TAB
import com.example.fullthrottle.data.entities.User
import kotlinx.coroutines.async

@Composable
fun FollowersScreen(
    uid: String,
    currentTab: Int = FOLLOWERS_TAB,
    goToUserProfile: (String) -> Unit
) {
    var tabIndex by remember { mutableStateOf(currentTab) }

    val tabs = listOf(
        stringResource(id = R.string.followers_tab_label),
        stringResource(id = R.string.followed_tab_label)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                )
            }
        }
        when (tabIndex) {
            FOLLOWERS_TAB -> UsersList(uid, FOLLOWERS_TAB, goToUserProfile)
            FOLLOWED_TAB -> UsersList(uid, FOLLOWED_TAB, goToUserProfile)
        }
    }
}

@Composable
fun UsersList(
    uid: String,
    currentTab: Int,
    goToUserProfile: (String) -> Unit
) {
    var users by remember { mutableStateOf(emptyList<User>()) }
    var imagesUris by remember { mutableStateOf(mutableListOf<Uri>()) }
    LaunchedEffect(key1 = "followersQuery") {
        async {
            users = if (currentTab == FOLLOWERS_TAB) {
                getFollowers(uid)
            } else {
                getFolloweds(uid)
            }
            users.forEach {
                val imageUrl = it.userId + "/" + it.userImg
                imagesUris.add(getImageUri(imageUrl))
            }
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(horizontal = 10.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp),
    ) {
        users.forEachIndexed { index, user ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            goToUserProfile(user.userId.toString())
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
                                imgUri = imagesUris.getOrElse(index) { Uri.EMPTY },
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
                /*ItemTonalButton(value = user.username.toString(),
                    onClick = { goToUserProfile(user.userId.toString()) },
                    imgUri = imagesUris.getOrElse(index) { Uri.EMPTY }
                )*/
            }
        }
    }
}