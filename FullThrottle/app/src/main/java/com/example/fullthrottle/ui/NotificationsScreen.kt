package com.example.fullthrottle.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.data.LocalDbViewModel
import com.example.fullthrottle.data.entities.CommentNotification
import com.example.fullthrottle.data.entities.FollowNotification
import com.example.fullthrottle.data.entities.LikeNotification

@Composable
fun NotificationsScreen(
    goToPost: (String) -> Unit,
    goToProfile: (String) -> Unit,
    localDbViewModel: LocalDbViewModel
) {
    var likesNotifications by remember { mutableStateOf(emptyList<LikeNotification>()) }
    var commentsNotifications by remember { mutableStateOf(emptyList<CommentNotification>()) }
    var followNotifications by remember { mutableStateOf(emptyList<FollowNotification>()) }

    commentsNotifications = localDbViewModel.commentsNotifications.collectAsState(initial = listOf()).value
    likesNotifications = localDbViewModel.likesNotifications.collectAsState(initial = listOf()).value
    followNotifications = localDbViewModel.followsNotifications.collectAsState(initial = listOf()).value

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        if (followNotifications.isNotEmpty()) {
            Column(Modifier.heightIn(0.dp, 200.dp)) {
                SimpleTitle(text = "Nuovi followers")
                LazyColumn {
                    items(followNotifications) { follow ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp)
                                .clickable { goToProfile(follow.followerId.toString()) },
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(follow.username.toString())
                                    }
                                    append(" ha iniziato a seguirti")
                                },
                                Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
        if (likesNotifications.isNotEmpty()) {
            Column(Modifier.heightIn(0.dp, 200.dp)) {
                SimpleTitle(text = "Likes")
                LazyColumn {
                    items(likesNotifications) { like ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp)
                                .clickable { goToPost(like.postId.toString()) },
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(like.username.toString())
                                    }
                                    append(" ha messo like al tuo post")
                                },
                                Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
        if (commentsNotifications.isNotEmpty()) {
            Column(Modifier.heightIn(0.dp, 250.dp)) {
                SimpleTitle(text = "Commenti")
                LazyColumn {
                    items(commentsNotifications) { comment ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clickable { goToPost(comment.postId.toString()) },
                            elevation = CardDefaults.cardElevation(5.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(comment.username.toString())
                                    }
                                    append(" ha commentato il tuo post:")
                                },
                                Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                            Text(
                                text = comment.text.toString(),
                                Modifier.padding(start = 10.dp, end = 10.dp, top = 0.dp, bottom = 5.dp)
                            )
                        }
                    }
                }
            }
        }
        if (followNotifications.isEmpty() && likesNotifications.isEmpty() && commentsNotifications.isEmpty()) {
            Column(Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))
                Row(Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    SimpleTitle(text = "Non ci sono notifiche")
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}