package com.example.josephwanis.reportingsystem.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.ChatViewModel
import com.example.josephwanis.reportingsystem.ui.composables.MessageItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatSessionId: String, navController: NavHostController) {


    val chatRepository = ChatRepository()
    val chatViewModel = ChatViewModel(chatRepository)

    // Fetch chat messages for the chat session
    LaunchedEffect(chatSessionId) {
        chatViewModel.getChatMessages(chatSessionId)
    }

    // Observe the chatMessages LiveData
    val chatMessages by chatViewModel.chatMessages.observeAsState()

    // Display the chat messages
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
    var newMessageState by remember { mutableStateOf(TextFieldValue()) }
    TextField(
        value = newMessageState,
        onValueChange = { newValue ->
            newMessageState = newValue
        },
        keyboardActions = KeyboardActions(
            onDone = {
                // Send the message
                val message = newMessageState.text
                val senderId = chatViewModel.senderId.value ?: "" // Replace with appropriate property for the current user's ID
                chatViewModel.sendMessage(chatSessionId, senderId, message)

                // Clear the text field after sending the message
                newMessageState = TextFieldValue()
            }
        ),
        placeholder = { Text(text = "Type your message...", color = Color.Gray) },
        modifier = Modifier.padding(8.dp)
    )

    // Handle the send button click
    Button(
        onClick = {
            // Send the message
            val message = newMessageState.text
            val senderId = chatViewModel.senderId.value ?: "" // Replace with appropriate property for the current user's ID
            chatViewModel.sendMessage(chatSessionId, senderId, message)

            // Clear the text field after sending the message
            newMessageState = TextFieldValue()
        },
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = "Send")
    }
}