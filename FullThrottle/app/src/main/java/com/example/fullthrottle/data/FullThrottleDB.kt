package com.example.fullthrottle.data

import android.net.Uri
import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DataStoreConstants.MAIL_KEY
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.Motorbike
import com.example.fullthrottle.data.entities.User
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

    suspend fun userLogin(
        username: String,
        password: String,
        settingsViewModel: SettingsViewModel
    ) = callbackFlow {
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

    suspend fun getUserByUsername(username: String) = callbackFlow {
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

    suspend fun getUserByMail(mail: String) = callbackFlow {
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

    suspend fun getUserById(uid: String) = callbackFlow {
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

    suspend fun getFollowers(uid: String) = callbackFlow {
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

    suspend fun getFolloweds(uid: String) = callbackFlow {
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

    suspend fun userRegistration(
        username: String,
        mail: String,
        password: String,
        settingsViewModel: SettingsViewModel
    ) = callbackFlow {
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

    suspend fun getMotorbikeById(uid: String) = callbackFlow {
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

    suspend fun getMotorbikesByUserId(uid: String) = callbackFlow {
        database
            .getReference("motorbikes")
            .orderByChild("userId")
            .equalTo(uid)
            .get()
            .addOnSuccessListener { motorbikes ->
                if (motorbikes.exists()) {
                    trySend(motorbikes.children.map { it.getValue<Motorbike>() })
                } else {
                    trySend(emptyList<Motorbike>())
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun getImageUri(imgUrl: String): Uri = callbackFlow {
        if (imgUrl.last() == '/') {
            trySend(Uri.EMPTY)
        } else {
            storage.reference
                .child(imgUrl)
                .downloadUrl
                .addOnSuccessListener { imgUri ->
                    trySend(imgUri)
                }.addOnFailureListener { error ->
                    Log.d("Error getting image", error.toString())
                }
        }
        awaitClose { }
    }.first()

    suspend fun getRecentPosts(num: Int = 10) = callbackFlow {
        database
            .getReference("posts")
            .get()
            .addOnSuccessListener { posts ->
                if (posts.exists()) {
                    trySend(posts.children.map { post -> post.getValue<Post>() })
                } else {
                    trySend(emptyList<Post>())
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

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
    ) = callbackFlow {
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