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

    fun sendMessage(content: String, senderUserId: String) {
        val timestamp = System.currentTimeMillis()
        val userMessage = Message(
            messageId = generateMessageId(),
            senderUserId = senderUserId,
            content = content,
            timestamp = timestamp
        )

        val currentMessages = _messages.value ?: emptyList()
        _messages.value = currentMessages + userMessage

        viewModelScope.launch {
            val response = repository.getResponseFromLlama3(content)
            Log.d("ChatbotViewModel", "Response from Llama3: $response")

            val botMessage = Message(
                messageId = generateMessageId(),
                senderUserId = "bot",
                content = response,
                timestamp = System.currentTimeMillis()
            )
            _messages.value = _messages.value?.plus(botMessage)
        }
    }

    private fun generateMessageId(): String {
        return System.currentTimeMillis().toString() // Simple ID generation; replace as needed
    }
}
