package com.example.josephwanis.reportingsystem.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.ChatSession
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import kotlinx.coroutines.launch

class ChatListViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _chatSessions = MutableLiveData<List<ChatSession>>()
    val chatSessions: LiveData<List<ChatSession>> get() = _chatSessions

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Function to get chat sessions for a specific user
    fun getChatSessionsForUser(userId: String, isKnownUser: Boolean) {
        viewModelScope.launch {
            try {
                // Fetch all chat sessions for the user
                val chatSessions = chatRepository.getChatSessionsForUser(userId)

                // Filter chat sessions based on whether the user is a known user or not
                val filteredChatSessions = if (isKnownUser) {
                    // For known users, show all chat sessions with other known users
                    chatSessions.filter { chatSession ->
                        chatSession.participants.size == 2 && // Check if the chat session has two participants only
                                chatSession.participants.any { participantId ->
                                    val user = userRepository.getUserProfile(participantId)
                                    user?.isKnown == true && !user.blockedByUsers.contains(userId)
                                }
                    }
                } else {
                    // For normal users, show chat sessions with known users only where they are participants
                    chatSessions.filter { chatSession ->
                        chatSession.participants.size == 2 && // Check if the chat session has two participants only
                                chatSession.participants.any { participantId ->
                                    val user = userRepository.getUserProfile(participantId)
                                    user?.isKnown == true && !user.blockedByUsers.contains(userId) && chatSession.isUserParticipant(userId)
                                }
                    }
                }

                _chatSessions.value = filteredChatSessions
            } catch (e: Exception) {
                // Handle any errors that occur during the fetch operation
                _errorMessage.value = "Error fetching chat sessions: ${e.message}"
            }
        }
    }

    // Optional: Function to clear error message when the error is handled in the view
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}