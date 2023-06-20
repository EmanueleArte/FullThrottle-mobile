package com.example.fullthrottle.data

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.getPostsByUserId
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DBHelper.notifyComment
import com.example.fullthrottle.data.DBHelper.notifyFollow
import com.example.fullthrottle.data.DBHelper.notifyLike
import com.example.fullthrottle.data.DataStoreConstants.PUSH_NOTIFICATIONS_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.entities.*
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.random.Random

@Composable
private fun createNotificationChannels() {
    val notificationManager: NotificationManager =
        LocalContext.current.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // LIKES
    val likeChannel = NotificationChannel(
        stringResource(id = R.string.likes_notifications_channel_id),
        stringResource(id = R.string.likes_notifications_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = stringResource(id = R.string.likes_notifications_channel_description)
    }
    notificationManager.createNotificationChannel(likeChannel)

    // COMMENTS
    val commentsChannel = NotificationChannel(
        stringResource(id = R.string.comments_notifications_channel_id),
        stringResource(id = R.string.comments_notifications_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = stringResource(id = R.string.comments_notifications_channel_description)
    }
    notificationManager.createNotificationChannel(commentsChannel)

    // FOLLOWERS
    val followersChannel = NotificationChannel(
        stringResource(id = R.string.followers_notifications_channel_id),
        stringResource(id = R.string.followers_notifications_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = stringResource(id = R.string.followers_notifications_channel_description)
    }
    notificationManager.createNotificationChannel(followersChannel)
}

@Composable
fun NotificationsHandler (
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())

    createNotificationChannels()

    val likeListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var likes = dataSnapshot.children.map { like -> like.getValue<Like>() }
            val userId = settings[USER_ID_KEY].toString()
            var myPosts = emptyList<Post>()
            CoroutineScope(EmptyCoroutineContext).launch {
                myPosts = getPostsByUserId(userId)
            }.invokeOnCompletion {
                likes = likes.filter { like -> like?.postId in myPosts.map { post -> post.postId } }
                likes = likes.filter { like -> like?.userId != userId }
                likes = likes.filter { like -> like?.notified == "0" }
                likes.forEach { like ->
                    if (like?.notified == "0") {
                        notifyLike(like.likeId.toString())
                        if (settings[PUSH_NOTIFICATIONS_KEY] == PushNotificationConstants.ALL_NOTIFICATIONS
                            || settings[PUSH_NOTIFICATIONS_KEY] == PushNotificationConstants.POSTS_NOTIFICATIONS) {
                            sendLikeNotification(like, context)
                        }
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("Error", databaseError.toString())
        }
    }

    val commentListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var comments = dataSnapshot.children.map { comment -> comment.getValue<Comment>() }
            val userId = settings[USER_ID_KEY].toString()
            var myPosts = emptyList<Post>()
            CoroutineScope(EmptyCoroutineContext).launch {
                myPosts = getPostsByUserId(userId)
            }.invokeOnCompletion {
                comments = comments.filter { comment -> comment?.postId in myPosts.map { post -> post.postId } }
                comments = comments.filter { comment -> comment?.userId != userId }
                comments = comments.filter { comment -> comment?.notified == "0" }
                comments.forEach { comment ->
                    if (comment?.notified == "0") {
                        notifyComment(comment.commentId.toString())
                        if (settings[PUSH_NOTIFICATIONS_KEY] == PushNotificationConstants.ALL_NOTIFICATIONS
                            || settings[PUSH_NOTIFICATIONS_KEY] == PushNotificationConstants.POSTS_NOTIFICATIONS) {
                            sendCommentNotification(comment, context)
                        }
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("Error", databaseError.toString())
        }
    }

    val followerListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var follows = dataSnapshot.children.map { follow -> follow.getValue<Follow>() }
            val userId = settings[USER_ID_KEY].toString()
            follows = follows.filter { follow -> follow?.followedId == userId }
            follows = follows.filter { follow -> follow?.notified == "0" }
            follows.forEach { follow ->
                if (follow?.notified == "0") {
                    notifyFollow(follow.followId.toString())
                    if (settings[PUSH_NOTIFICATIONS_KEY] == PushNotificationConstants.ALL_NOTIFICATIONS
                        || settings[PUSH_NOTIFICATIONS_KEY] == PushNotificationConstants.FOLLOWERS_NOTIFICATIONS) {
                        sendFollowNotification(follow, context)
                    }
                }
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("Error", databaseError.toString())
        }
    }

    Firebase.database.getReference("likes").addValueEventListener(likeListener)
    Firebase.database.getReference("comments").addValueEventListener(commentListener)
    Firebase.database.getReference("follows").addValueEventListener(followerListener)
}

@SuppressLint("MissingPermission")
private fun sendLikeNotification (like: Like, context: Context) {
    var user: User? = null
    CoroutineScope(EmptyCoroutineContext).launch {
        user = getUserById(like.userId.toString())
    }.invokeOnCompletion {
        val builder = NotificationCompat.Builder(context, "FullThrottleLikesNotificationsChannel")
            .setSmallIcon(R.drawable.fullthrottle_logo_light)
            .setContentTitle("Nuovo like ricevuto")
            .setContentText(user?.username + " ha messo mi piace al tuo post")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            notify(Random(like.userId.hashCode() + like.postId.hashCode()).nextInt(), builder.build())
        }
    }
}

@SuppressLint("MissingPermission")
private fun sendCommentNotification (comment: Comment, context: Context) {
    var user: User? = null
    CoroutineScope(EmptyCoroutineContext).launch {
        user = getUserById(comment.userId.toString())
    }.invokeOnCompletion {
        val builder = NotificationCompat.Builder(context, "FullThrottleCommentsNotificationsChannel")
            .setSmallIcon(R.drawable.fullthrottle_logo_light)
            .setContentTitle("Nuovo commento")
            .setContentText(user?.username + ": " + comment.text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            notify(Random(comment.userId.hashCode() + comment.text.hashCode()).nextInt() + comment.postId.hashCode(), builder.build())
        }
    }
}

@SuppressLint("MissingPermission")
private fun sendFollowNotification (follow: Follow, context: Context) {
    var user: User? = null
    CoroutineScope(EmptyCoroutineContext).launch {
        user = getUserById(follow.followerId.toString())
    }.invokeOnCompletion {
        val builder = NotificationCompat.Builder(context, "FullThrottleCommentsNotificationsChannel")
            .setSmallIcon(R.drawable.fullthrottle_logo_light)
            .setContentTitle("Nuovo follower")
            .setContentText(user?.username + " ha iniziato a seguirti")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            notify(Random(user?.userId.hashCode()).nextInt(), builder.build())
        }
    }
}