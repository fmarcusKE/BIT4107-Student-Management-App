package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed interface ResultUiState {
    data class Success(val results: List<StudentResult>) : ResultUiState
    object Error : ResultUiState
    object Loading : ResultUiState
}

class ResultViewModel : ViewModel() {
    var resultUiState: ResultUiState by mutableStateOf(ResultUiState.Loading)
        private set

    init {
        fetchResults()
    }

    fun fetchResults() {
        viewModelScope.launch {
            resultUiState = ResultUiState.Loading
            resultUiState = try {
                // In a real app, we'd fetch from ApiService.getInstance().getResults()
                // Simulating API response for demo purposes
                val mockResults = listOf(
                    StudentResult("1", "Artificial Intelligence", 85, "A", "Semester 1"),
                    StudentResult("2", "Mobile App Development", 90, "A+", "Semester 1"),
                    StudentResult("3", "Database Systems", 78, "B", "Semester 1"),
                    StudentResult("4", "Data Structures", 82, "A-", "Semester 1")
                )
                ResultUiState.Success(mockResults)
            } catch (e: Exception) {
                ResultUiState.Error
            }
        }
    }

    fun downloadTranscript() {
        // Implementation for downloading transcript
        // This would typically involve saving the ResponseBody to a file
    }
}
