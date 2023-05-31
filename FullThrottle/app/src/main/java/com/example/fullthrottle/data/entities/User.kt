package com.example.fullthrottle.data.entities

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class User(
    val userId: String? = null,
    val username: String? = null,
    val password: String? = null,
    val mail: String? = null,
    val followers: String? = null,
    val followed: String? = null,
    val userImg: String? = null,
    val informed: String? = null
) : Parcelable