package com.example.fullthrottle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fullthrottle.R
import com.example.fullthrottle.data.DataStoreConstants.USERNAME_KEY
import com.example.fullthrottle.viewModel.SettingsViewModel

@Composable
fun ProfileModificationScreen(
    currentUsername: String,
) {
    var currUsername by rememberSaveable { mutableStateOf(currentUsername) }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        SimpleTitle(text = stringResource(id = R.string.profile_modification_title))

        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            val username = OutLineTextField(label = "Username", value = currUsername)
        }
    }
}