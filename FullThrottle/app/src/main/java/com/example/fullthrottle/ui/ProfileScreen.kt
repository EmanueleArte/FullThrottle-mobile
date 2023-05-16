package com.example.fullthrottle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.BottomAppBarFunction
import com.example.fullthrottle.TopAppBarFunction
import com.example.fullthrottle.ui.theme.md_theme_dark_primary
import com.example.fullthrottle.ui.theme.md_theme_light_primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    Column {
        Row (
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ){
            Column (
                modifier = Modifier
                    .requiredWidth(100.dp)
                    .requiredHeight(100.dp),
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    modifier = Modifier.requiredWidth(100.dp),
                    text = "33",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.requiredWidth(100.dp),
                    text = "followers",
                    textAlign = TextAlign.Center
                )
            }
            Image(
                painter = painterResource(id = com.example.fullthrottle.R.drawable.fullthrottle_logo_light),
                contentDescription = "user image",
                modifier = Modifier
                    .padding(5.dp)
                    .requiredHeight(100.dp)
                    .requiredWidth(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Column (
                modifier = Modifier
                    .requiredWidth(100.dp)
                    .requiredHeight(100.dp),
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    modifier = Modifier.requiredWidth(100.dp),
                    text = "12",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.requiredWidth(100.dp),
                    text = "seguiti",
                    textAlign = TextAlign.Center
                )
            }
        }
        Text(text = "Mark98")
        Text(text = "Mail: prova123@gmail.com")
        Text(text = "Le mie moto:")
        Text(text = "KTM Duke 890")
        Text(text = "Yamaha R1 2022")
        Text(
            text = "Modifica account",
            color = md_theme_light_primary
        )
    }
}