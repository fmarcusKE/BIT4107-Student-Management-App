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

data class StudyNote(
    val id: String,
    val title: String,
    val content: String,
    val category: String = "General"
)

data class Student(
    val id: String,
    val name: String,
    val studentId: String
)

data class AttendanceRecord(
    val studentId: String,
    val date: String,
    val isPresent: Boolean
)

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
    object StudyNotes : Screen() // For Entity Records (CRUD)
    object ApiRecords : Screen() // For API Consumer
    object AcademicResults : Screen() // For Student Results (Network Ops)
    object Attendance : Screen() // For Attendance Management
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
    val studyNotes = remember { mutableStateListOf<StudyNote>() }
    val students = remember { mutableStateListOf<Student>(
        Student("1", "John Doe", "S001"),
        Student("2", "Jane Smith", "S002"),
        Student("3", "Alex Johnson", "S003")
    ) }
    val attendanceRecords = remember { mutableStateListOf<AttendanceRecord>() }

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
        is Screen.StudyNotes -> StudyNotesScreen(
            notes = studyNotes,
            onAddNote = { title: String, content: String ->
                studyNotes.add(StudyNote(System.currentTimeMillis().toString(), title, content))
            },
            onUpdateNote = { id: String, title: String, content: String ->
                val index = studyNotes.indexOfFirst { it.id == id }
                if (index != -1) {
                    studyNotes[index] = studyNotes[index].copy(title = title, content = content)
                }
            },
            onDeleteNote = { note: StudyNote -> studyNotes.remove(note) },
            onNavigate = { screen: Screen -> currentScreen = screen },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.ApiRecords -> ApiRecordsScreen(
            onNavigate = { screen: Screen -> currentScreen = screen },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.AcademicResults -> AcademicResultsScreen(
            onNavigate = { screen: Screen -> currentScreen = screen },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.Attendance -> AttendanceScreen(
            students = students,
            records = attendanceRecords,
            onAddStudent = { name: String, sid: String -> students.add(Student(System.currentTimeMillis().toString(), name, sid)) },
            onRecordAttendance = { sid: String, date: String, present: Boolean ->
                attendanceRecords.removeAll { it.studentId == sid && it.date == date }
                attendanceRecords.add(AttendanceRecord(sid, date, present))
            },
            onNavigate = { screen: Screen -> currentScreen = screen },
            onLogout = { currentScreen = Screen.Login }
        )
    }
}

// ====================== LOGIN SCREEN ======================
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                        onValueChange = { newValue -> email = newValue },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { newValue -> password = newValue },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    val authState = viewModel.authUiState
                    if (authState is AuthUiState.Error) {
                        Text(
                            authState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.login(email, password) { success ->
                                if (success) onLoginSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = authState !is AuthUiState.Loading
                    ) {
                        if (authState is AuthUiState.Loading) {
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ModuleCard(Modifier.weight(1f), "Study Planner", Icons.Default.EditNote, "Manage study tasks (CRUD)") { onNavigate(Screen.StudyNotes) }
                        ModuleCard(Modifier.weight(1f), "Academic Results", Icons.Default.Assessment, "View & Download Transcript") { onNavigate(Screen.AcademicResults) }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ModuleCard(Modifier.weight(1f), "Global Resources", Icons.Default.Public, "Fetched from REST API") { onNavigate(Screen.ApiRecords) }
                        ModuleCard(Modifier.weight(1f), "Attendance", Icons.Default.Checklist, "Manage student records") { onNavigate(Screen.Attendance) }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ModuleCard(Modifier.weight(1f), "Career Assistant", Icons.Default.Work, "AI career guidance") { onNavigate(Screen.CareerAssistant) }
                        Spacer(modifier = Modifier.weight(1f))
                    }
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

// ====================== STUDY NOTES (ENTITY CRUD) ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyNotesScreen(
    notes: List<StudyNote>,
    onAddNote: (String, String) -> Unit,
    onUpdateNote: (String, String, String) -> Unit,
    onDeleteNote: (StudyNote) -> Unit,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<StudyNote?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredNotes = notes.filter { 
        it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(currentScreen = Screen.StudyNotes, onScreenSelected = { s -> scope.launch { drawerState.close(); onNavigate(s) } }, onLogout = onLogout)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Study Planner") },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, "Add Note") }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredNotes) { note ->
                        Card(modifier = Modifier.fillMaxWidth().clickable { editingNote = note }) {
                            ListItem(
                                headlineContent = { Text(note.title, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text(note.content, maxLines = 2) },
                                trailingContent = {
                                    IconButton(onClick = { onDeleteNote(note) }) {
                                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            NoteDialog(onDismiss = { showAddDialog = false }, onConfirm = { t, c -> onAddNote(t, c) })
        }
        if (editingNote != null) {
            NoteDialog(
                initialTitle = editingNote!!.title,
                initialContent = editingNote!!.content,
                onDismiss = { editingNote = null },
                onConfirm = { t, c -> onUpdateNote(editingNote!!.id, t, c) }
            )
        }
    }
}

@Composable
fun NoteDialog(initialTitle: String = "", initialContent: String = "", onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTitle.isEmpty()) "Add Note" else "Edit Note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.height(120.dp))
            }
        },
        confirmButton = { Button(onClick = { onConfirm(title, content); onDismiss() }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// ====================== API RECORDS (API CONSUMER) ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiRecordsScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit, viewModel: PostViewModel = viewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(currentScreen = Screen.ApiRecords, onScreenSelected = { s -> scope.launch { drawerState.close(); onNavigate(s) } }, onLogout = onLogout)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Global Shared Resources") },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                when (val state = viewModel.postUiState) {
                    is PostUiState.Loading -> CircularProgressIndicator()
                    is PostUiState.Error -> Text("Failed to load records.")
                    is PostUiState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.posts) { post ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(post.title, fontWeight = FontWeight.Bold)
                                        Text(post.body, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ====================== ACADEMIC RESULTS (NETWORK OPS) ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicResultsScreen(
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit,
    viewModel: ResultViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(
                    currentScreen = Screen.AcademicResults,
                    onScreenSelected = { s -> scope.launch { drawerState.close(); onNavigate(s) } },
                    onLogout = onLogout
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Academic Performance") },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } },
                    actions = {
                        IconButton(onClick = { viewModel.downloadTranscript() }) {
                            Icon(Icons.Default.Download, "Download Transcript")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                when (val state = viewModel.resultUiState) {
                    is ResultUiState.Loading -> CircularProgressIndicator()
                    is ResultUiState.Error -> Text("Failed to retrieve results.")
                    is ResultUiState.Success -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Official Transcript Available", fontWeight = FontWeight.Bold)
                                    Text("You can download your full transcript using the icon above.", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(state.results) { result ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        ListItem(
                                            headlineContent = { Text(result.subject, fontWeight = FontWeight.Bold) },
                                            supportingContent = { Text(result.semester) },
                                            trailingContent = {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer
                                                ) {
                                                    Text(
                                                        text = "${result.score}% (${result.grade})",
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ====================== ATTENDANCE MANAGEMENT ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    students: List<Student>,
    records: List<AttendanceRecord>,
    onAddStudent: (String, String) -> Unit,
    onRecordAttendance: (String, String, Boolean) -> Unit,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("2023-10-27") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(currentScreen = Screen.Attendance, onScreenSelected = { s -> scope.launch { drawerState.close(); onNavigate(s) } }, onLogout = onLogout)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Attendance Management") },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddStudentDialog = true }) { Icon(Icons.Default.PersonAdd, "Add Student") }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("Daily Attendance - $selectedDate", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    items(students) { student ->
                        val isPresent = records.any { it.studentId == student.studentId && it.date == selectedDate && it.isPresent }
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(student.name, fontWeight = FontWeight.Bold)
                                    Text("ID: ${student.studentId}", style = MaterialTheme.typography.bodySmall)
                                }
                                Switch(
                                    checked = isPresent,
                                    onCheckedChange = { onRecordAttendance(student.studentId, selectedDate, it) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Attendance Report Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                    val presentCount = records.filter { it.date == selectedDate && it.isPresent }.size
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Students: ${students.size}")
                        Text("Present: $presentCount")
                        Text("Absent: ${students.size - presentCount}")
                    }
                }
            }
        }

        if (showAddStudentDialog) {
            AddStudentDialog(onDismiss = { showAddStudentDialog = false }, onConfirm = { n, sid -> onAddStudent(n, sid) })
        }
    }
}

@Composable
fun AddStudentDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Student") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
                OutlinedTextField(value = studentId, onValueChange = { studentId = it }, label = { Text("Student ID") })
            }
        },
        confirmButton = { Button(onClick = { onConfirm(name, studentId); onDismiss() }) { Text("Register") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
