package com.example.fullthrottle.ui

import androidx.compose.runtime.*
import com.example.fullthrottle.data.entities.Comment
import com.example.fullthrottle.data.entities.Follow
import com.example.fullthrottle.data.entities.Like

@Composable
fun NotificationsScreen(
    goToPost: (String) -> Unit,
    goToProfile: (String) -> Unit,
) {
    var likeNotifications by remember { mutableStateOf(emptyList<Like>()) }
    var commentNotifications by remember { mutableStateOf(emptyList<Comment>()) }
    var followNotifications by remember { mutableStateOf(emptyList<Follow>()) }


}