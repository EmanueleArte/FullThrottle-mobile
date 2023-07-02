@file:Suppress("RemoveExplicitTypeArguments")

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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DBHelper {
    private val database = Firebase.database
    private val storage = Firebase.storage

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    // USERS
    suspend fun getAllUsers(): List<User> = callbackFlow {
        database
            .getReference("users")
            .get()
            .addOnSuccessListener { users ->
                if (users.exists()) {
                    trySend(users.children.map { user -> user.getValue<User>() as User })
                } else {
                    trySend(emptyList<User>())
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

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

    suspend fun searchUsers(text: String): List<User> = callbackFlow {
        database
            .getReference("users")
            .get()
            .addOnSuccessListener { users ->
                if (users.exists()) {
                    val res = users.children
                        .map { user -> user.getValue<User>() as User }
                        .filter { user -> user.username.toString().contains(text, true) }
                    trySend(res)
                } else {
                    trySend(emptyList<User>())
                }
            }
            .addOnFailureListener{ error ->
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

    fun followUser(uidToFollow: String, uid: String): Boolean {
        val follow = Follow(
            followId = UUID.randomUUID().toString(),
            followedId = uidToFollow,
            followerId = uid,
            notified = "0"
        )
        var success = true

        database
            .getReference("follows")
            .child(follow.followId.toString())
            .setValue(follow)
            .addOnFailureListener { error ->
                Log.d("Error creating follow", error.toString())
                success = false
            }

        if (success) {
            database
                .getReference("users")
                .orderByKey()
                .equalTo(uidToFollow)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user = it.children.first().getValue<User>()
                        val newFollowers = user?.followers?.toInt()?.plus(1)
                        val updates: MutableMap<String, Any> = hashMapOf(
                            "users/$uidToFollow/followers" to newFollowers.toString(),
                        )
                        database.reference.updateChildren(updates)
                    }
                }
                .addOnFailureListener { error ->
                    Log.d("Error getting data", error.toString())
                    success = false
                }
        }

        if (success) {
            database
                .getReference("users")
                .orderByKey()
                .equalTo(uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user = it.children.first().getValue<User>()
                        val newFolloweds = user?.followed?.toInt()?.plus(1)
                        val updates: MutableMap<String, Any> = hashMapOf(
                            "users/$uid/followed" to newFolloweds.toString(),
                        )
                        database.reference.updateChildren(updates)
                    }
                }
                .addOnFailureListener { error ->
                    Log.d("Error getting data", error.toString())
                    success = false
                }
        }
        return success
    }

    fun unfollowUser(uidToUnfollow: String, uid: String): Boolean {
        var success = true

        database
            .getReference("follows")
            .orderByChild("followedId")
            .equalTo(uidToUnfollow)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val follows = it.children.map { follow -> follow.getValue<Follow>() as Follow }
                    val followId = follows.find { follow -> follow.followerId == uid }?.followId
                    database.getReference("follows").child(followId.orEmpty()).removeValue()
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error getting data", error.toString())
                success = false
            }

        if (success) {
            database
                .getReference("users")
                .orderByKey()
                .equalTo(uidToUnfollow)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user = it.children.first().getValue<User>()
                        val newFollowers = user?.followers?.toInt()?.plus(-1)
                        val updates: MutableMap<String, Any> = hashMapOf(
                            "users/$uidToUnfollow/followers" to newFollowers.toString(),
                        )
                        database.reference.updateChildren(updates)
                    }
                }
                .addOnFailureListener { error ->
                    Log.d("Error getting data", error.toString())
                    success = false
                }
        }

        if (success) {
            database
                .getReference("users")
                .orderByKey()
                .equalTo(uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user = it.children.first().getValue<User>()
                        val newFolloweds = user?.followed?.toInt()?.plus(-1)
                        val updates: MutableMap<String, Any> = hashMapOf(
                            "users/$uid/followed" to newFolloweds.toString(),
                        )
                        database.reference.updateChildren(updates)
                    }
                }
                .addOnFailureListener { error ->
                    Log.d("Error getting data", error.toString())
                    success = false
                }
        }
        return success
    }

    fun notifyFollow(followId: String) {
        database
            .getReference("follows")
            .child(followId)
            .child("notified")
            .setValue("1")
            .addOnFailureListener { error ->
                Log.d("Error notifying like", error.toString())
            }
    }

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
    suspend fun getAllPosts(): List<Post> = callbackFlow {
        database
            .getReference("posts")
            .get()
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

    suspend fun getPostsByUserId(uid: String): List<Post> = callbackFlow {
        database
            .getReference("posts")
            .orderByChild("userId")
            .equalTo(uid)
            .get()
            .addOnSuccessListener { posts ->
                if (posts.exists()) {
                    trySend(posts.children.map { post -> post.getValue<Post>() as Post }.reversed())
                } else {
                    trySend(emptyList<Post>())
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    fun createPost(uid: String, title: String, description: String, postImgUri: Uri, motorbikeId: String, position: String, length: String, lapTime: String = "", categoryId: String = "") {
        val postImageRef = storage.reference.child("$uid/${postImgUri.lastPathSegment}")
        val uploadTask = postImageRef.putFile(postImgUri)

        val currDateTime = LocalDateTime.now().format(formatter)
        val post = Post(
            categoryId = categoryId,
            description = description,
            lapTime = lapTime,
            length = length,
            likesNumber = "0",
            motorbikeId = motorbikeId,
            position = position,
            postId = UUID.randomUUID().toString(),
            postImg = postImgUri.lastPathSegment,
            publishDate = currDateTime.toString(),
            title = title,
            userId = uid
        )
        database
            .getReference("posts")
            .child(post.postId)
            .setValue(post)

        uploadTask.addOnFailureListener {
            Log.d("Error uploading image", it.toString())
        }
    }

    suspend fun getRecentPosts(): List<Post> = callbackFlow {
        database
            .getReference("posts")
            .orderByChild("publishDate")
            .get()
            .addOnSuccessListener { posts ->
                if (posts.exists()) {
                    trySend(posts.children.map { post -> post.getValue<Post>() as Post }.reversed())
                } else {
                    trySend(emptyList<Post>())
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun searchPosts(text: String): List<Post> = callbackFlow {
        database
            .getReference("posts")
            .get()
            .addOnSuccessListener { posts ->
                if (posts.exists()) {
                    val res = posts.children
                        .map { post -> post.getValue<Post>() as Post }
                        .filter { post -> post.title.toString().contains(text, true) }
                    trySend(res)
                } else {
                    trySend(emptyList<Post>())
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun deletePost(postId: String, localDelete: () -> Unit) {
        val post = getPostById(postId)
        database.getReference("posts").child(postId).removeValue()
        storage.reference
            .child("${post?.userId}/${post?.postImg}")
            .delete()
            .addOnSuccessListener {
                GlobalScope.launch(Dispatchers.IO) {
                    localDelete()
                }
            }
            .addOnFailureListener { error ->
                Log.d("Error deleting image", error.toString())
            }
    }

    // MOTORBIKES
    suspend fun getMotorbikeById(motorbikeId: String): Motorbike? = callbackFlow {
        database
            .getReference("motorbikes")
            .orderByChild("motorbikeId")
            .equalTo(motorbikeId)
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

    fun deleteMotorbikeById(motorbikeId: String) {
        database
            .getReference("motorbikes")
            .child(motorbikeId)
            .child("deleted")
            .setValue(true)
            .addOnFailureListener { error ->
                Log.d("Error deleting motorbike", error.toString())
            }
    }

    fun addMotorbike(uid: String, brand: String, model: String, productionYear: String) {
        val newMotorbike = Motorbike(
            motorbikeId = UUID.randomUUID().toString(),
            brand = brand,
            model = model,
            productionYear = productionYear,
            userId = uid,
            deleted = false
        )
        database.getReference("motorbikes")
            .child(newMotorbike.motorbikeId.toString())
            .setValue(newMotorbike)
    }

    // COMMENTS
    suspend fun getCommentsByPostId(postId: String): List<Comment> = callbackFlow {
        database
            .getReference("comments")
            .orderByChild("postId")
            .equalTo(postId)
            .get()
            .addOnSuccessListener { comments ->
                if (comments.exists()) {
                    trySend(comments.children.map { it.getValue<Comment>() as Comment }.reversed())
                } else {
                    trySend(emptyList<Comment>())
                }
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    fun publishComment(postId: String, uid: String, text: String) {
        val comment = Comment(
            commentId = UUID.randomUUID().toString(),
            notified = "0",
            postId = postId,
            publishDate = LocalDateTime.now().format(formatter),
            text = text,
            userId = uid
        )
        database
            .getReference("comments")
            .child(comment.commentId.toString())
            .setValue(comment)
            .addOnFailureListener{ error ->
                Log.d("Error publishing comment", error.toString())
            }
    }

    fun notifyComment(commentId: String) {
        database
            .getReference("comments")
            .child(commentId)
            .child("notified")
            .setValue("1")
            .addOnFailureListener { error ->
                Log.d("Error notifying like", error.toString())
            }
    }

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
                    var like = likes.find { like -> like?.userId == userId }
                    if (like != null) {
                        database
                            .getReference("likes")
                            .child(like.likeId.toString())
                            .removeValue()
                        database
                            .getReference("posts")
                            .child(postId)
                            .child("likesNumber")
                            .setValue((likes.size - 1).toString())
                        trySend(false)
                    } else {
                        like = Like(
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

    fun notifyLike(likeId: String) {
        database
            .getReference("likes")
            .child(likeId)
            .child("notified")
            .setValue("1")
            .addOnFailureListener { error ->
                Log.d("Error notifying like", error.toString())
            }
    }

    // LOCATIONS
    suspend fun getPostsLocations(): Map<String, List<Post>> = callbackFlow {
        database
            .getReference("posts")
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val posts = it.children.map { post -> post.getValue<Post>() as Post }
                    val locations = mutableMapOf<String, List<Post>>()
                    posts.forEach { post ->
                        locations[post.position.toString()] = if (locations.containsKey(post.position)) {
                            locations[post.position]?.plus(post) as List<Post>
                        } else {
                            listOf(post)
                        }
                    }
                    trySend(locations)
                } else {
                    trySend(emptyMap<String, List<Post>>())
                }
            }
            .addOnFailureListener { error ->
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

    fun changeProfileImage(uid: String, profileImageUri: Uri) {
        val postImageRef = storage.reference.child("$uid/${profileImageUri.lastPathSegment}")
        val uploadTask = postImageRef.putFile(profileImageUri)

        database
            .getReference("users")
            .child(uid)
            .child("userImg")
            .setValue(profileImageUri.lastPathSegment.toString())

        uploadTask.addOnFailureListener {
            Log.d("Error uploading image", it.toString())
        }
    }

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