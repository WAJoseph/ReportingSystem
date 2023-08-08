import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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

    val chatRepository = ChatRepository()

    val firebaseAuth = FirebaseAuthManager
    val userRepository = UserRepository(firebaseAuth)
    val chatListViewModel = ChatListViewModel(chatRepository, userRepository)

    // Call the getChatSessionsForUser function to fetch chat sessions for the specific user
    chatListViewModel.getChatSessionsForUser(userId, isKnownUser)

    // Observe the chatSessions LiveData
    val chatSessions by chatListViewModel.chatSessions.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chat Sessions") })
        },
        bottomBar = {
            // BottomAppBar with navigation options
            BottomAppBar {
                // Add icons for UserProfile and Settings
                IconButton(
                    onClick = {
                        navController.navigate("userProfile/$userId"){
                            launchSingleTop = true
                        }

                    }
                ) {
                    Icon(Icons.Default.Person, contentDescription = "User Profile")
                }
                IconButton(
                    onClick = {
                        navController.navigate("settings/$userId"){
                            launchSingleTop = true
                        }
                    }
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }
    ) {
        // Content with padding
        ChatList(chatSessions, navController)
    }
}

@Composable
fun ChatList(chatSessions: List<ChatSession>?, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(chatSessions ?: emptyList()) { chatSession ->
            ChatListItem(navController, chatSession)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListItem(navController: NavController, chatSession: ChatSession) {
    Card(
        onClick = {
            // Navigate to the Chat screen passing the chat session ID as an argument
            navController.navigate("chat/${chatSession.sessionId}")
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Text(
                text = "Chat session ID: ${chatSession.sessionId}",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Participants: ${chatSession.participants.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}