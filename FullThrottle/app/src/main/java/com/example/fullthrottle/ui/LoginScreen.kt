package com.example.fullthrottle.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val context = LocalContext.current
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(paddingValues)
                .padding(50.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.fullthrottle_logo_light),
                contentDescription = "app logo",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.size(40.dp))

            OutLineTextField(label = "Username")

            Spacer(modifier = Modifier.size(10.dp))

            OutLineTextField(label = "Password")

            Spacer(modifier = Modifier.size(10.dp))

            SimpleButton(value = "Login", onClick = { /*TODO*/ })

            Spacer(modifier = Modifier.size(5.dp))

            Text(
                text = "Non hai ancora un account?",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.size(5.dp))

            SimpleButton(value = "Registrati", onClick = { /*TODO*/ })

        }
    }
}