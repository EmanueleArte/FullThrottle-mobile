package com.example.fullthrottle.data.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    val categoryId: String? = null,
    val description: String? = null,
    val lapTime: String? = null,
    val length: String? = null,
    val likesNumber: String? = null,
    val motorbikeId: String? = null,
    val position: String? = null,
    val postId: String? = null,
    val postImg: String? = null,
    val publishDate: String? = null,
    val title: String? = null,
    val userId: String? = null
)