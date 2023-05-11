package com.example.fullthrottle;

import android.app.Application;
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fullthrottle.viewModel.SettingsViewModel
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
class FullThrottle : Application() {
    // lazy --> the database and the repository are only created when they're needed
    //val database by lazy { PlacesDatabase.getDatabase(this) }
}

/*val settingsViewModel = hiltViewModel<SettingsViewModel>()
val username by settingsViewModel.username.collectAsState(initial = emptyMap())
*/