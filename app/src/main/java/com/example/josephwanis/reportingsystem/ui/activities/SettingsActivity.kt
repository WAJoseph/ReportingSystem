package com.example.josephwanis.reportingsystem.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.josephwanis.reportingsystem.data.viewmodels.SettingsViewModel
import com.example.josephwanis.reportingsystem.ui.composables.SettingsScreen
import com.example.josephwanis.reportingsystem.ui.theme.ReportingSystemTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ReportingSystemTheme {
                val settingsViewModel: SettingsViewModel = viewModel()

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "settings") {
                        composable("settings") {
                            SettingsScreen()
                        }
                        composable("chatList") {
                            // Navigate to ChatListActivity or any other activity/fragment
                            // You can handle navigation to the desired destination here.
                            // For now, we are simply displaying a Text composable.
                            Text("Chat List Activity")
                        }
                    }
                }
            }
        }
    }
}