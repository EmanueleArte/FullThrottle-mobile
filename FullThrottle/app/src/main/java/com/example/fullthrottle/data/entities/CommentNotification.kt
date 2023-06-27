package com.example.fullthrottle.data.entities;

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties;
import kotlinx.parcelize.Parcelize;

@IgnoreExtraProperties
@Parcelize
@Entity(tableName = "comments_notifications")
data class CommentNotification(
    @PrimaryKey
    val commentId: String = "",
    @ColumnInfo(name = "notified")
    val notified: String? = null,
    @ColumnInfo(name = "post_id")
    val postId: String? = null,
    @ColumnInfo(name = "publish_date")
    val publishDate: String? = null,
    @ColumnInfo(name = "text")
    val text: String? = null,
    @ColumnInfo(name = "user_id")
    val userId: String? = null,
    @ColumnInfo(name = "username")
    val username: String? = null
) : Parcelable