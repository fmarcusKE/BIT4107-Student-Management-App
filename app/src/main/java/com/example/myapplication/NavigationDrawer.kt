package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
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
            "Edu Pilot",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider()
        
        NavigationDrawerItem(
            label = { Text("Dashboard") },
            icon = { Icon(Icons.Default.Dashboard, null) },
            selected = currentScreen is Screen.Dashboard,
            onClick = { onScreenSelected(Screen.Dashboard) }
        )
        
        NavigationDrawerItem(
            label = { Text("Summarizer") },
            icon = { Icon(Icons.Default.Description, null) },
            selected = currentScreen is Screen.Summarizer,
            onClick = { onScreenSelected(Screen.Summarizer) }
        )
        
        NavigationDrawerItem(
            label = { Text("Quiz Generator") },
            icon = { Icon(Icons.Default.Quiz, null) },
            selected = currentScreen is Screen.QuizGenerator,
            onClick = { onScreenSelected(Screen.QuizGenerator) }
        )
        
        NavigationDrawerItem(
            label = { Text("Flashcards") },
            icon = { Icon(Icons.Default.Style, null) },
            selected = currentScreen is Screen.Flashcards,
            onClick = { onScreenSelected(Screen.Flashcards) }
        )

        NavigationDrawerItem(
            label = { Text("Study Progress") },
            icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, null) },
            selected = currentScreen is Screen.ProgressTracker,
            onClick = { onScreenSelected(Screen.ProgressTracker) }
        )

        NavigationDrawerItem(
            label = { Text("Career Assistant") },
            icon = { Icon(Icons.Default.Work, null) },
            selected = currentScreen is Screen.CareerAssistant,
            onClick = { onScreenSelected(Screen.CareerAssistant) }
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        NavigationDrawerItem(
            label = { Text("Profile") },
            icon = { Icon(Icons.Default.Person, null) },
            selected = currentScreen is Screen.Profile,
            onClick = { onScreenSelected(Screen.Profile) }
        )
        NavigationDrawerItem(
            label = { Text("Settings") },
            icon = { Icon(Icons.Default.Settings, null) },
            selected = currentScreen is Screen.Settings,
            onClick = { onScreenSelected(Screen.Settings) }
        )
        
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        NavigationDrawerItem(
            label = { Text("Logout") },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
            selected = false,
            onClick = onLogout
        )
    }
}
