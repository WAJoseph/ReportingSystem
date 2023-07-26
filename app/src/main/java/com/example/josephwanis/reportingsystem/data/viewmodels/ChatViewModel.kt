package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.Message
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _chatMessages = MutableLiveData<List<Message>>()
    val chatMessages: LiveData<List<Message>> get() = _chatMessages

    private val _sendMessageSuccess = MutableLiveData<Boolean>()
    val sendMessageSuccess: LiveData<Boolean> get() = _sendMessageSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Function to get chat messages for a specific chat session
    fun getChatMessages(sessionId: String) {
        viewModelScope.launch {
            val messages = chatRepository.getAllMessagesForChatSession(sessionId)
            _chatMessages.value = messages
        }
    }

    // Function to send a message in the chat session
    fun sendMessage(sessionId: String, senderId: String, message: String) {
        viewModelScope.launch {
            val success = chatRepository.sendMessage(sessionId, senderId, message)
            _sendMessageSuccess.value = success
        }
    }

    // Optional: Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}