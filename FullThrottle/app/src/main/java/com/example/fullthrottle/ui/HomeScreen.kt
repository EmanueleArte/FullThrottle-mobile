package com.example.fullthrottle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.BottomAppBarFunction
import com.example.fullthrottle.R
import com.example.fullthrottle.TopAppBarFunction

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    Column(

    ) {
        Card (
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(10.dp)
        ){
            Column {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.fullthrottle_logo_light),
                        contentDescription = "user image",
                        modifier = Modifier
                            .requiredHeight(40.dp)
                            .requiredWidth(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Column {
                        Text(
                            text = "Rider",
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "20/20/2020")
                    }
                    Spacer(modifier = Modifier.fillMaxWidth())
                    Icon(Icons.Filled.Place, contentDescription = "post location")
                }
                Image(painter = painterResource(id = R.drawable.fullthrottle_logo_light),
                    contentDescription = "post image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)

                )
                Text(
                    text = "Passo del muraglione",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Piace a 326 riders",
                    fontWeight = FontWeight.Thin
                )
                Text(text = "Moto: KTM Duke 890")
                Text(text = "Lunghezza percorso: 8.3km")
                Text(text = "Questo passo è stupendo!")
            }
        }
    }
}