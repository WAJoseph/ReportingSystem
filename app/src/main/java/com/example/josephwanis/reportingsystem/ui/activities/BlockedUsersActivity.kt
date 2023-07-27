package com.example.josephwanis.reportingsystem.ui.activities

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.models.User
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirestoreManager
import com.example.josephwanis.reportingsystem.data.viewmodels.BlockedUsersViewModel
import com.example.josephwanis.reportingsystem.ui.theme.ReportingSystemTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedUsersActivity(blockedUsersViewModel: BlockedUsersViewModel, userId: String) {
    val blockedUsers by blockedUsersViewModel.blockedUsers.observeAsState(emptyList())
    val loadingState by blockedUsersViewModel.loadingState.observeAsState(false)
    val errorState by blockedUsersViewModel.errorState.observeAsState()


    ReportingSystemTheme {
        Scaffold(
            topBar = { BlockedUsersTopAppBar() },
            content = {
                if (loadingState) {
                    // Show a loading indicator if data is being fetched
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (!errorState.isNullOrBlank()) {
                    // Show an error message if there was an error fetching data
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = errorState!!, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
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

                    // Display the list of blocked users
                    currentUser?.let { user ->
                        BlockedUsersList(blockedUsers, blockedUsersViewModel, user)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedUsersTopAppBar() {

    val topAppBarColors = TopAppBarDefaults.smallTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.surface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )

    TopAppBar(
        title = { Text(stringResource(R.string.blocked_users)) },
        colors = topAppBarColors
    )
}

@Composable
fun BlockedUsersList(blockedUsers: List<User>, blockedUsersViewModel: BlockedUsersViewModel, currentUser: User) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(blockedUsers) { blockedUser ->
            BlockedUserItem(blockedUser, blockedUsersViewModel, currentUser)
            Divider()
        }
    }
}

@Composable
fun BlockedUserItem(blockedUser: User, blockedUsersViewModel: BlockedUsersViewModel, blockingUser: User) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = blockedUser.displayName, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                blockedUsersViewModel.unblockUser(blockingUser.userId, blockedUser.userId)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White // You can adjust the content color as needed
            )
        ) {
            Text(text = stringResource(R.string.unblock))
        }
    }
}