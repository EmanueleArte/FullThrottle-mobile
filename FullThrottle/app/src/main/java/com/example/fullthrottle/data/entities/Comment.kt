package com.example.fullthrottle.data.entities

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Comment(
    val commentId: String? = null,
    val notified: String? = null,
    val postId: String? = null,
    val publishDate: String? = null,
    val text: String? = null,
    val userId: String? = null
) : Parcelable