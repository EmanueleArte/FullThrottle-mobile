package com.example.fullthrottle.data.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment(
    val commentId: String? = null,
    val notified: String? = null,
    val postId: String? = null,
    val publishDate: String? = null,
    val text: String? = null,
    val userId: String? = null
    )