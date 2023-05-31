package com.example.fullthrottle.data.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Like(
    val likeId: String? = null,
    val notified: String? = null,
    val postId: String? = null,
    val userId: String? = null,
)