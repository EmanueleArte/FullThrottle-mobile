package com.example.fullthrottle.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.fullthrottle.R
import com.example.fullthrottle.data.TabConstants.FOLLOWED_TAB
import com.example.fullthrottle.data.TabConstants.FOLLOWERS_TAB

@Composable
fun FollowersScreen(
    currentTab: Int = FOLLOWERS_TAB
) {
    var tabIndex by remember { mutableStateOf(currentTab) }

    val tabs = listOf(
        stringResource(id = R.string.followers_tab_label),
        stringResource(id = R.string.followed_tab_label)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                )
            }
        }
        when (tabIndex) {
            FOLLOWERS_TAB -> UsersList(FOLLOWERS_TAB)
            FOLLOWED_TAB -> UsersList(FOLLOWED_TAB)
        }
    }
}

@Composable
fun UsersList(currentTab: Int) {


    LazyColumn() {

    }
}