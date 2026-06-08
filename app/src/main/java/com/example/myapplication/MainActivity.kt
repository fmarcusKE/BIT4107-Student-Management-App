package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.myapplication.ui.theme.MyApplicationTheme

data class Student(val id: String, val name: String, val email: String = "", val course: String = "")

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
    object Profile : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StudentManagementApp()
            }
        }
    }
}

@Composable
fun StudentManagementApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    val students = remember { mutableStateListOf<Student>() }

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
            students = students,
            onAddStudent = { name, email, course ->
                students.add(Student(
                    id = (students.size + 1).toString(),
                    name = name,
                    email = email,
                    course = course
                ))
            },
            onLogout = { currentScreen = Screen.Login }
        )
        is Screen.Profile -> ProfileScreen(onBack = { currentScreen = Screen.Dashboard })
    }
}

// ====================== LOGIN SCREEN ======================
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onRegisterClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF4A69E2)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Welcome Back", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Sign in to continue", color = Color.White.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            if (error.isNotEmpty()) {
                Text(error, color = Color.Yellow, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onLoginSuccess()
                    } else {
                        error = "Please fill all fields"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("LOGIN", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = onRegisterClick) {
                Text("Create New Account", color = Color.White)
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

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = { if (name.isNotBlank() && email.isNotBlank()) onRegisterSuccess() }, modifier = Modifier.fillMaxWidth()) {
                Text("REGISTER")
            }
        }
    }
}

// ====================== DASHBOARD ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    students: MutableList<Student>,
    onAddStudent: (String, String, String) -> Unit,
    onLogout: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newCourse by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EduManage Dashboard") },
                actions = {
                    IconButton(onClick = onLogout) { Icon(Icons.AutoMirrored.Filled.Logout, "Logout") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Student")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Total Students: ${students.size}",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(students) { student ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        ListItem(
                            headlineContent = { Text(student.name) },
                            supportingContent = { Text("${student.email} • ${student.course}") }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Student") },
            text = {
                Column {
                    TextField(value = newName, onValueChange = { newName = it }, label = { Text("Name") })
                    TextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email") })
                    TextField(value = newCourse, onValueChange = { newCourse = it }, label = { Text("Course") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newName.isNotBlank()) {
                        onAddStudent(newName, newEmail, newCourse)
                        newName = ""; newEmail = ""; newCourse = ""
                        showAddDialog = false
                    }
                }) { Text("Add") }
            }
        )
    }
}

// ====================== PROFILE ======================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Profile") }, navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(120.dp))
            Text("Marcus Kipchoge", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("BIT4107 Student", color = Color.Gray)
        }
    }
}
