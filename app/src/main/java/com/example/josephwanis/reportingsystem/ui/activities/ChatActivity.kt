package com.example.josephwanis.reportingsystem.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.ChatViewModel
import com.example.josephwanis.reportingsystem.ui.composables.ChatScreen


class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract chat session ID from intent or arguments (Replace with actual implementation)
        val chatSessionId = intent.getStringExtra("chatSessionId") ?: ""

        val chatRepository = ChatRepository()
        val chatViewModel = ChatViewModel(chatRepository)

        setContent {
            ChatScreen(chatSessionId, chatViewModel)
        }
    }
}