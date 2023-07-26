package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.ChatSession
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import kotlinx.coroutines.launch

class ChatListViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _chatSessions = MutableLiveData<List<ChatSession>>()
    val chatSessions: LiveData<List<ChatSession>> get() = _chatSessions

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Function to get chat sessions for a specific user
    fun getChatSessionsForUser(userId: String) {
        viewModelScope.launch {
            val chatSessions = chatRepository.getChatSessionsForUser(userId)
            _chatSessions.value = chatSessions
        }
    }

    // Optional: Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}