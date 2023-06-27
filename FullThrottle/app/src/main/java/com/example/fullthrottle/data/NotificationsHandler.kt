package com.example.fullthrottle.data

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.fullthrottle.MainActivity
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.getPostsByUserId
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DBHelper.notifyComment
import com.example.fullthrottle.data.DBHelper.notifyFollow
import com.example.fullthrottle.data.DBHelper.notifyLike
import com.example.fullthrottle.data.DataStoreConstants.PUSH_NOTIFICATIONS_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.PushNotificationValues.commentsNotificationsChannelDescription
import com.example.fullthrottle.data.PushNotificationValues.commentsNotificationsChannelId
import com.example.fullthrottle.data.PushNotificationValues.commentsNotificationsChannelName
import com.example.fullthrottle.data.PushNotificationValues.followersNotificationsChannelDescription
import com.example.fullthrottle.data.PushNotificationValues.followersNotificationsChannelId
import com.example.fullthrottle.data.PushNotificationValues.followersNotificationsChannelName
import com.example.fullthrottle.data.PushNotificationValues.likesNotificationChannelId
import com.example.fullthrottle.data.PushNotificationValues.likesNotificationsChannelDescription
import com.example.fullthrottle.data.PushNotificationValues.likesNotificationsChannelName
import com.example.fullthrottle.data.PushNotificationValues.localDbViewModel
import com.example.fullthrottle.data.PushNotificationValues.notificationManager
import com.example.fullthrottle.data.entities.*
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

object PushNotificationValues {
    lateinit var localDbViewModel: LocalDbViewModel
    lateinit var notificationManager: NotificationManager
    lateinit var likesNotificationChannelId: String
    lateinit var likesNotificationsChannelName: String
    lateinit var likesNotificationsChannelDescription: String
    lateinit var commentsNotificationsChannelId: String
    lateinit var commentsNotificationsChannelName: String
    lateinit var commentsNotificationsChannelDescription: String
    lateinit var followersNotificationsChannelId: String
    lateinit var followersNotificationsChannelName: String
    lateinit var followersNotificationsChannelDescription: String

    @Composable
    fun SetVariables() {
        localDbViewModel = hiltViewModel()
        notificationManager =
            LocalContext.current.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        likesNotificationChannelId = stringResource(id = R.string.likes_notifications_channel_id)
        likesNotificationsChannelName = stringResource(id = R.string.likes_notifications_channel_name)
        likesNotificationsChannelDescription = stringResource(id = R.string.likes_notifications_channel_description)
        commentsNotificationsChannelId = stringResource(id = R.string.comments_notifications_channel_id)
        commentsNotificationsChannelName = stringResource(id = R.string.comments_notifications_channel_name)
        commentsNotificationsChannelDescription = stringResource(id = R.string.comments_notifications_channel_description)
        followersNotificationsChannelId = stringResource(id = R.string.followers_notifications_channel_id)
        followersNotificationsChannelName = stringResource(id = R.string.followers_notifications_channel_name)
        followersNotificationsChannelDescription = stringResource(id = R.string.followers_notifications_channel_description)
    }
}

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        notificationsHandler(applicationContext, inputData.keyValueMap)
        return Result.success()
    }

}

private fun createNotificationChannels() {
    // LIKES
    val likeChannel = NotificationChannel(
        likesNotificationChannelId,
        likesNotificationsChannelName,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = likesNotificationsChannelDescription
    }
    notificationManager.createNotificationChannel(likeChannel)

    // COMMENTS
    val commentsChannel = NotificationChannel(
        commentsNotificationsChannelId,
        commentsNotificationsChannelName,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = commentsNotificationsChannelDescription
    }
    notificationManager.createNotificationChannel(commentsChannel)

    // FOLLOWERS
    val followersChannel = NotificationChannel(
        followersNotificationsChannelId,
        followersNotificationsChannelName,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = followersNotificationsChannelDescription
    }
    notificationManager.createNotificationChannel(followersChannel)
}

fun notificationsHandler (
    context: Context,
    settings: Map<String, Any>
) {
    val settings = settings as Map<String, String>
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
                            sendCommentNotification(comment, context, localDbViewModel)
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
        val id = Random(like.userId.hashCode() + like.postId.hashCode()).nextInt()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, "FullThrottleLikesNotificationsChannel")
            .setSmallIcon(R.drawable.ic_app_round)
            .setContentTitle("Nuovo like ricevuto")
            .setContentText(user?.username + " ha messo mi piace al tuo post")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
        val likeNotification = LikeNotification(
            id.toString(),
            "0",
            like.postId,
            like.userId,
            user?.username
        )
        localDbViewModel.addNewLikeNotification(likeNotification)
    }
}

@SuppressLint("MissingPermission")
private fun sendCommentNotification (comment: Comment, context: Context, localDbViewModel: LocalDbViewModel) {
    var user: User? = null
    CoroutineScope(EmptyCoroutineContext).launch {
        user = getUserById(comment.userId.toString())
    }.invokeOnCompletion {
        val id = Random(comment.userId.hashCode() + comment.text.hashCode()).nextInt() + comment.postId.hashCode()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, "FullThrottleCommentsNotificationsChannel")
            .setSmallIcon(R.drawable.ic_app_round)
            .setContentTitle("Nuovo commento")
            .setContentText(user?.username + ": " + comment.text)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
        val commentNotification = CommentNotification(
            id.toString(),
            "0",
            comment.postId,
            comment.publishDate,
            comment.text,
            comment.userId,
            user?.username
            )
        localDbViewModel.addNewCommentNotification(commentNotification)
    }
}

@SuppressLint("MissingPermission")
private fun sendFollowNotification (follow: Follow, context: Context) {
    var user: User? = null
    CoroutineScope(EmptyCoroutineContext).launch {
        user = getUserById(follow.followerId.toString())
    }.invokeOnCompletion {
        val id = Random(user?.userId.hashCode()).nextInt()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(context, "FullThrottleCommentsNotificationsChannel")
            .setSmallIcon(R.drawable.ic_app_round)
            .setContentTitle("Nuovo follower")
            .setContentText(user?.username + " ha iniziato a seguirti")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
        val followNotification = FollowNotification(
            id.toString(),
            follow.followedId,
            follow.followerId,
            "0",
            user?.username
        )
        localDbViewModel.addNewFollowNotification(followNotification)
    }
}