package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val userId: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel : ViewModel() {
    var authUiState: AuthUiState by mutableStateOf(AuthUiState.Idle)
        private set

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authUiState = AuthUiState.Loading
            // Simulating network operation for Task 6
            delay(1500)
            
            // For demo purposes, any non-empty password works
            if (email.contains("@") && password.length >= 4) {
                authUiState = AuthUiState.Success("user_123")
                onResult(true)
            } else {
                authUiState = AuthUiState.Error("Invalid credentials")
                onResult(false)
            }
        }
    }
}
