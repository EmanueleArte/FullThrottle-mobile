package com.example.fullthrottle.data

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object DBHelper {
    val database = Firebase.database
    val ref = database.reference

    fun getUserRef(uid: String) {
        lateinit var res: String // TODO: return type
        DBHelper.ref
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
}