package com.example.fullthrottle.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.fullthrottle.BottomAppBarFunction
import com.example.fullthrottle.TopAppBarFunction

@Composable
fun NewPostScreen() {
    val context = LocalContext.current
    Column(

    ) {
        Text(text = "new post")
    }
}