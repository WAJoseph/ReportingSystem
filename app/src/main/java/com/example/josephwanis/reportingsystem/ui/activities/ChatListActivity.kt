package com.example.josephwanis.reportingsystem.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.josephwanis.reportingsystem.data.models.ChatSession
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.viewmodels.ChatListViewModel

class ChatListActivity : ComponentActivity() {
    private val chatListViewModel: ChatListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChatListScreen(chatListViewModel)
        }

        // Check if the user is signed in
        val currentUserId = FirebaseAuthManager.getCurrentUserId()
        if (currentUserId != null) {
            // Get the user's ID and fetch chat sessions for that user
            chatListViewModel.getChatSessionsForUser(currentUserId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(chatListViewModel: ChatListViewModel) {
    val chatSessions by chatListViewModel.chatSessions.observeAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        // TopBar
        TopAppBar(title = { Text("Chat Sessions") })

        // Content with padding
        ChatList(chatSessions)
    }
}


@Composable
fun ChatList(chatSessions: List<ChatSession>?) {
    LazyColumn {
        items(chatSessions ?: emptyList()) { chatSession ->
            ChatListItem(chatSession)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListItem(chatSession: ChatSession) {
    Card(
        onClick = { /* Handle click event */ },
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(text = "Chat session ID: ${chatSession.sessionId}", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Participants: ${chatSession.participants.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

