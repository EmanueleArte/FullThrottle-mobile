package com.example.fullthrottle.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullthrottle.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val username = settingsRepository.preferenceFlow

    fun saveUsername(username:String) {
        viewModelScope.launch {
            settingsRepository.saveToDataStore(username)
        }
    }
}