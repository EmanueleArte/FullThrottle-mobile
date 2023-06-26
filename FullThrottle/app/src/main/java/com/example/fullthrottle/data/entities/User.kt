package com.example.fullthrottle.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    var userId: String = "",
    @ColumnInfo(name = "username")
    val username: String? = null,
    @ColumnInfo(name = "password")
    val password: String? = null,
    @ColumnInfo(name = "mail")
    val mail: String? = null,
    @ColumnInfo(name = "followers")
    val followers: String? = null,
    @ColumnInfo(name = "followed")
    val followed: String? = null,
    @ColumnInfo(name = "user_img")
    val userImg: String? = null,
    @ColumnInfo(name = "informed")
    val informed: String? = null
) : Parcelable