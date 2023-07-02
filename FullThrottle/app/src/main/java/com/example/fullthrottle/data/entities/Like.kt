package com.example.fullthrottle.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

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

@IgnoreExtraProperties
@Parcelize
@Entity(tableName = "likes_notifications")
data class LikeNotification(
    @PrimaryKey
    val likeId: String = "",
    @ColumnInfo(name = "notified")
    val notified: String? = null,
    @ColumnInfo(name = "post_id")
    val postId: String? = null,
    @ColumnInfo(name = "user_id")
    val userId: String? = null,
    @ColumnInfo(name = "username")
    val username: String? = null,
    @ColumnInfo(name = "date")
    val date: String? = null
): Parcelable