package com.example.fullthrottle.data.entities

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Motorbike (
    val motorbikeId: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val productionYear: String? = null,
    val userId: String? = null,
    val deleted: Boolean? = null
) : Parcelable