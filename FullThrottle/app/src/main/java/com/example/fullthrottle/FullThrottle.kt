package com.example.fullthrottle

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.ui.*
import com.example.fullthrottle.ui.GPSAlertDialogComposable
import com.example.fullthrottle.ui.PermissionSnackBarComposable
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.example.fullthrottle.viewModel.WarningViewModel
import dagger.hilt.android.HiltAndroidApp

sealed class AppScreen(val name: String) {
    object Home : AppScreen("Home")
    object Map : AppScreen("Map Screen")
    object NewPost : AppScreen("New Post Screen")
    object Search : AppScreen("Search Screen")
    object Profile : AppScreen("Profile Screen")
    object Login : AppScreen("Login")
    object Register : AppScreen("Register")
    object Settings : AppScreen("Settings Screen")
}

@HiltAndroidApp
class FullThrottle : Application() {
    // lazy --> the database and the repository are only created when they're needed
    //val database by lazy { PlacesDatabase.getDatabase(this) }
}

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

@Composable
fun BottomAppBarFunction(
    currentScreen: String,
    navController: NavHostController
) {
    NavigationBar {
        // Home
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            selected = currentScreen == AppScreen.Home.name,
            onClick = { navController.navigate(AppScreen.Home.name) },
            label = { Text(stringResource(id = R.string.nav_home)) }
        )
        // Map
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Map, contentDescription = null) },
            selected = currentScreen == AppScreen.Map.name,
            onClick = { navController.navigate(AppScreen.Map.name) },
            label = { Text(stringResource(id = R.string.nav_map)) }
        )
        // New post
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
            selected = currentScreen == AppScreen.NewPost.name,
            onClick = { navController.navigate(AppScreen.NewPost.name) },
            label = { Text(stringResource(id = R.string.nav_new_post)) }
        )
        // Search
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            selected = currentScreen == AppScreen.Search.name,
            onClick = { navController.navigate(AppScreen.Search.name) },
            label = { Text(stringResource(id = R.string.nav_search)) }
        )
        // Profile
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
            selected = currentScreen == AppScreen.Profile.name,
            onClick = { navController.navigate(AppScreen.Profile.name) },
            label = { Text(stringResource(id = R.string.nav_profile)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationApp(
    warningViewModel: WarningViewModel,
    //startLocationUpdates: () -> Unit,
    methods: Map<String, () -> Unit>,
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
        },
        bottomBar = {
            BottomAppBarFunction(
                currentScreen,
                navController
            )
        }
    ) { innerPadding ->
        NavigationGraph(navController, innerPadding, methods)
        val context = LocalContext.current
        if (warningViewModel.showPermissionSnackBar.value) {
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
        }
        if (warningViewModel.stopLocationSnackBar.value) {
            PermissionSnackBarComposable(
                snackbarHostState,
                warningViewModel,
                "Aggiornamenti sulla posizione attuale disabilitati"
            )
        }
        if (warningViewModel.startLocationSnackBar.value) {
            PermissionSnackBarComposable(
                snackbarHostState,
                warningViewModel,
                "Aggiornamenti sulla posizione attuale abilitati"
            )
        }
    }
}

@Composable
private fun NavigationGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    methods: Map<String, () -> Unit>,
    modifier: Modifier = Modifier
) {
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    var startDestination = AppScreen.Login.name
    if (settings.isNotEmpty() && settings[USER_ID_KEY] != "") {
        startDestination = AppScreen.Home.name
    }
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.padding(innerPadding)
    ) {
        composable(route = AppScreen.Home.name) {
            HomeScreen(
                /*onAddButtonClicked = {
                    navController.navigate(AppScreen.Add.name)
                },
                onItemClicked = {
                    navController.navigate(AppScreen.Details.name)
                },
                placesViewModel = placesViewModel*/
            )
        }
        composable(route = AppScreen.Map.name) {
            MapScreen(
            )
        }
        composable(route = AppScreen.NewPost.name) {
            NewPostScreen(
            )
        }
        composable(route = AppScreen.Search.name) {
            SearchScreen(
            )
        }
        composable(route = AppScreen.Profile.name) {
            ProfileScreen(
            )
        }
        composable(route = AppScreen.Login.name) {
            LoginScreen(
                settingsViewModel,
                mapOf(
                    "home" to { navController.navigate(AppScreen.Home.name) },
                    "registration" to { navController.navigate(AppScreen.Register.name) }
                )
            )
        }
        composable(route = AppScreen.Register.name) {
            RegisterScreen(
                settingsViewModel,
                mapOf(
                    "home" to { navController.navigate(AppScreen.Home.name) }
                )
            )
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
            SettingsScreen(settingsViewModel, methods)
        }
    }
}