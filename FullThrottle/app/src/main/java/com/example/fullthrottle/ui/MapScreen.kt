package com.example.fullthrottle.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.data.DBHelper.getPostsLocations
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.ui.theme.md_theme_light_primary
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    settingsViewModel: SettingsViewModel
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    val mapProperties = MapProperties(
        isMyLocationEnabled = settings["location_updates"] == "true"
    )
    val cameraPositionState = rememberCameraPositionState()
    var locations by remember { mutableStateOf(emptyMap<String, List<Post>>()) }

    LaunchedEffect(Unit) {
        locations = getPostsLocations()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        SimpleTitle(text = "Mappa")
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, md_theme_light_primary),
            properties = mapProperties,
            cameraPositionState = cameraPositionState
        )
    }
}