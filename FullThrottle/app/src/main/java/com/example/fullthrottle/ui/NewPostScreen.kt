package com.example.fullthrottle.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.LocationSearching
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun NewPostScreen() {
    val context = LocalContext.current

    Column (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ){
            SimpleTitle(text = "Nuovo Post")
            Spacer(Modifier.weight(1f))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                OutlineTextButton("Pubblica") { /*TODO*/ }
            }
        }
        TextButtonWithIcon(
            "Scegli una foto o scattane una",
            Icons.Outlined.AddAPhoto,
            "photo icon",
            onClick = { /*TODO*/ },
            Modifier.fillMaxWidth()
        )
        outLineTextField(label = "Titolo", modifier = Modifier.fillMaxWidth())
        outLineTextField(label = "Descrizione", modifier = Modifier.fillMaxWidth())
        outLineTextField(label = "Moto", modifier = Modifier.fillMaxWidth())
        outLineTextField(label = "Lunghezza percorso (in km)", modifier = Modifier.fillMaxWidth())
        outLineTextFieldWithIcon(label = "Luogo", icon = Icons.Outlined.LocationSearching, iconDescription = "location icon", modifier = Modifier.fillMaxWidth())
    }
}