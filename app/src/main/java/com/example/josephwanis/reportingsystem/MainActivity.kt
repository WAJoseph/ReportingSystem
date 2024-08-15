package com.example.josephwanis.reportingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.josephwanis.reportingsystem.data.viewmodels.AppViewModel
import com.example.josephwanis.reportingsystem.ui.screens.BlockedUsersScreen
import com.example.josephwanis.reportingsystem.ui.screens.ChatListScreen
import com.example.josephwanis.reportingsystem.ui.screens.ChatScreen
import com.example.josephwanis.reportingsystem.ui.screens.LoginScreen
import com.example.josephwanis.reportingsystem.ui.screens.RegistrationScreen
import com.example.josephwanis.reportingsystem.ui.screens.SettingsScreen
import com.example.josephwanis.reportingsystem.ui.screens.UserProfileScreen
import com.example.josephwanis.reportingsystem.ui.theme.ReportingSystemTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase here
        FirebaseApp.initializeApp(this)

        val appViewModel: AppViewModel by viewModels()

        setContent {
            ReportingSystemTheme {
                // Create a NavController instance using rememberNavController
                val navController = rememberNavController()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass the NavController to the NavHost
                    ReportingSystemNavHost(navController, appViewModel)
                }
            }
        }
    }
}

@Composable
fun ReportingSystemNavHost(navController: NavHostController, appViewModel: AppViewModel) {
    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController, appViewModel)
        }
        composable("registration") {
            RegistrationScreen(navController, appViewModel)
        }
        composable("chatList/{userId}/{isKnown}") { backStackEntry->
            // Retrieve the userId argument from the previous screen
            val userId = backStackEntry.arguments?.getString("userId")
            val isKnown = backStackEntry.arguments?.getBoolean("isKnown")
            if (userId != null && isKnown != null) {
                    ChatListScreen(navController, userId, isKnown)
                }

        }
        composable("chat/{chatSessionId}/{userId}") {backStackEntry->
            // Retrieve the chatSessionId argument from the previous screen
            val chatSessionId = backStackEntry.arguments?.getString("chatSessionId") ?: ""
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ChatScreen(navController, chatSessionId, userId )
            }
        }
        composable("blockedUsers/{userId}") {backStackEntry->
            // Retrieve the userId argument from the previous screen
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                BlockedUsersScreen(userId, navController)
            }
        }
        composable("userProfile/{userId}") {backStackEntry->
            // Retrieve the userId argument from the previous screen
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserProfileScreen(navController,userId)
            }
        }
        composable("settings/{userId}") {backStackEntry->
            // Retrieve the userId argument from the previous screen
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                SettingsScreen(userId, navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ReportingSystemTheme {
        // You can add any composable here for preview if needed
    }
}