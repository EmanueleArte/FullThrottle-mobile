package com.example.fullthrottle

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fullthrottle.Utils.manageNavigateBack
import com.example.fullthrottle.data.DataStoreConstants.MAIL_KEY
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.TabConstants.FOLLOWED_TAB
import com.example.fullthrottle.data.TabConstants.FOLLOWERS_TAB
import com.example.fullthrottle.ui.*
import com.example.fullthrottle.ui.Logo.logoId
import com.example.fullthrottle.ui.UiConstants.ANIMATION_DURATION
import com.example.fullthrottle.viewModel.SettingsViewModel
import com.example.fullthrottle.viewModel.WarningViewModel
import dagger.hilt.android.HiltAndroidApp

sealed class AppScreen(val name: String) {
    object Home : AppScreen("Home")
    object Post : AppScreen("post")
    object Map : AppScreen("Map Screen")
    object NewPost : AppScreen("New Post Screen")
    object Search : AppScreen("Search Screen")
    object Profile : AppScreen("Profile Screen")
    object Login : AppScreen("Login")
    object Register : AppScreen("Register")
    object Settings : AppScreen("Settings Screen")
    object Followers : AppScreen("Followers Screen")
    object Followeds : AppScreen("Followed Screen")
    object ProfileModification : AppScreen("Profile modification")
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
    AnimatedVisibility(
        visible = currentScreen != AppScreen.Login.name,
        enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(ANIMATION_DURATION)),
        exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(ANIMATION_DURATION))
    ) {
        CenterAlignedTopAppBar(
            title = {
                if (currentScreen != AppScreen.Login.name && currentScreen != AppScreen.Register.name) {
                    Image(
                        painter = painterResource(id = logoId),
                        contentDescription = "app logo",
                        modifier = Modifier
                            .requiredHeight(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }
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
}

@Composable
fun BottomAppBarFunction(
    currentScreen: String,
    navController: NavHostController,
    goToMyProfile: () -> Unit
) {
    AnimatedVisibility(
        visible = currentScreen != AppScreen.Login.name
                && currentScreen != AppScreen.Register.name,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(ANIMATION_DURATION)),
        exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(ANIMATION_DURATION))
    ) {
        NavigationBar {
            // Map
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Outlined.Map,
                        contentDescription = stringResource(id = R.string.nav_map)
                    )
                },
                selected = currentScreen == AppScreen.Map.name,
                onClick = {
                    navController.backQueue.clear()
                    navController.navigate(AppScreen.Map.name)
                },
                label = { Text(stringResource(id = R.string.nav_map)) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
                )
            )
            // Search
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = stringResource(id = R.string.nav_search)
                    )
                },
                selected = currentScreen == AppScreen.Search.name,
                onClick = {
                    navController.backQueue.clear()
                    navController.navigate(AppScreen.Search.name)
                },
                label = { Text(stringResource(id = R.string.nav_search)) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
                )
            )
            // Home
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Outlined.Home,
                        contentDescription = stringResource(id = R.string.nav_home)
                    )
                },
                selected = currentScreen == AppScreen.Home.name,
                onClick = {
                    navController.backQueue.clear()
                    navController.navigate(AppScreen.Home.name)
                },
                label = { Text(stringResource(id = R.string.nav_home)) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
                )
            )
            // New post
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = stringResource(id = R.string.nav_new_post)
                    )
                },
                selected = currentScreen == AppScreen.NewPost.name,
                onClick = {
                    navController.backQueue.clear()
                    navController.navigate(AppScreen.NewPost.name)
                },
                label = { Text(stringResource(id = R.string.nav_new_post)) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
                )
            )
            // Profile
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        contentDescription = stringResource(id = R.string.nav_profile)
                    )
                },
                selected = currentScreen == AppScreen.Profile.name
                        || currentScreen == AppScreen.Followers.name
                        || currentScreen == AppScreen.Followeds.name
                        || currentScreen == AppScreen.Settings.name
                        || currentScreen == AppScreen.ProfileModification.name,
                onClick = {
                    navController.backQueue.clear()
                    goToMyProfile()
                },
                label = { Text(stringResource(id = R.string.nav_profile)) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationApp(
    settingsViewModel: SettingsViewModel,
    warningViewModel: WarningViewModel,
    startDestination: String = AppScreen.Login.name,
    methods: Map<String, () -> Unit>,
    navController: NavHostController = rememberNavController()
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = backStackEntry?.destination?.route ?: startDestination

    val snackbarHostState = remember { SnackbarHostState() }

    val userIdHistory = remember {
        mutableListOf<String>()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarFunction(
                currentScreen = currentScreen,
                canNavigateBack = currentScreen != AppScreen.Home.name,
                navigateUp = {
                    if (navController.previousBackStackEntry != null) {
                        navController.navigateUp()
                    } else {
                        navController.navigate(AppScreen.Home.name)
                    }
                    manageNavigateBack(
                        userIdHistory = userIdHistory,
                        currentScreen = currentScreen
                    )
                },
                onSettingsButtonClicked = { navController.navigate(AppScreen.Settings.name) }
            )
        },
        bottomBar = {
            BottomAppBarFunction(
                currentScreen,
                navController
            ) {
                //userId.value = settings[USER_ID_KEY].orEmpty()
                userIdHistory.removeAll(userIdHistory)
                userIdHistory.add(settings[USER_ID_KEY].orEmpty())
                navController.navigate(AppScreen.Profile.name)
            }
        }
    ) { innerPadding ->
        NavigationGraph(settingsViewModel, warningViewModel, startDestination, navController, innerPadding, methods,
            userIdHistory = userIdHistory
        )
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
        if (warningViewModel.showModificationDone.value) {
            SimpleSnackBarComposable(
                snackbarHostState,
                warningViewModel,
                warningViewModel.simpleSnackBarContent.value
            )
        }
    }
}

