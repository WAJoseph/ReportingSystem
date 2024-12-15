package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.Message
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.repositories.AnalyticsBotRepository
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(private val chatRepository: ChatRepository,
                    private val analyticsBotRepository: AnalyticsBotRepository
) : ViewModel() {

    private val _chatMessages = MutableLiveData<List<Message>>()
    val chatMessages: LiveData<List<Message>> get() = _chatMessages

    private val _sendMessageSuccess = MutableLiveData<Boolean>()
    val sendMessageSuccess: LiveData<Boolean> get() = _sendMessageSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> get() = _errorMessage

    private val _senderId = MutableLiveData<String>()
    val senderId: LiveData<String> get() = _senderId

    private val _analysisResult = MutableLiveData<Map<String, Float>>()
    val analysisResult: LiveData<Map<String, Float>> get() = _analysisResult

    private val _analysisError = MutableLiveData<String?>()
    val analysisError: LiveData<String?> get() = _analysisError

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    // Function to update the sender ID in the ViewModel
    fun updateSenderId(senderId: String) {
        _senderId.value = senderId
    }

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
            if (success) {
                // Fetch all messages from chat session
                val messages = chatRepository.getAllMessagesForChatSession(sessionId)
                _chatMessages.value = messages
            }
            _sendMessageSuccess.value = success
        }
    }

    // In ChatViewModel
    fun analyzeChatMessages() {
        // Set loading to true on the main thread
        _isLoading.postValue(true)

        viewModelScope.launch {
            try {
                val messages = _chatMessages.value ?: emptyList()
                if (messages.isEmpty()) {
                    // Use postValue for LiveData updates from background threads
                    _analysisError.postValue("No messages to analyze")
                    _isLoading.postValue(false)
                    return@launch
                }

                val messageContents = messages.map { it.content }
                val validMessages = messageContents.filter { it.isNotBlank() }

                if (validMessages.isEmpty()) {
                    _analysisError.postValue("No meaningful messages to analyze")
                    _isLoading.postValue(false)
                    return@launch
                }

                val result = analyticsBotRepository.analyzeMessagesForChart(validMessages)

                if (result.isEmpty()) {
                    _analysisError.postValue("Unable to generate insights")
                    _isLoading.postValue(false)
                    return@launch
                }

                // Use postValue to ensure thread-safety
                _analysisResult.postValue(result)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _analysisError.postValue("Failed to analyze messages: ${e.localizedMessage}")
                _isLoading.postValue(false)
            }
        }
    }

    // Clear analysis error
    fun clearAnalysisError() {
        _analysisError.value = null
    }

    // Optional: Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}