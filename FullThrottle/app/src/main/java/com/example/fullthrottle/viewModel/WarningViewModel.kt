package com.example.fullthrottle.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class WarningViewModel: ViewModel() {
    private var _showPermissionSnackBar = mutableStateOf(false)
    val showPermissionSnackBar
        get() = _showPermissionSnackBar

    private var _showGPSAlertDialog = mutableStateOf(false)
    val showGPSAlertDialog
        get() = _showGPSAlertDialog

    private var _showConnectivitySnackBar = mutableStateOf(false)
    val showConnectivitySnackBar
        get() = _showConnectivitySnackBar

    private var _startLocationSnackBar = mutableStateOf(false)
    val startLocationSnackBar
        get() = _startLocationSnackBar

    private var _stopLocationSnackBar = mutableStateOf(false)
    val stopLocationSnackBar
        get() = _stopLocationSnackBar

    fun setPermissionSnackBarVisibility(visible: Boolean) {
        _showPermissionSnackBar.value = visible
    }

    fun setGPSAlertDialogVisibility(visible: Boolean) {
        _showGPSAlertDialog.value = visible
    }

    fun setConnectivitySnackBarVisibility(visible: Boolean) {
        _showConnectivitySnackBar.value = visible
    }

    fun setStopLocationSnackBarVisibility(visible: Boolean) {
        _stopLocationSnackBar.value = visible
    }

    fun setStartLocationSnackBarVisibility(visible: Boolean) {
        _startLocationSnackBar.value = visible
    }
}