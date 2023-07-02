package com.example.fullthrottle

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fullthrottle.data.DataStoreConstants.MAIL_KEY
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.data.DataStoreConstants.USER_ID_KEY
import com.example.fullthrottle.data.HomeValues.getFilterValueListener
import com.example.fullthrottle.data.HomeValues.setLifeCycleOwner
import com.example.fullthrottle.data.LocalDB
import com.example.fullthrottle.data.LocalDbViewModel
import com.example.fullthrottle.data.LocationDetails
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
    object Post : AppScreen("Post")
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
    object Notifications : AppScreen("Notifications Screen")
}

@HiltAndroidApp
class FullThrottle : Application() {
    val database by lazy { LocalDB.getDatabase(this) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarFunction(
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsButtonClicked: () -> Unit,
    onNotificationsButtonClicked: () -> Unit
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
                } else if (currentScreen == AppScreen.Home.name) {
                    Box(
                        contentAlignment = Alignment.Center,
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        fun expand() {
                            expanded = true
                        }

                        setLifeCycleOwner(LocalLifecycleOwner.current)
                        val allPostsLabel = stringResource(id = R.string.all_posts)
                        val followedPostsLabel = stringResource(id = R.string.followeds_posts_only)
                        val currFilter = stringResource(id = getFilterValueListener().value!!)
                        var filterValueLabel by remember { mutableStateOf(currFilter) }

                        TextButtonWithIcon(
                            text = filterValueLabel,
                            icon = Icons.Outlined.ExpandMore,
                            iconDescription = "Expand more",
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) { expand() }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    filterValueLabel = allPostsLabel
                                    getFilterValueListener().value = R.string.all_posts
                                },
                                text = {
                                    Text(text = allPostsLabel)
                                }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    filterValueLabel = followedPostsLabel
                                    getFilterValueListener().value = R.string.followeds_posts_only
                                },
                                text = {
                                    Text(text = followedPostsLabel)
                                }
                            )
                        }
                    }
                }
            },
            actions = {
                if (currentScreen == AppScreen.Profile.name) {
                    IconButton(onClick = onSettingsButtonClicked) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings button"
                        )
                    }
                } else {
                    IconButton(onClick = onNotificationsButtonClicked) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                    }
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
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
    localDbViewModel: LocalDbViewModel,
    startDestination: String = AppScreen.Login.name,
    methods: Map<String, () -> Unit>,
    onBackAction: MutableState<() -> Unit>,
    navController: NavHostController = rememberNavController(),
    location: MutableState<LocationDetails>
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = backStackEntry?.destination?.route ?: startDestination

    val snackbarHostState = remember { SnackbarHostState() }

    val userIdStack = remember { mutableListOf<String>() }

    val postIdStack = remember { mutableStateListOf<String>() }

    onBackAction.value = {
        if (currentScreen == AppScreen.Login.name
            || currentScreen == AppScreen.Home.name) {
            methods["exit"]?.invoke()
        }
        if (navController.previousBackStackEntry != null) {
            navController.navigateUp()
        } else {
            navController.navigate(AppScreen.Home.name)
        }
        manageNavigateBack(
            userIdStack = userIdStack,
            postIdStack = postIdStack,
            currentScreen = currentScreen
        )
        println("ok ok ok")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarFunction(
                currentScreen = currentScreen,
                canNavigateBack = currentScreen != AppScreen.Home.name,
                navigateUp = {
                    onBackAction.value()
                },
                onSettingsButtonClicked = { navController.navigate(AppScreen.Settings.name) },
                onNotificationsButtonClicked = { navController.navigate(AppScreen.Notifications.name) }
            )
        },
        bottomBar = {
            BottomAppBarFunction(
                currentScreen,
                navController
            ) {
                userIdStack.removeAll(userIdStack)
                userIdStack.add(settings[USER_ID_KEY].orEmpty())
                navController.navigate(AppScreen.Profile.name)
            }
        }
    ) { innerPadding ->
        NavigationGraph(settingsViewModel, warningViewModel, localDbViewModel, navController, innerPadding, methods,
            userIdStack = userIdStack,
            postIdStack = postIdStack,
            startDestination = startDestination,
            location = location
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
                stringResource(id = R.string.location_snackbar_stop)
            )
        }
        if (warningViewModel.startLocationSnackBar.value) {
            PermissionSnackBarComposable(
                snackbarHostState,
                warningViewModel,
                stringResource(id = R.string.location_snackbar_start)
            )
        }
        if (warningViewModel.showModificationDone.value) {
            SimpleSnackBarComposable(
                snackbarHostState,
                warningViewModel,
                warningViewModel.simpleSnackBarContent.value
            )
        }
        if (warningViewModel.showGoToSettings.value) {
            SimpleSnackBarComposable(
                snackbarHostState,
                warningViewModel,
                warningViewModel.simpleSnackBarContent.value,
                warningViewModel.simpleSnackBarActionLabel.value,
                warningViewModel.action.value
            )
        }
    }
}

