package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
    object Summarizer : Screen()
    object QuizGenerator : Screen()
    object Flashcards : Screen()
    object ProgressTracker : Screen()
    object CareerAssistant : Screen()
    object Profile : Screen()
    object Settings : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EduPilotApp()
            }
        }
    }
}

@Composable
fun EduPilotApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    when (currentScreen) {
        is Screen.Splash -> SplashScreen(onTimeout = { currentScreen = Screen.Login })
        is Screen.Login -> LoginScreen(
            onLoginSuccess = { currentScreen = Screen.Dashboard },
            onRegisterClick = { currentScreen = Screen.Register }
        )
        is Screen.Register -> RegisterScreen(
            onRegisterSuccess = { currentScreen = Screen.Dashboard }
        )
        is Screen.Dashboard -> DashboardScreen(
            onNavigate = { currentScreen = it },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.Summarizer -> SummarizerScreen(
            onNavigate = { currentScreen = it },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.QuizGenerator -> QuizGeneratorScreen(
            onNavigate = { currentScreen = it },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.Flashcards -> FlashcardsScreen(
            onNavigate = { currentScreen = it },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.ProgressTracker -> ProgressTrackerScreen(
            onNavigate = { currentScreen = it },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.CareerAssistant -> CareerAssistantScreen(
            onNavigate = { currentScreen = it },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.Profile -> ProfileScreen(
            onNavigate = { currentScreen = it },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.Settings -> SettingsScreen(
            onNavigate = { currentScreen = it },
            onLogout = { currentScreen = Screen.Login }
        )
    }
}

// ====================== LOGIN SCREEN ======================
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
        Box(contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Edu Pilot", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Your AI-Powered Study Assistant", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { newValue -> email = newValue; error = "" },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = error.isNotEmpty()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { newValue -> password = newValue; error = "" },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = error.isNotEmpty()
                    )

                    if (error.isNotEmpty()) {
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                error = "Please fill in all fields"
                            } else {
                                isLoading = true
                                onLoginSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("LOGIN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    TextButton(onClick = onRegisterClick) {
                        Text("New to Edu Pilot? Register here")
                    }
                }
            }
        }
    }
}

// ====================== REGISTER SCREEN ======================
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Join Edu Pilot", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Start your AI-enhanced study journey", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { newValue -> name = newValue },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Person, null) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { newValue -> email = newValue },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Email, null) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { newValue -> password = newValue },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation()
            )

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.length >= 6) {
                        onRegisterSuccess()
                    } else {
                        error = "Please fill all fields correctly"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CREATE ACCOUNT", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ====================== DASHBOARD ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(
                    currentScreen = Screen.Dashboard,
                    onScreenSelected = { screen ->
                        scope.launch {
                            drawerState.close()
                            onNavigate(screen)
                        }
                    },
                    onLogout = onLogout
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edu Pilot Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.Logout, "Logout") }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Welcome to Edu Pilot!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("What would you like to do today?", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                item {
                    Text("Study Modules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ModuleCard(Modifier.weight(1f), "Summarizer", Icons.Default.Description, "Summarize notes & PDFs") { onNavigate(Screen.Summarizer) }
                        ModuleCard(Modifier.weight(1f), "Quiz Gen", Icons.Default.Quiz, "Generate AI quizzes") { onNavigate(Screen.QuizGenerator) }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ModuleCard(Modifier.weight(1f), "Flashcards", Icons.Default.Style, "Auto flashcards") { onNavigate(Screen.Flashcards) }
                        ModuleCard(Modifier.weight(1f), "Progress", Icons.AutoMirrored.Filled.TrendingUp, "Track your study") { onNavigate(Screen.ProgressTracker) }
                    }
                }

                item {
                    ModuleCard(Modifier.fillMaxWidth(), "Career Assistant", Icons.Default.Work, "AI career guidance") { onNavigate(Screen.CareerAssistant) }
                }
            }
        }
    }
}

@Composable
fun ModuleCard(modifier: Modifier = Modifier, title: String, icon: ImageVector, description: String, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(description, fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

// ====================== FEATURE SCREENS (PLACEHOLDERS) ======================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummarizerScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    FeaturePlaceholderScreen("AI Note Summarizer", "Upload PDFs or paste your lecturer notes to get a concise summary.", Icons.Default.Description, Screen.Summarizer, onNavigate, onLogout)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizGeneratorScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    FeaturePlaceholderScreen("Quiz Generator", "Transform your notes into interactive quizzes automatically.", Icons.Default.Quiz, Screen.QuizGenerator, onNavigate, onLogout)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    FeaturePlaceholderScreen("Smart Flashcards", "Create flashcards from key concepts in your study material.", Icons.Default.Style, Screen.Flashcards, onNavigate, onLogout)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressTrackerScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    FeaturePlaceholderScreen("Study Progress", "Keep track of your learning milestones and study time.", Icons.AutoMirrored.Filled.TrendingUp, Screen.ProgressTracker, onNavigate, onLogout)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerAssistantScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    FeaturePlaceholderScreen("Career Assistant", "AI-powered career guidance and job market insights.", Icons.Default.Work, Screen.CareerAssistant, onNavigate, onLogout)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturePlaceholderScreen(title: String, description: String, icon: ImageVector, screen: Screen, onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(currentScreen = screen, onScreenSelected = { s -> scope.launch { drawerState.close(); onNavigate(s) } }, onLogout = onLogout)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(icon, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(24.dp))
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(description, textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.Gray)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { /* Implement AI logic */ }) {
                    Text("START USING " + title.uppercase())
                }
            }
        }
    }
}

// ====================== PROFILE & SETTINGS (EXISTING BUT UPDATED) ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(currentScreen = Screen.Profile, onScreenSelected = { s -> scope.launch { drawerState.close(); onNavigate(s) } }, onLogout = onLogout)
            }
        }
    ) {
        Scaffold(topBar = { TopAppBar(title = { Text("Profile") }, navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } }) }) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = RoundedCornerShape(60.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(120.dp)) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.padding(20.dp).fillMaxSize(), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Edu Pilot User", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Top Performer", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                Spacer(modifier = Modifier.height(32.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProfileInfoRow(Icons.Default.Email, "Email", "user@edupilot.ai")
                        ProfileInfoRow(Icons.Default.School, "Level", "Undergraduate")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(currentScreen = Screen.Settings, onScreenSelected = { s -> scope.launch { drawerState.close(); onNavigate(s) } }, onLogout = onLogout)
            }
        }
    ) {
        Scaffold(topBar = { TopAppBar(title = { Text("Settings") }, navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } }) }) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AI Preferences", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                ListItem(headlineContent = { Text("Smart Summaries") }, trailingContent = { Switch(checked = true, onCheckedChange = {}) })
                ListItem(headlineContent = { Text("Daily Quizzes") }, trailingContent = { Switch(checked = true, onCheckedChange = {}) })
                HorizontalDivider()
                Text("Account", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                ListItem(headlineContent = { Text("Sign Out", color = Color.Red) }, leadingContent = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red) }, modifier = Modifier.clickable { onLogout() })
            }
        }
    }
}
