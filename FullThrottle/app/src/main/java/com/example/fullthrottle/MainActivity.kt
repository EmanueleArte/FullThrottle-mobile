package com.example.fullthrottle

import android.Manifest
import android.R
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.volley.RequestQueue
import com.example.fullthrottle.data.DataStoreConstants.THEME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.LocationDetails
import com.example.fullthrottle.data.ThemeConstants.DARK_THEME
import com.example.fullthrottle.data.ThemeConstants.SYSTEM_THEME
import com.example.fullthrottle.ui.LockScreenOrientation
import com.example.fullthrottle.ui.theme.FullThrottleTheme
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.example.fullthrottle.viewModel.WarningViewModel
import com.google.android.gms.location.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var requestingLocationUpdates = mutableStateOf(false)

    private lateinit var locationPermissionRequest: ActivityResultLauncher<String>

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var connectivityManager : ConnectivityManager

    val location =  mutableStateOf(LocationDetails(0.toDouble(), 0.toDouble()))

    private var queue : RequestQueue? = null

    val warningViewModel by viewModels<WarningViewModel>()

    private var showSnackbar: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                startLocationUpdates()
            } else {
                warningViewModel.setPermissionSnackBarVisibility(true)
            }
        }

        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                location.value = LocationDetails(
                    p0.locations.first().latitude,
                    p0.locations.first().longitude
                )
                //stopLocationUpdates()
                if (isOnline(connectivityManager = connectivityManager)) {
                    //sendRequest(location.value, connectivityManager)
                } else {
                    warningViewModel.setConnectivitySnackBarVisibility(true)
                }
            }
        }

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network : Network) {
                if (requestingLocationUpdates.value) {
                    //sendRequest(location.value, connectivityManager)
                    warningViewModel.setConnectivitySnackBarVisibility(false)
                }
            }

            override fun onLost(network : Network) {
                warningViewModel.setConnectivitySnackBarVisibility(true)
            }
        }

        setContent {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
            var darkTheme = isSystemInDarkTheme()
            if (settings[THEME_KEY] != SYSTEM_THEME) {
                darkTheme = settings[THEME_KEY] == DARK_THEME
            }
            val startDestination = if (settings[USER_ID_KEY] != "") {
                AppScreen.Home.name
            } else {
                AppScreen.Login.name
            }

            FullThrottleTheme(darkTheme = darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

                    NavigationApp(
                        settingsViewModel = settingsViewModel,
                        warningViewModel = warningViewModel,
                        startDestination = startDestination,
                        methods = mapOf(
                            "startLocationUpdates" to ::startLocationUpdates,
                            "stopLocationUpdates" to ::stopLocationUpdates,
                            "requestingLocationUpdatesFalse" to { requestingLocationUpdates.value = false },
                        )
                    )

                }

                if (requestingLocationUpdates.value) {
                    connectivityManager.registerDefaultNetworkCallback(networkCallback)
                }
            }
        }
    }

    fun showSnackBar(content: String) {
        warningViewModel.setSimpleSnackBarContent(content)
        warningViewModel.setSimpleSnackBarVisibility(true)
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates.value) startLocationUpdates()
        showSnackbar = true
    }

    override fun onPause() {
        super.onPause()
        showSnackbar = false
        stopLocationUpdates()
        showSnackbar = true
    }

    override fun onStop() {
        super.onStop()
        queue?.cancelAll(TAG)
        if (requestingLocationUpdates.value)
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .unregisterNetworkCallback(networkCallback)
    }

    override fun onStart() {
        super.onStart()
        if (requestingLocationUpdates.value)
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .registerDefaultNetworkCallback(networkCallback)
    }

    private fun startLocationUpdates() {
        requestingLocationUpdates.value = true

        val permission = Manifest.permission.ACCESS_COARSE_LOCATION

        when {
            //permission already granted
            ContextCompat.checkSelfPermission (this, permission) == PackageManager.PERMISSION_GRANTED -> {
                locationRequest =
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
                        setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                        setWaitForAccurateLocation(true)
                    }.build()

                val gpsEnabled = checkGPS(this)
                if (gpsEnabled) {
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                    if (showSnackbar) {
                        warningViewModel.setStartLocationSnackBarVisibility(true)
                        warningViewModel.setStopLocationSnackBarVisibility(false)
                    }
                } else {
                    warningViewModel.setGPSAlertDialogVisibility(true)
                }
            }
            //permission already denied
            shouldShowRequestPermissionRationale(permission) -> {
                warningViewModel.setPermissionSnackBarVisibility(true)
            }
            else -> {
                //first time: ask for permissions
                locationPermissionRequest.launch(
                    permission
                )
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        if (showSnackbar) {
            warningViewModel.setStartLocationSnackBarVisibility(false)
            warningViewModel.setStopLocationSnackBarVisibility(true)
        }
    }

    private fun isOnline(connectivityManager: ConnectivityManager): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        ) {
            return true
        }
        return false
    }

    companion object {
        private const val TAG = "OSM_REQUEST"

        fun checkGPS(context: Context): Boolean {
            val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
    }
}