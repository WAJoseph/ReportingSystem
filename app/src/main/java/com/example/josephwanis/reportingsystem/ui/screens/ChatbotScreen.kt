package com.example.josephwanis.reportingsystem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.josephwanis.reportingsystem.data.models.Message
import com.example.josephwanis.reportingsystem.data.viewmodels.ChatbotViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(navController: NavHostController, viewModel: ChatbotViewModel = viewModel(), userId: String) {
    val messages by viewModel.messages.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chatbot") },
                actions = {
                    IconButton(onClick = { /* Handle settings or other actions */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            var inputText by remember { mutableStateOf("") }

            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1F),
                    placeholder = { Text("Ask a question...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        // You can add more color parameters as needed
                    )

                )
                IconButton(onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText, userId)
                        inputText = ""
                    }
                }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 16.dp),
            contentPadding = PaddingValues(bottom = 56.dp)
        ) {
            items(messages) { message ->
                MessageItem(message = message)
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(message.timestamp))

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = message.content,
            color = if (message.senderUserId == "userId") Color.Blue else Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = formattedTime,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
