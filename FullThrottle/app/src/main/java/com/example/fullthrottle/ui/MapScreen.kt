package com.example.fullthrottle.ui

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.fullthrottle.MainActivity.Companion.checkLocationPermission
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DBHelper.getAllPosts
import com.example.fullthrottle.data.DBHelper.getImageUri
import com.example.fullthrottle.data.DBHelper.getPostsLocations
import com.example.fullthrottle.data.DBHelper.getUserById
import com.example.fullthrottle.data.DataStoreConstants.LOCATION_UPDATES_KEY
import com.example.fullthrottle.data.LocationDetails
import com.example.fullthrottle.data.entities.Post
import com.example.fullthrottle.data.entities.User
import com.example.fullthrottle.ui.MapScreenData.coordinatesLoaded
import com.example.fullthrottle.ui.MapScreenData.load
import com.example.fullthrottle.ui.UiConstants.CORNER_RADIUS
import com.example.fullthrottle.ui.UiConstants.MAIN_H_PADDING
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.example.fullthrottle.viewModel.WarningViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

internal object MapScreenData {
    var coordinatesLoaded by mutableStateOf(emptyMap<String, LatLng>())

    fun load(coordinates: Map<String, LatLng>) {
        coordinatesLoaded = coordinates
    }
}

@Composable
fun MapScreen(
    settingsViewModel: SettingsViewModel,
    warningViewModel: WarningViewModel,
    goToPost: (String) -> Unit,
    goToSettings: () -> Unit,
    focusLocation: String?,
    methods: Map<String, () -> Unit>,
    location: MutableState<LocationDetails>
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())

    // Check GPS settings
    var showToast by remember { mutableStateOf(true) }
    if (settings[LOCATION_UPDATES_KEY] == "false" && showToast) {
        showButtonSnackBar(
            warningViewModel,
            stringResource(id = R.string.curr_position_off),
            stringResource(id = R.string.go_settings),
            goToSettings
        )
        showToast = false
    } else if (settings[LOCATION_UPDATES_KEY] == "true" && showToast) {
        methods["startLocationUpdates"]?.invoke()
        showToast = false
    }

    val coroutineScope = rememberCoroutineScope()

    val geocoder = Geocoder(LocalContext.current)
    val mapProperties = MapProperties(
        isMyLocationEnabled = settings["location_updates"] == "true" && checkLocationPermission(context)
    )

    var init = false
    val cameraPositionState = rememberCameraPositionState(
        init = {
            init = true
        }
    )

    var locations by remember { mutableStateOf(emptyMap<String, List<Post>>()) }
    var coordinates by rememberSaveable { mutableStateOf(coordinatesLoaded) }
    var posts by remember { mutableStateOf(emptyList<Post>()) }
    var users by remember { mutableStateOf(emptyList<User>()) }
    var postImagesUris by remember { mutableStateOf(emptyList<Uri>()) }

    var currentLocation by remember { mutableStateOf(emptyList<Post>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        posts = getAllPosts()
        users = posts.map { post -> getUserById(post.userId.toString()) as User }
        postImagesUris = posts.map { post -> getImageUri(post.userId + "/" + post.postImg) }
        locations = getPostsLocations()
        if (coordinates.isEmpty()) {
            locations.keys.forEach { location ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(location, 1) {
                        if (it.isNotEmpty()) {
                            coordinates = coordinates.plus(
                                Pair(
                                    location,
                                    LatLng(it[0].latitude, it[0].longitude)
                                )
                            )
                        }
                    }
                } else {
                    val tCoordinates = geocoder.getFromLocationName(location, 1)
                    if (!tCoordinates.isNullOrEmpty()) {
                        coordinates = coordinates.plus(
                            Pair(
                                location,
                                LatLng(
                                    tCoordinates[0].latitude,
                                    tCoordinates[0].longitude
                                )
                            )
                        )
                    }
                }
            }
        }
        if (!focusLocation.isNullOrEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(focusLocation, 1) {
                    if (it.isNotEmpty()) {
                        currentLocation = locations[focusLocation]!!
                        coroutineScope.launch {
                            loading = false
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(
                                        LatLng(it[0].latitude, it[0].longitude),
                                        12f,
                                        0f,
                                        0f
                                    )
                                )
                            )
                        }
                    }
                }
            } else {
                val tCoordinates = geocoder.getFromLocationName(focusLocation, 1)
                if (!tCoordinates.isNullOrEmpty()) {
                    currentLocation = locations[focusLocation]!!
                    loading = false
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newCameraPosition(
                            CameraPosition(
                                LatLng(
                                    tCoordinates[0].latitude,
                                    tCoordinates[0].longitude
                                ),
                                12f,
                                0f,
                                0f
                            )
                        )
                    )
                }
            }
        } else if (init) {
            loading = false
            if (settings[LOCATION_UPDATES_KEY] == "true" && checkLocationPermission(context)) {
                cameraPositionState.move(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition(
                            LatLng(location.value.latitude, location.value.longitude),
                            10f,
                            0f,
                            0f
                        )
                    )
                )
            } else {
                cameraPositionState.move(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition(
                            LatLng(41.87194, 12.56738),
                            5f,
                            0f,
                            0f
                        )
                    )
                )
            }
        }
        loading = false
        load(coordinates)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MAIN_H_PADDING)
            .padding(bottom = 10.dp)
    ) {
        SimpleTitle(text = stringResource(id = R.string.map))
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(CORNER_RADIUS))
                .clip(RoundedCornerShape(CORNER_RADIUS)),
            properties = mapProperties,
            cameraPositionState = cameraPositionState
        ) {
            for (c in coordinates) {
                val icon = getDrawable(context, R.drawable.ft_marker)!!.toBitmap(100, 100)
                Marker(
                    state = MarkerState(c.value),
                    icon = BitmapDescriptorFactory.fromBitmap(icon),
                    onClick = {
                        currentLocation = locations[c.key]!!
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newCameraPosition(
                                    CameraPosition(
                                        c.value,
                                        12f,
                                        0f,
                                        0f
                                    )
                                )
                            )
                        }
                        true
                    }
                )
            }
        }
        /*if (loading) {
            Dialog(onDismissRequest = {}) {
                LoadingAnimation()
            }
        }*/
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
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(IntrinsicSize.Max)
                        ){
                            Column (
                                modifier = Modifier
                                    .padding(5.dp)
                                    .weight(1f)
                            ) {
                                if (currentLocation.isNotEmpty()) Text(text = currentLocation.first().position.toString())
                            }
                            IconButton(onClick = {
                                val gmmIntentUri =
                                    Uri.parse("geo:0,0?q=" + currentLocation.first().position.toString())
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                startActivity(context, mapIntent, null)
                            }) {
                                Icon(Icons.Outlined.Navigation, "navigation icon")
                            }
                        }
                        LazyColumn (
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 15.dp),
                            modifier = Modifier.heightIn(0.dp, 150.dp)
                        ) {
                            items(currentLocation) { post ->
                                Card(
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            goToPost(post.postId)
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
                                                    .requiredWidth(105.dp)
                                                    .requiredHeight(60.dp)
                                                    .clip(RoundedCornerShape(UiConstants.CORNER_RADIUS))
                                            )
                                        }

                                        Column(
                                            modifier = Modifier.padding(start = 5.dp)
                                        ) {
                                            Text(
                                                text = users[posts.indexOf(post)].username.orEmpty(),
                                                fontWeight = FontWeight.SemiBold
                                            )
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