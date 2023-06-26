package com.example.fullthrottle.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
@Entity(tableName = "motorbikes")
data class Motorbike (
    @PrimaryKey
    var motorbikeId: String = "",
    @ColumnInfo(name = "brand")
    val brand: String? = null,
    @ColumnInfo(name = "model")
    val model: String? = null,
    @ColumnInfo(name = "prod_year")
    val productionYear: String? = null,
    @ColumnInfo(name = "user_id")
    val userId: String? = null,
    @ColumnInfo(name = "deleted")
    val deleted: Boolean? = null
) : Parcelable