package com.example.fullthrottle.data.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Motorbike (
    val motorbikeId: String? = null,
    val brand: String? = null,
    val model: String? = null,
    val productionYear: String? = null,
    val userId: String? = null
)