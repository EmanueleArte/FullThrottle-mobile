package com.example.fullthrottle;

import android.app.Application;
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fullthrottle.ui.SettingsScreen
import com.example.fullthrottle.viewModel.SettingsViewModel
import dagger.hilt.android.HiltAndroidApp;

sealed class AppScreen(val name: String) {
    object Home : AppScreen("Home")
    object Login : AppScreen("Login")
    object Settings : AppScreen("Settings Screen")
    object Profile : AppScreen("Profile Screen")
}

@HiltAndroidApp
class FullThrottle : Application() {
    // lazy --> the database and the repository are only created when they're needed
    //val database by lazy { PlacesDatabase.getDatabase(this) }
}

/*object DataStoreInstance {
    @Volatile
    var settingsViewModel: SettingsViewModel? = null
}*/

/*val settingsViewModel = hiltViewModel<SettingsViewModel>()
val username by settingsViewModel.username.collectAsState(initial = emptyMap())
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarFunction(
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsButtonClicked: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.fullthrottle_logo_light),
                contentDescription = "app logo",
                modifier = Modifier
                    .requiredHeight(40.dp),
                contentScale = ContentScale.Fit
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            }
        },
        actions = {
            if (currentScreen == AppScreen.Home.name) {
                /* TODO Notifiche */
                IconButton(onClick = { /* doSomething() */ }) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
            }
            if (currentScreen == AppScreen.Profile.name) {
                IconButton(onClick = onSettingsButtonClicked) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Settings button"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationApp(
    //warningViewModel: WarningViewModel,
    //startLocationUpdates: () -> Unit,
    navController: NavHostController = rememberNavController()
) {

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = backStackEntry?.destination?.route ?: AppScreen.Home.name

    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarFunction(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onSettingsButtonClicked = { navController.navigate(AppScreen.Settings.name) }
            )
        }
    ) { innerPadding ->
        NavigationGraph(navController, innerPadding/*, startLocationUpdates*/)
        val context = LocalContext.current
        /*if (warningViewModel.showPermissionSnackBar.value) {
            PermissionSnackBarComposable(snackbarHostState, context, warningViewModel)
        }
        if (warningViewModel.showGPSAlertDialog.value) {
            GPSAlertDialogComposable(context, warningViewModel)
        }
        if (warningViewModel.showConnectivitySnackBar.value) {
            ConnectivitySnackBarComposable(
                snackbarHostState,
                context,
                warningViewModel
            )
        }*/
    }
}

@Composable
private fun NavigationGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    //startLocationUpdates: () -> Unit,
    modifier: Modifier = Modifier
) {
    //val placesViewModel = hiltViewModel<PlacesViewModel>()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Settings.name,
        modifier = modifier.padding(innerPadding)
    ) {
        composable(route = AppScreen.Home.name) {
            /*HomeScreen(
                onAddButtonClicked = {
                    navController.navigate(AppScreen.Add.name)
                },
                onItemClicked = {
                    navController.navigate(AppScreen.Details.name)
                },
                placesViewModel = placesViewModel
            )*/
        }
        /*composable(route = AppScreen.Add.name) {
            AddScreen(
                onNextButtonClicked = {
                    navController.popBackStack(AppScreen.Home.name, inclusive = false)
                },
                placesViewModel = placesViewModel,
                startLocationUpdates
            )
        }
        composable(route = AppScreen.Details.name) {
            DetailsScreen(placesViewModel = placesViewModel)
        }*/
        composable(route = AppScreen.Settings.name) {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            SettingsScreen(settingsViewModel)
        }
    }
}