package com.example.fullthrottle.ui

import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.fullthrottle.data.DBHelper.getAllPosts
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getPostsLocations
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.ui.theme.md_theme_light_primary
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(
    settingsViewModel: SettingsViewModel,
    goToPost: (String) -> Unit
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    val geocoder = Geocoder(LocalContext.current)
    val mapProperties = MapProperties(
        isMyLocationEnabled = settings["location_updates"] == "true"
    )
    val cameraPositionState = rememberCameraPositionState()
    var locations by remember { mutableStateOf(emptyMap<String, List<Post>>()) }
    var coordinates by remember { mutableStateOf(emptyMap<String, LatLng>()) }
    var posts by remember { mutableStateOf(emptyList<Post>()) }
    var postImagesUris by remember { mutableStateOf(emptyList<Uri>()) }

    var currentLocation by remember { mutableStateOf(emptyList<Post>()) }

    LaunchedEffect(Unit) {
        posts = getAllPosts()
        postImagesUris = posts.map { post -> getImageUri(post.userId + "/" + post.postImg) }
        locations = getPostsLocations()
        locations.keys.forEach { location ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(location, 1) {
                    coordinates = coordinates.plus(Pair(location, LatLng(it[0].latitude, it[0].longitude)))
                }
            } else {
                val tCoordinates = geocoder.getFromLocationName(location, 1)
                coordinates = coordinates.plus(Pair(location, LatLng(tCoordinates?.get(0)?.latitude as Double, tCoordinates?.get(0)?.longitude as Double)))
            }
        }
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
        ) {
            for (c in coordinates) {
                Marker(
                    state = MarkerState(c.value),
                    onClick = {
                        currentLocation = locations[c.key]!!
                        true
                    }
                )
            }
        }
        if (currentLocation.isNotEmpty()) {
            Popup (
                onDismissRequest = {
                    currentLocation = emptyList()
                },
                alignment = Alignment.BottomCenter
            ) {
                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    modifier = Modifier
                        .padding(20.dp),
                    elevation = CardDefaults.cardElevation(5.dp)
                ) {
                    Column (
                        modifier = Modifier.padding(5.dp)
                    ) {
                        if (currentLocation.isNotEmpty()) Text(text = currentLocation.first().position.toString())
                        LazyColumn (
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 20.dp),
                        ) {
                            items(currentLocation) { post ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            goToPost(post.postId.toString())
                                        },
                                    elevation = CardDefaults.cardElevation(5.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(2.dp)
                                        ) {
                                            PostImage(
                                                imgUri = postImagesUris[posts.indexOf(post)],
                                                contentDescription = "post image",
                                                modifier = Modifier
                                                    .requiredWidth(71.dp)
                                                    .requiredHeight(40.dp)
                                                    .clip(RoundedCornerShape(UiConstants.CORNER_RADIUS))
                                            )
                                        }

                                        Column(
                                            modifier = Modifier.padding(start = 5.dp)
                                        ) {
                                            Text(text = post.publishDate.orEmpty())
                                            Text(text = post.title.orEmpty())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}