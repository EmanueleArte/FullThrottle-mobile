package com.example.fullthrottle.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

object ThemeConstants {
    val SYSTEM_THEME = "system"
    val DARK_THEME = "dark"
    val LIGHT_THEME = "light"
}

@OptIn(ExperimentalMaterial3Api::class)
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
fun SimpleTextButton(value: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(value)
    }
}