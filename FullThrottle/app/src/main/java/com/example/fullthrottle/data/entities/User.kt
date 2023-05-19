package com.example.fullthrottle.data.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val userId: String? = null,
    val username: String? = null,
    val password: String? = null,
    val mail: String? = null,
    val followers: String? = null,
    val followed: String? = null,
    val userImg: String? = null,
    val informed: String? = null
    )