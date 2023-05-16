package com.example.fullthrottle.data.entities

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val IdUtente: String? = null,
    val Username: String? = null,
    val Password: String? = null,
    val Mail: String? = null,
    val Followers: String? = null,
    val Seguiti: String? = null,
    //val ImgUtente: String? = null,
    val Attivo: String? = null,
    val Token: String? = null,
    val Informato: String? = null
    )