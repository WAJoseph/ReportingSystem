package com.example.josephwanis.reportingsystem.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.ChatViewModel
import com.example.josephwanis.reportingsystem.ui.composables.MessageItem


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController, chatSessionId: String, userId: String) {
    val firebaseAuth = FirebaseAuthManager
    val userRepository = UserRepository(firebaseAuth)
    val chatRepository = ChatRepository(userRepository)
    val chatViewModel = ChatViewModel(chatRepository)

    chatViewModel.getChatMessages(chatSessionId)
    val chatMessages by chatViewModel.chatMessages.observeAsState()
    val newMessageState = remember { mutableStateOf(TextFieldValue()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.chat_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = false,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                chatMessages?.let { messages ->
                    itemsIndexed(messages) { _, message ->
                        val senderId = message.senderUserId
                        val userDisplayName = remember(senderId){mutableStateOf<String?>(null) }
                        LaunchedEffect(senderId){
                            val displaName = userRepository.getUserProfile(senderId)?.displayName ?: "Unknown"
                            userDisplayName.value = displaName
                        }
                        MessageItem(userDisplayName.value ?: "Unknown", message.content)
                    }
                }
            }

            Divider(modifier = Modifier.fillMaxWidth())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(0.dp, Color.Transparent)
                    .drawBehind {

                        val strokeWidth = 1 * density
                        val y = size.height - strokeWidth / 2

                        drawLine(
                            Color.LightGray,
                            Offset(0f, y.toFloat()),
                            Offset(size.width/2, y.toFloat()),
                            strokeWidth
                        )
                    }
            ) {
                TextField(
                    value = newMessageState.value,
                    onValueChange = { newValue ->
                        if(newValue.text.length <= 600) {
                            newMessageState.value = newValue
                        }
                    },
                    shape = RoundedCornerShape(30),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            chatViewModel.sendMessage(chatSessionId, userId, newMessageState.value.text)
                            newMessageState.value = TextFieldValue()
                        }
                    ),
                    placeholder = { Text(text = "Type your message...", color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp).border(1.dp, Color.LightGray, shape = RoundedCornerShape(30))
                )

                Button(
                    onClick = {
                        chatViewModel.sendMessage(chatSessionId, userId, newMessageState.value.text)
                        newMessageState.value = TextFieldValue()
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                ) {
                    Text(text = "Send")
                }
            }
        }
    }
}