package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawer(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    onLogout: () -> Unit,
) {
    Column {
        Text(
            "EduManage",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium
        )
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Home") },
            selected = currentScreen is Screen.Dashboard,
            onClick = { onScreenSelected(Screen.Dashboard) }
        )
        NavigationDrawerItem(
            label = { Text("Logout") },
            selected = false,
            onClick = onLogout
        )
    }
}
