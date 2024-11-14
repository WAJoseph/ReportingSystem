package com.example.josephwanis.reportingsystem.data.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.josephwanis.reportingsystem.data.models.Message
import com.example.josephwanis.reportingsystem.data.repositories.ChatbotRepository
import kotlinx.coroutines.launch

class ChatbotViewModel(private val context: Context) : ViewModel() {
    private val repository = ChatbotRepository(context)

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> get() = _messages

    private val conversationHistory = mutableListOf<Message>()

    fun sendMessage(content: String, senderUserId: String) {
        // Add user's message
        val userMessage = createMessage(content, senderUserId)
        addMessageToHistory(userMessage)

        // Fetch the bot's response
        viewModelScope.launch {
            try {
                val response = repository.getResponseFromLlama3(content, conversationHistory)
                Log.d("ChatbotViewModel", "Response from Llama3: $response")

                val botMessage = createMessage(response, "bot")
                addMessageToHistory(botMessage)

            } catch (e: Exception) {
                Log.e("ChatbotViewModel", "Error retrieving response: ${e.message}")

                // Handle failure by adding a bot error message
                val errorMessage = createMessage("I'm sorry, there was an issue connecting to the server.", "bot")
                addMessageToHistory(errorMessage)
            }
        }
    }

    private fun addMessageToHistory(message: Message) {
        conversationHistory.add(message)
        _messages.value = conversationHistory.toList()
    }

    private fun createMessage(content: String, senderUserId: String): Message {
        return Message(
            messageId = generateMessageId(),
            senderUserId = senderUserId,
            content = content,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun generateMessageId(): String {
        return System.currentTimeMillis().toString() // Simple ID generation; replace as needed
    }
}