@Composable
private fun NavigationGraph(
    settingsViewModel: SettingsViewModel,
    warningViewModel: WarningViewModel,
    localDbViewModel: LocalDbViewModel,
    navController: NavHostController,
    innerPadding: PaddingValues,
    methods: Map<String, () -> Unit>,
    userIdStack: MutableList<String>,
    postIdStack: MutableList<String>,
    modifier: Modifier = Modifier,
    startDestination: String = AppScreen.Login.name,
    location: MutableState<LocationDetails>
) {
    val settings by settingsViewModel.settings.collectAsState(initial = emptyMap())
    var focusLocation by remember { mutableStateOf("") }

    val goToPost = fun(id : String) {
        postIdStack.add(id)
        navController.navigate(AppScreen.Post.name)
    }
    val goToProfile = fun(id : String) {
        userIdStack.add(id)
        navController.navigate(AppScreen.Profile.name)
    }
    val goToMap = fun(location : String) {
        focusLocation = location
        navController.navigate(AppScreen.Map.name)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.padding(innerPadding)
    ) {
        composable(route = AppScreen.Home.name) {
            HomeScreen(
                goToPost,
                goToProfile,
                goToMap,
                settingsViewModel,
                localDbViewModel
            )
        }
        composable(route = AppScreen.Post.name) {
            PostScreen(
                if (postIdStack.size > 0) postIdStack.last() else "",
                settingsViewModel,
                goToProfile,
                goToMap
            )
        }
        composable(route = AppScreen.Map.name) {
            MapScreen(
                settingsViewModel,
                warningViewModel,
                goToPost,
                { navController.navigate(AppScreen.Settings.name) },
                focusLocation,
                methods,
                location
            )
            focusLocation = ""
        }
        composable(route = AppScreen.NewPost.name) {
            NewPostScreen(
                settingsViewModel,
                warningViewModel,
                { navController.navigate(AppScreen.Home.name) },
                { navController.navigate(AppScreen.Settings.name) },
                location
            )
        }
        composable(route = AppScreen.Search.name) {
            SearchScreen(
                goToPost,
                goToProfile
            )
        }
        composable(route = AppScreen.Profile.name) {
            ProfileScreen(
                settingsViewModel,
                localDbViewModel,
                mapOf(
                    "followers" to { navController.navigate(AppScreen.Followers.name) },
                    "followed" to { navController.navigate(AppScreen.Followeds.name) },
                    "profileModification" to { navController.navigate(AppScreen.ProfileModification.name) }
                ),
                if (userIdStack.size > 0) userIdStack.last() else settings[USER_ID_KEY].toString(),
                goToPost
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
            FollowersScreen(userIdStack.last()/*userId.value*/, FOLLOWERS_TAB,
                goToProfile
            )
        }
        composable(route = AppScreen.Followeds.name) {
            FollowersScreen(userIdStack.last()/*userId.value*/, FOLLOWED_TAB,
                goToProfile
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
        composable(route = AppScreen.Settings.name) {
            SettingsScreen(settingsViewModel, methods)
        }
        composable(route = AppScreen.Notifications.name) {
            NotificationsScreen(
                goToPost,
                goToProfile,
                localDbViewModel
            )
        }
    }
}