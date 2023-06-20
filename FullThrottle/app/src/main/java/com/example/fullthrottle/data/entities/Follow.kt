package com.example.fullthrottle.data.entities

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Follow(
    val followId: String? = null,
    val followedId: String? = null,
    val followerId: String? = null,
    val notified: String? = null
) : Parcelable