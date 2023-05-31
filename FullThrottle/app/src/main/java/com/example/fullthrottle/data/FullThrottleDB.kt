package com.example.fullthrottle.data

import android.net.Uri
import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DataStoreConstants.MAIL_KEY
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
import com.example.fullthrottle.data.entities.*
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*

object DBHelper {
    private val database = Firebase.database
    private val storage = Firebase.storage

    // USERS
    suspend fun getUserByUsername(username: String): User? = callbackFlow {
        database
            .getReference("users")
            .orderByChild("username")
            .equalTo(username)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    trySend(it.children.first().getValue<User>())
                } else {
                    trySend(null)
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun getUserByMail(mail: String): User? = callbackFlow {
        database
            .getReference("users")
            .orderByChild("mail")
            .equalTo(mail)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    trySend(it.children.first().getValue<User>())
                } else {
                    trySend(null)
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun getUserById(uid: String): User? = callbackFlow {
        database
            .getReference("users")
            .orderByKey()
            .equalTo(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    trySend(it.children.first().getValue<User>())
                } else {
                    trySend(null)
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun getFollowers(uid: String): List<User> = callbackFlow {
        database
            .getReference("follows")
            .orderByChild("followedId")
            .equalTo(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val followers = mutableListOf<User>()
                    it.children.map { follow ->
                        follow.child("followerId").value.toString()
                    }.forEach { followerId ->
                        database
                            .getReference("users")
                            .orderByKey()
                            .equalTo(followerId)
                            .get()
                            .addOnSuccessListener { follower ->
                                if (follower.exists()) {
                                    followers.add(
                                        follower.children.first().getValue<User>() as User
                                    )
                                }
                                trySend(followers)
                            }
                            .addOnFailureListener { error ->
                                Log.d("Error getting data", error.toString())
                            }
                    }
                } else {
                    trySend(emptyList<User>())
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun getFolloweds(uid: String): List<User> = callbackFlow {
        database
            .getReference("follows")
            .orderByChild("followerId")
            .equalTo(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val followers = mutableListOf<User>()
                    it.children.map { follow ->
                        follow.child("followedId").value.toString()
                    }.forEach { followerId ->
                        database
                            .getReference("users")
                            .orderByKey()
                            .equalTo(followerId)
                            .get()
                            .addOnSuccessListener { followed ->
                                if (followed.exists()) {
                                    followers.add(
                                        followed.children.first().getValue<User>() as User
                                    )
                                }
                                trySend(followers)
                            }
                            .addOnFailureListener { error ->
                                Log.d("Error getting data", error.toString())
                            }
                    }
                } else {
                    trySend(emptyList<User>())
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun userLogin(
        username: String,
        password: String,
        settingsViewModel: SettingsViewModel
    ): Boolean = callbackFlow {
        var res = false
        database
            .getReference("users")
            .orderByChild("username")
            .equalTo(username)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.children.first().getValue<User>()
                    if (BCrypt.verifyer().verify(password.toCharArray(), user?.password).verified) {
                        settingsViewModel.saveData(USER_ID_KEY, user?.userId.toString())
                        settingsViewModel.saveData(USERNAME_KEY, user?.username.orEmpty())
                        settingsViewModel.saveData(USER_IMAGE_KEY, user?.userImg.orEmpty())
                        settingsViewModel.saveData(MAIL_KEY, user?.mail.orEmpty())
                        res = true
                    }
                }
                trySend(res)
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun userRegistration(
        username: String,
        mail: String,
        password: String,
        settingsViewModel: SettingsViewModel
    ): Int = callbackFlow {
        var usernameUser: User?
        var mailUser: User?
        withContext(Dispatchers.Default) {
            usernameUser = getUserByUsername(username)
            mailUser = getUserByMail(mail)
        }
        if (usernameUser == null && mailUser == null) {
            val user = User(
                userId = UUID.randomUUID().toString(),
                username = username,
                password = BCrypt.with(BCrypt.Version.VERSION_2Y)
                    .hashToString(6, password.toCharArray()),
                mail = mail,
                followers = "0",
                followed = "0",
                userImg = "",
                informed = "1"
            )
            database.getReference("users").child(user.userId.toString()).setValue(user)
            settingsViewModel.saveData(USER_ID_KEY, user.userId.orEmpty())
            settingsViewModel.saveData(USERNAME_KEY, user.username.orEmpty())
            settingsViewModel.saveData(USER_IMAGE_KEY, user.userImg.orEmpty())
            settingsViewModel.saveData(MAIL_KEY, user.mail.orEmpty())
            trySend(-1)
        } else {
            if (usernameUser != null) {
                trySend(R.string.username_used)
            } else {
                trySend(R.string.mail_used)
            }
        }
        awaitClose { }
    }.first()

    // POSTS
    suspend fun getPostById(postId: String): Post? = callbackFlow {
        database
            .getReference("posts")
            .orderByChild("postId")
            .equalTo(postId)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    trySend(it.children.first().getValue<Post>())
                } else {
                    trySend(null)
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun getRecentPosts(): List<Post> = callbackFlow {
        database
            .getReference("posts")
            .orderByChild("publishDate")
            .get()
            .addOnSuccessListener { posts ->
                if (posts.exists()) {
                    val res = posts.children.map { post -> post.getValue<Post>() as Post }
                    trySend(res.reversed())
                } else {
                    trySend(emptyList<Post>())
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun getPostsByUserId(uid: String): List<Post> = callbackFlow {
        database
            .getReference("posts")
            .orderByChild("userId")
            .equalTo(uid).get()
            .addOnSuccessListener { posts ->
                if (posts.exists()) {
                    trySend(posts.children.map { post -> post.getValue<Post>() as Post })
                } else {
                    trySend(emptyList<Post>())
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    // MOTORBIKES
    suspend fun getMotorbikeById(uid: String): Motorbike? = callbackFlow {
        database
            .getReference("motorbikes")
            .orderByChild("motorbikeId")
            .equalTo(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    trySend(it.children.first().getValue<Motorbike>())
                } else {
                    trySend(null)
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun getMotorbikesByUserId(uid: String): List<Motorbike> = callbackFlow {
        database
            .getReference("motorbikes")
            .orderByChild("userId")
            .equalTo(uid)
            .get()
            .addOnSuccessListener { motorbikes ->
                if (motorbikes.exists()) {
                    trySend(motorbikes.children.map { it.getValue<Motorbike>() as Motorbike })
                } else {
                    trySend(emptyList<Motorbike>())
                }
            }.addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    // COMMENTS
    suspend fun getCommentsByPostId(postId: String): List<Comment> = callbackFlow {
        database
            .getReference("comments")
            .orderByChild("postId")
            .equalTo(postId)
            .get()
            .addOnSuccessListener { comments ->
                if (comments.exists()) {
                    trySend(comments.children.map { it.getValue<Comment>() as Comment })
                } else {
                    trySend(emptyList<Comment>())
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    // LIKES
    suspend fun checkLike(postId: String, userId: String): Boolean = callbackFlow{
        database
            .getReference("likes")
            .orderByChild("postId")
            .equalTo(postId)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val likes = it.children.map { like -> like.getValue<Like>() }
                    val like = likes.find { like -> like?.userId == userId }
                    trySend(like != null)
                } else {
                    trySend(false)
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun toggleLike(postId: String, userId: String): Boolean = callbackFlow {
        database
            .getReference("likes")
            .orderByChild("postId")
            .equalTo(postId)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val likes = it.children.map { like -> like.getValue<Like>()  }
                    val like = likes.find { like -> like?.userId == userId }
                    if (like != null) {
                        database
                            .getReference("likes")
                            .child(like?.likeId.toString())
                            .removeValue()
                        database
                            .getReference("posts")
                            .child(postId)
                            .child("likesNumber")
                            .setValue((likes.size - 1).toString())
                        trySend(false)
                    } else {
                        val like = Like(
                            likeId = UUID.randomUUID().toString(),
                            notified = "0",
                            postId,
                            userId
                        )
                        database
                            .getReference("likes").child(like.likeId.toString()).setValue(like)
                        database
                            .getReference("posts")
                            .child(postId)
                            .child("likesNumber")
                            .setValue((likes.size + 1).toString())
                        trySend(true)
                    }
                } else {
                    val like = Like(
                        likeId = UUID.randomUUID().toString(),
                        notified = "0",
                        postId,
                        userId
                    )
                    database
                        .getReference("likes").child(like.likeId.toString()).setValue(like)
                    database
                        .getReference("posts")
                        .child(postId)
                        .child("likesNumber")
                        .setValue(1.toString())
                    trySend(true)
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    // IMAGES
    suspend fun getImageUri(imgUrl: String): Uri = callbackFlow {
        storage.reference
            .child(imgUrl)
            .downloadUrl
            .addOnSuccessListener { imgUri ->
                trySend(imgUri)
            }.addOnFailureListener { error ->
                Log.d("Error getting image", error.toString())
            }
        awaitClose { }
    }.first()

    // USER DATA
    fun updateUsername(
        uid: String,
        newUsername: String,
        settingsViewModel: SettingsViewModel
    ) {
        if (newUsername != "") {
            database
                .getReference("users")
                .child(uid)
                .child("username")
                .setValue(newUsername)
                .addOnSuccessListener {
                    settingsViewModel.saveData(USERNAME_KEY, newUsername)
                }
                .addOnFailureListener { error ->
                    Log.d("Error updating username", error.toString())
                }
        }
    }

    fun updateMail(
        uid: String,
        newMail: String,
        settingsViewModel: SettingsViewModel
    ) {
        if (newMail != "") {
            database
                .getReference("users")
                .child(uid)
                .child("mail")
                .setValue(newMail)
                .addOnSuccessListener {
                    settingsViewModel.saveData(MAIL_KEY, newMail)
                }
                .addOnFailureListener { error ->
                    Log.d("Error updating mail", error.toString())
                }
        }
    }

    suspend fun checkPassword(
        uid: String,
        password: String,
    ): Boolean = callbackFlow {
        var res = false
        database
            .getReference("users")
            .orderByChild("userId")
            .equalTo(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.children.first().getValue<User>()
                    if (BCrypt.verifyer().verify(password.toCharArray(), user?.password).verified) {
                        res = true
                    }
                }
                trySend(res)
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    fun updatePassword(
        uid: String,
        newPassword: String,
    ) {
        if (newPassword != "") {
            database
                .getReference("users")
                .child(uid)
                .child("password")
                .setValue(
                    BCrypt.with(BCrypt.Version.VERSION_2Y)
                        .hashToString(6, newPassword.toCharArray())
                )
                .addOnFailureListener { error ->
                    Log.d("Error updating password", error.toString())
                }
        }
    }

}