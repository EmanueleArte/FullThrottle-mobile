package com.example.fullthrottle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R

@Composable
fun OutLineTextField(label: String) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    return OutlinedTextField(
        shape = RoundedCornerShape(10.dp),
        value = text,
        label = { Text(text = label) },
        onValueChange = {
            text = it
        }
    )
}

@Composable
fun SimpleButton(value: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(value)
    }
}

@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.fullthrottle_logo_light),
                contentDescription = "app logo",
                modifier = Modifier
                    .requiredHeight(50.dp),
                contentScale = ContentScale.Fit
            )
        },
        navigationIcon = { Icon(Icons.Outlined.ArrowBack, contentDescription = null) },
        actions = { Icon(Icons.Outlined.Notifications, contentDescription = null) }
    )
}

@Composable
fun BottomBar() {
    NavigationBar() {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            selected = true,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Place, contentDescription = null) },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
            selected = false,
            onClick = { /*TODO*/ }
        )
    }
}

@Composable
fun SimpleTextButton(value: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(value)
    }
}