package com.example.fullthrottle.data

import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.fullthrottle.data.entities.User
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
            .child("utenti")
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

    suspend fun userLogin(username: String, password: String) = callbackFlow {
        var res = false
        database
            .getReference("utenti")
            .orderByChild("Username")
            .equalTo(username)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val user = it.children.first().getValue<User>()
                    //Log.d("User found", it.children.first().child("Mail").value.toString())
                    Log.d("Pw correct", BCrypt.verifyer().verify(password.toCharArray(), user?.Password).verified.toString())
                    if (BCrypt.verifyer().verify(password.toCharArray(), user?.Password).verified) {
                        res = true
                        /* TODO: save username in datastore */
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