@Composable
private fun NavigationGraph(
    settingsViewModel: SettingsViewModel,
    warningViewModel: WarningViewModel,
    startDestination: String = AppScreen.Login.name,
    navController: NavHostController,
    innerPadding: PaddingValues,
    methods: Map<String, () -> Unit>,
    userIdHistory: MutableList<String>,
    modifier: Modifier = Modifier
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())

    var postId by remember {
        mutableStateOf(String())
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.padding(innerPadding)
    ) {
        composable(route = AppScreen.Home.name) {
            HomeScreen(
                fun (id : String) {
                    postId = id
                    navController.navigate(AppScreen.Post.name)
                },
                fun (id : String) {
                    userIdHistory.add(id)
                    navController.navigate(AppScreen.Profile.name)
                },
                settingsViewModel
            )
        }
        composable(route = AppScreen.Post.name) {
            PostScreen(
                postId,
                settingsViewModel
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
                settingsViewModel,
                mapOf(
                    "followers" to { navController.navigate(AppScreen.Followers.name) },
                    "followed" to { navController.navigate(AppScreen.Followeds.name) },
                    "profileModification" to { navController.navigate(AppScreen.ProfileModification.name) }
                ),
                if (userIdHistory.size > 0) userIdHistory.last() else settings[USER_ID_KEY].toString()
                //userId.value
            )
        }
        composable(route = AppScreen.ProfileModification.name) {
            ProfileModificationScreen(
                settings[USER_ID_KEY].toString(),
                settings[USERNAME_KEY].orEmpty(),
                settings[MAIL_KEY].orEmpty(),
                settingsViewModel,
                warningViewModel
            )
        }
        composable(route = AppScreen.Followers.name) {
            FollowersScreen(userIdHistory.last()/*userId.value*/, FOLLOWERS_TAB,
                fun (uid: String) {
                    userIdHistory.add(uid)
                    //userId.value = uid
                    navController.navigate(AppScreen.Profile.name)
                }
            )
        }
        composable(route = AppScreen.Followeds.name) {
            FollowersScreen(userIdHistory.last()/*userId.value*/, FOLLOWED_TAB,
                fun (uid: String) {
                    userIdHistory.add(uid)
                    //userId.value = uid
                    navController.navigate(AppScreen.Profile.name)
                }
            )
        }
        composable(route = AppScreen.Login.name) {
            LoginScreen(
                settingsViewModel,
                mapOf(
                    "home" to { navController.navigate(AppScreen.Home.name) },
                    "registration" to { navController.navigate(AppScreen.Register.name) },
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