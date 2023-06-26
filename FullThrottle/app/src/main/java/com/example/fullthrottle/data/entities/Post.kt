package com.example.fullthrottle.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
@Entity(tableName = "posts")
data class Post(
    @PrimaryKey
    var postId: String = "",
    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,
    @ColumnInfo(name = "description")
    val description: String? = null,
    @ColumnInfo(name = "lapTime")
    val lapTime: String? = null,
    @ColumnInfo(name = "length")
    val length: String? = null,
    @ColumnInfo(name = "likes_number")
    val likesNumber: String? = null,
    @ColumnInfo(name = "motorbike_id")
    val motorbikeId: String? = null,
    @ColumnInfo(name = "position")
    val position: String? = null,
    @ColumnInfo(name = "post_img")
    val postImg: String? = null,
    @ColumnInfo(name = "publish_date")
    val publishDate: String? = null,
    @ColumnInfo(name = "title")
    val title: String? = null,
    @ColumnInfo(name = "user_id")
    val userId: String? = null
) : Parcelable
