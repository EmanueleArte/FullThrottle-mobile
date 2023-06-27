package com.example.fullthrottle.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

@IgnoreExtraProperties
@Parcelize
@Entity(tableName = "follows_notifications")
data class FollowNotification(
    @PrimaryKey
    val followId: String = "",
    @ColumnInfo(name = "followed_id")
    val followedId: String? = null,
    @ColumnInfo(name = "follower_id")
    val followerId: String? = null,
    @ColumnInfo(name = "notified")
    val notified: String? = null,
    @ColumnInfo(name = "username")
    val username: String? = null
) : Parcelable