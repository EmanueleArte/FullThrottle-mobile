package com.example.fullthrottle.data

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import at.favre.lib.crypto.bcrypt.BCrypt
import coil.compose.rememberAsyncImagePainter
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.*

object DBHelper {
    private val database = Firebase.database
    private val storage = Firebase.storage

    suspend fun userLogin(username: String, password: String, settingsViewModel: SettingsViewModel) = callbackFlow {
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
                        settingsViewModel.saveData(USERNAME_KEY, user?.username.toString())
                        settingsViewModel.saveData(USER_IMAGE_KEY, user?.userImg.toString())
                        res = true
                    }
                }
                trySend(res)
            }
            .addOnFailureListener{ error ->
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
            .addOnFailureListener{ error ->
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
            .addOnFailureListener{ error ->
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
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()

    suspend fun userRegistration(username: String, mail: String, password: String, settingsViewModel: SettingsViewModel) = callbackFlow {
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
                password = BCrypt.with(BCrypt.Version.VERSION_2Y).hashToString(6, password.toCharArray()),
                mail = mail,
                followers = "0",
                followed = "0",
                userImg = "",
                informed = "1"
            )
            database.getReference("users").child(user.userId.toString()).setValue(user)
            settingsViewModel.saveData(USER_ID_KEY, user.userId.toString())
            settingsViewModel.saveData(USERNAME_KEY, user.username.toString())
            settingsViewModel.saveData(USER_IMAGE_KEY, user.userImg.toString())
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

    suspend fun getImage(img: String) = callbackFlow {

        /*var imageUrl by rememberSaveable { mutableStateOf<Uri>(Uri.EMPTY) }
        var paint = rememberAsyncImagePainter(model = imageUrl)
        val coroutineScope = rememberCoroutineScope()
        coroutineScope.async {
            imageUrl = DBHelper.imgProva()
        }
        if (imageUrl != Uri.EMPTY) {
            Image(
                painter = paint,
                contentDescription = "",
                modifier = Modifier.size(50.dp).clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }*/

        storage.reference
            .child(img)
            .downloadUrl
            .addOnSuccessListener { imgUri ->
                trySend(imgUri)
            }.addOnFailureListener { error ->
                Log.d("Error getting image", error.toString())
            }
        awaitClose { }
    }.first()
}