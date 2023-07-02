package com.example.fullthrottle.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.fullthrottle.viewModel.WarningViewModel

@Composable
internal fun GPSAlertDialogComposable(
    applicationContext: Context,
    warningViewModel: WarningViewModel
) {
    AlertDialog(
        onDismissRequest = {
            warningViewModel.setGPSAlertDialogVisibility(false)
        },
        title = {
            Text(text = "GPS disabilitato")
        },
        text = {
            Text(text = "GPS spento ma necessario per ottenere le coordinate")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    if (intent.resolveActivity(applicationContext.packageManager) != null) {
                        applicationContext.startActivity(intent)
                    }
                    warningViewModel.setGPSAlertDialogVisibility(false)
                }
            ) {
                Text("Accendi GPS")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { warningViewModel.setGPSAlertDialogVisibility(false)  }
            ) {
                Text("Annulla")
            }
        }
    )
}

@Composable
internal fun PermissionSnackBarComposable(
    snackbarHostState: SnackbarHostState,
    applicationContext: Context,
    warningViewModel: WarningViewModel
) {
    LaunchedEffect(snackbarHostState) {
        val result = snackbarHostState.showSnackbar(
            message = "Sono richiesti i permessi per ottenere la tua posizione\n",
            actionLabel = "Vai alle impostazioni"
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", applicationContext.packageName, null)
                }
                if (intent.resolveActivity(applicationContext.packageManager) != null) {
                    applicationContext.startActivity(intent)
                }
            }
            SnackbarResult.Dismissed -> {
                warningViewModel.setPermissionSnackBarVisibility(false)
            }
        }
    }
}

@Composable
fun ConnectivitySnackBarComposable(
    snackbarHostState: SnackbarHostState,
    applicationContext: Context,
    warningViewModel: WarningViewModel
) {
    LaunchedEffect(snackbarHostState) {
        val result = snackbarHostState.showSnackbar(
            message = "Internet non disponibile",
            actionLabel = "Vai alle impostazioni",
            duration = SnackbarDuration.Indefinite
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                if (intent.resolveActivity(applicationContext.packageManager) != null) {
                    applicationContext.startActivity(intent)
                }
            }
            SnackbarResult.Dismissed -> {
                warningViewModel.setConnectivitySnackBarVisibility(false)
            }
        }
    }
}

@Composable
internal fun PermissionSnackBarComposable(
    snackbarHostState: SnackbarHostState,
    warningViewModel: WarningViewModel,
    message: String
) {
    LaunchedEffect(snackbarHostState) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
        when (result) {
            SnackbarResult.ActionPerformed -> {}
            SnackbarResult.Dismissed -> {
                warningViewModel.setStopLocationSnackBarVisibility(false)
                warningViewModel.setStartLocationSnackBarVisibility(false)
            }
        }
    }
}