package com.example.fullthrottle.data

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_IMAGE_KEY
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first

object DBHelper {
    private val database = Firebase.database
    private val ref = database.reference

    fun getUserRef(uid: String) {
        lateinit var res: String // TODO: return type
        ref
            .child("users")
            .orderByKey()
            .equalTo(uid)
            .get()
            .addOnSuccessListener {
                /* TODO */
            }.addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        //return res
    }

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
                    //Log.d("User found", it.children.first().child("Mail").value.toString())
                    //Log.d("Pw correct", BCrypt.verifyer().verify(password.toCharArray(), user?.password).verified.toString())
                    if (BCrypt.verifyer().verify(password.toCharArray(), user?.password).verified) {
                        res = true
                        settingsViewModel.saveData(USER_ID_KEY, user?.userId.toString())
                        settingsViewModel.saveData(USERNAME_KEY, user?.username.toString())
                        settingsViewModel.saveData(USER_IMAGE_KEY, user?.userImg.toString())
                    }
                }
                trySend(res)
            }
            .addOnFailureListener{ error ->
                Log.d("Error getting data", error.toString())
            }
        awaitClose { }
    }.first()
}