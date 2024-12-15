package com.example.josephwanis.reportingsystem.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.josephwanis.reportingsystem.data.models.ChatSession
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.ChatListViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(navController: NavHostController, userId: String, isKnownUser: Boolean) {
    val firebaseAuth = FirebaseAuthManager
    val userRepository = UserRepository(firebaseAuth)
    val chatRepository = ChatRepository(userRepository)

    val chatListViewModel = ChatListViewModel(chatRepository, userRepository)

    // Fetch chat sessions for the user
    chatListViewModel.getChatSessionsForUser(userId, isKnownUser)
    val chatSessions by chatListViewModel.chatSessions.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chat Sessions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                IconButton(onClick = { navController.navigate("userProfile/$userId") }) {
                    Icon(Icons.Default.Person, contentDescription = "User Profile")
                }
                IconButton(onClick = { navController.navigate("settings/$userId") }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
                IconButton(onClick = { navController.navigate("chatbot/$userId") }) {
                    Icon(Icons.Default.Android, contentDescription = "Chatbot")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Active Conversations",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChatList(chatSessions, navController, userId, userRepository)
        }
    }
}

@Composable
fun ChatList(
    chatSessions: List<ChatSession>?,
    navController: NavController,
    userId: String,
    userRepository: UserRepository
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(chatSessions ?: emptyList()) { chatSession ->
            ChatListItem(navController, chatSession, userId, userRepository)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListItem(
    navController: NavController,
    chatSession: ChatSession,
    userId: String,
    userRepository: UserRepository
) {
    val participants = remember { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(chatSession.participants) {
        val participantUsernames = chatSession.participants.mapNotNull { participantId ->
            userRepository.getUserProfile(participantId)?.displayName
        }
        participants.value = participantUsernames
    }

    Card(
        onClick = {
            val chatSessionID = chatSession.sessionId
            navController.navigate("chat/$chatSessionID/$userId")
        },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = chatSession.lastMessage?.content ?: "No messages yet",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Participants: ${participants.value.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
