package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed interface PostUiState {
    data class Success(val posts: List<Post>) : PostUiState
    object Error : PostUiState
    object Loading : PostUiState
}

class PostViewModel : ViewModel() {
    var postUiState: PostUiState by mutableStateOf(PostUiState.Loading)
        private set

    init {
        getPosts()
    }

    fun getPosts() {
        viewModelScope.launch {
            postUiState = PostUiState.Loading
            postUiState = try {
                PostUiState.Success(ApiService.getInstance().getPosts())
            } catch (e: Exception) {
                PostUiState.Error
            }
        }
    }
}
