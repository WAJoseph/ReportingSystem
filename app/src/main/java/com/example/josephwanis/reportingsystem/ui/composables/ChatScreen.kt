package com.example.josephwanis.reportingsystem.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.josephwanis.reportingsystem.data.models.ChatSession
import com.example.josephwanis.reportingsystem.data.models.User
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirestoreManager
import com.example.josephwanis.reportingsystem.data.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatSessionId: String, chatViewModel: ChatViewModel) {
    // Get the current user ID from FirebaseAuthManager
    val currentUserId = FirebaseAuthManager.getCurrentUserId()

    // Fetch the current user's model from the database using FirestoreManager
    var currentUser by remember(currentUserId) { mutableStateOf<User?>(null) }
    LaunchedEffect(currentUserId) {
        currentUserId?.let { uid ->
            val userMap = FirestoreManager.getDocument("users", uid)
            userMap?.let {
                currentUser = User.fromMap(it)
            }
        }
    }

    // Fetch chat session data using FirestoreManager
    var chatSession by remember(chatSessionId) { mutableStateOf<ChatSession?>(null) }
    LaunchedEffect(chatSessionId) {
        chatSessionId.let { sessionId ->
            val chatSessionMap = FirestoreManager.getDocument("chatSessions", sessionId)
            chatSessionMap?.let {
                chatSession = ChatSession.fromMap(it)
            }
        }
    }

    // Fetch chat messages for the chat session
    chatSession?.sessionId?.let { sessionId ->
        chatViewModel.getChatMessages(sessionId)
    }

    // Observe the chatMessages LiveData
    val chatMessages = chatViewModel.chatMessages.value

    // Display the chat messages
    // You can use the chatMessages data to render the chat UI here

    // For example, you can use a LazyColumn to display the chat messages
    LazyColumn(
        reverseLayout = true,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        chatMessages?.let { messages ->
            itemsIndexed(messages) { index, message ->
                MessageItem(message.content)
            }
        }
    }

    // Handle sending a new message
    // You can use a TextField and a Button to allow the user to type and send messages
    val newMessageState = remember { mutableStateOf(TextFieldValue()) }
    TextField(
        value = newMessageState.value,
        onValueChange = { newValue ->
            newMessageState.value = newValue
        },
        keyboardActions = KeyboardActions(
            onSend = {
                // Send the message
                val message = newMessageState.value.text
                val senderId = currentUser?.userId ?: "" // Replace with appropriate property for the current user's ID
                chatViewModel.sendMessage(chatSession?.sessionId ?: "", senderId, message)

                // Clear the text field after sending the message
                newMessageState.value = TextFieldValue()
            }
        ),
        placeholder = { Text(text = "Type your message...") }
    )

    // Handle the send button click
    Button(
        onClick = {
            // Send the message
            val message = newMessageState.value.text
            val senderId = currentUser?.userId ?: "" // Replace with appropriate property for the current user's ID
            chatViewModel.sendMessage(chatSession?.sessionId ?: "", senderId, message)

            // Clear the text field after sending the message
            newMessageState.value = TextFieldValue()
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = "Send")
    }
}