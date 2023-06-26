package com.example.fullthrottle.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Like(
    val likeId: String? = null,
    val notified: String? = null,
    val postId: String? = null,
    val userId: String? = null,
)

@IgnoreExtraProperties
@Entity(tableName = "likes")
data class LikeBool(
    @PrimaryKey
    var likeId: String = "",
    @ColumnInfo(name = "post_id")
    val postId: String? = null,
    @ColumnInfo(name = "value")
    val value: Boolean? = null
)