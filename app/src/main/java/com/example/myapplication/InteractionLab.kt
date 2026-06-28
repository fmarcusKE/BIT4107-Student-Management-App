package com.example.myapplication

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// OOP: Data Class for Events
data class InteractionEvent(
    val type: String,
    val description: String,
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
)

// OOP: Separate Class for Logging Logic
class InteractionLogger {
    private val _events = mutableStateListOf<InteractionEvent>()
    val events: List<InteractionEvent> get() = _events

    fun log(type: String, description: String) {
        val event = InteractionEvent(type, description)
        _events.add(0, event) // Add to top
        Log.d("InteractionLab", "[$type] $description")
    }

    fun clear() {
        _events.clear()
    }
}

// OOP: Separate Class for Message Display Logic
class MessageService(private val context: android.content.Context) {
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

// OOP: Class for handling Input Logic
class InputProcessor(private val logger: InteractionLogger, private val messenger: MessageService) {
    fun processKeyboardInput(input: String) {
        if (input.isNotBlank()) {
            logger.log("Keyboard", "User typed: $input")
            messenger.showToast("Input Logged: $input")
        }
    }
}

// OOP: Class for handling Gesture Logic
class GestureHandler(private val logger: InteractionLogger, private val messenger: MessageService) {
    fun onSingleTap() {
        logger.log("Gesture", "Single Tap detected")
        messenger.showToast("Single Tap!")
    }

    fun onDoubleTap() {
        logger.log("Gesture", "Double Tap detected")
        messenger.showToast("Double Tap!!")
    }

    fun onLongPress() {
        logger.log("Gesture", "Long Press detected")
        messenger.showToast("Long Press Action")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractionLabScreen(onNavigate: (Screen) -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Instantiate OOP Classes
    val logger = remember { InteractionLogger() }
    val messenger = remember { MessageService(context) }
    val inputProcessor = remember { InputProcessor(logger, messenger) }
    val gestureHandler = remember { GestureHandler(logger, messenger) }

    var textInput by remember { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(
                    currentScreen = Screen.InteractionLab,
                    onScreenSelected = { s -> scope.launch { drawerState.close(); onNavigate(s) } },
                    onLogout = onLogout
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Interaction Lab (Week 8)") },
                    navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, "Menu") } }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Keyboard Input Section
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Keyboard Input", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            label = { Text("Type something...") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { 
                                    inputProcessor.processKeyboardInput(textInput)
                                    textInput = "" 
                                }) {
                                    Icon(Icons.Default.Send, "Log Input")
                                }
                            }
                        )
                    }
                }

                // Gesture Detection Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { gestureHandler.onSingleTap() },
                                onDoubleTap = { gestureHandler.onDoubleTap() },
                                onLongPress = { gestureHandler.onLongPress() }
                            )
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Touch Area: Tap, Double-Tap, or Long Press", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }

                // Event Logs Section
                Text("Interaction Logs", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Card(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.05f))
                ) {
                    if (logger.events.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No events logged yet", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            items(logger.events) { event ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("[${event.timestamp}]", color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                                    Text(event.type, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(event.description, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), fontSize = 12.sp)
                                }
                                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                            }
                        }
                    }
                }
                
                Button(
                    onClick = { logger.clear() },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear Logs")
                }
            }
        }
    }
}
