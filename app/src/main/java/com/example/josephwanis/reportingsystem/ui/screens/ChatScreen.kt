package com.example.josephwanis.reportingsystem.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.repositories.AnalyticsBotRepository
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.ChatViewModel
import android.graphics.Color as AndroidColor
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.foundation.shape.CircleShape


@Composable
fun AnalysisSidebar(
    analysisResult: Map<String, Float>?,
    isLoading: Boolean,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Header with close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chat Analysis",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Analysis",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Handle loading, no analysis, or valid results
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (analysisResult == null || analysisResult.isEmpty()) {
            NoAnalysisAvailable(
                showRetryButton = true,
                onRetry = onRetry
            )
        } else {
            AnalysisContent(analysisResult)
        }
    }
}



@Composable
private fun AnalysisContent(analysisResult: Map<String, Float>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Analytical chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceDim,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            AnalysisChart(analysisResult)
        }

        // Detailed breakdown
        Text(
            text = "Category Breakdown",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        analysisResult.forEach { (category, percentage) ->
            CategoryProgressItem(
                category = category,
                percentage = percentage
            )
        }
    }
}

@Composable
private fun CategoryProgressItem(
    category: String,
    percentage: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        label = "Category Progress"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = "${"%.1f".format(percentage)}%",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun NoAnalysisAvailable(
    showRetryButton: Boolean = false,
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No meaningful insights\nfound in this conversation",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (showRetryButton) {
            Button(onClick = onRetry) {
                Text("Retry Analysis")
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController, chatSessionId: String, userId: String) {
    val firebaseAuth = FirebaseAuthManager
    val userRepository = UserRepository(firebaseAuth)
    val chatRepository = ChatRepository(userRepository)
    val analyticsBotRepository = AnalyticsBotRepository(LocalContext.current)
    val chatViewModel = ChatViewModel(chatRepository, analyticsBotRepository)

    chatViewModel.getChatMessages(chatSessionId)
    val chatMessages by chatViewModel.chatMessages.observeAsState()
    val analysisResult by chatViewModel.analysisResult.observeAsState()
    val isLoading by chatViewModel.isLoading.observeAsState(initial = false)
    val newMessageState = remember { mutableStateOf(TextFieldValue()) }
    val listState = rememberLazyListState()
    val sidebarVisible = remember { mutableStateOf(false) }

    LaunchedEffect(chatMessages) {
        chatMessages?.let { messages ->
            if (messages.isNotEmpty()) {
                listState.scrollToItem(messages.size - 1)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.chat_title),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                sidebarVisible.value = true
                                chatMessages?.let { messages ->
                                    if (messages.isNotEmpty()) {
                                        chatViewModel.analyzeChatMessages()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Analyze Chat"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Chat Messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    chatMessages?.let { messages ->
                        itemsIndexed(messages) { _, message ->
                            val senderId = message.senderUserId
                            val userDisplayName = remember(senderId) { mutableStateOf<String?>(null) }
                            LaunchedEffect(senderId) {
                                userDisplayName.value = userRepository.getUserProfile(senderId)?.displayName ?: "Unknown"
                            }
                            MessageBubble(
                                sender = userDisplayName.value ?: "Unknown",
                                message = message.content,
                                isCurrentUser = senderId == userId
                            )
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.surfaceVariant)

                // Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),
                ) {
                    TextField(
                        value = newMessageState.value,
                        onValueChange = { newValue ->
                            if (newValue.text.length <= 600) {
                                newMessageState.value = newValue
                            }
                        },
                        placeholder = { Text(text = "Type your message...") },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(30),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(30))
                            .padding(end = 8.dp)
                    )

                    Spacer(Modifier.width(20.dp))

                    Button(
                        onClick = {
                            if (newMessageState.value.text.isNotBlank()) {
                                chatViewModel.sendMessage(chatSessionId, userId, newMessageState.value.text)
                                newMessageState.value = TextFieldValue()
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text(text = "Send")
                    }
                }
            }
        }

        // Overlay Sidebar
        AnimatedVisibility(
            visible = sidebarVisible.value,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier
                .fillMaxHeight()
                .width(300.dp)
                .align(Alignment.TopEnd)
        ) {
            AnalysisSidebar(
                analysisResult = analysisResult,
                isLoading = isLoading,
                onRetry = { chatViewModel.analyzeChatMessages() },
                onDismiss = { sidebarVisible.value = false }
            )
        }
    }
}

@Composable
fun AnalysisChart(analysisResult: Map<String, Float>) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PieChart(context).apply {
                val entries = analysisResult.map { (category, percentage) ->
                    PieEntry(percentage, category)
                }

                val dataSet = PieDataSet(entries, "Chat Categories").apply {
                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                    setDrawValues(true) // Show percentage values
                }

                val pieData = PieData(dataSet)

                description.isEnabled = false
                centerText = "Conversation\nInsights"
                setCenterTextSize(14f)
                setDrawEntryLabels(true)
                legend.isEnabled = false
                setUsePercentValues(true)

                data = pieData
                animateY(1000)
            }
        },
        update = { pieChart ->
            val entries = analysisResult.map { (category, percentage) ->
                PieEntry(percentage, category)
            }

            val dataSet = PieDataSet(entries, "Chat Categories").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                setDrawValues(true)
            }

            val pieData = PieData(dataSet)
            pieChart.data = pieData
            pieChart.invalidate() // Redraw chart
        }
    )
}


@Composable
fun MessageBubble(sender: String, message: String, isCurrentUser: Boolean) {
    val backgroundColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = sender,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
            modifier = Modifier
                .padding(8.dp)
                .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        )
    }
}
