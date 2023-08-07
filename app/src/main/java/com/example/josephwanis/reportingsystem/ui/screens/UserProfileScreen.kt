package com.example.josephwanis.reportingsystem.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.UserProfileViewModel
import com.example.josephwanis.reportingsystem.ui.composables.IconTextField

@Composable
fun UserProfileScreen(navController: NavHostController, userId: String) {

    val firebaseAuth = FirebaseAuthManager
    val userRepository = UserRepository(firebaseAuth)
    val userProfileViewModel = UserProfileViewModel(userRepository)
    var displayNameState by remember { mutableStateOf("") }

    // Observe the userProfile LiveData
    val userProfile by userProfileViewModel.userProfile.observeAsState()

    // Observe the updateSuccess LiveData to display success message
    val updateSuccess by userProfileViewModel.updateSuccess.observeAsState()

    // Observe the errorMessage LiveData to display error message
    val errorMessage by userProfileViewModel.errorMessage.observeAsState()

    // Call the getUserProfile function to populate the initial display name
    LaunchedEffect(Unit) {
        userProfileViewModel.getUserProfile(userId)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Display name Text Field
        IconTextField(
            icon = Icons.Default.Person,
            placeholder = stringResource(id = R.string.display_name),
            textField = {
                BasicTextField(
                    value = displayNameState,
                    onValueChange = { displayNameState = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    decorationBox = @Composable { innerTextField ->
                        if (displayNameState.isEmpty()) {
                            Text(stringResource(id = R.string.display_name), color = Color.Gray)
                        } else {
                            innerTextField()
                        }
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                // Get the updated display name from the text field
                val updatedDisplayName = displayNameState.trim()

                // Make sure the display name is not empty
                if (updatedDisplayName.isNotEmpty()) {
                    // Call the updateUserProfile function in UserProfileViewModel
                    userProfile?.let {
                        userProfileViewModel.updateUserProfile(
                            it.userId, updatedDisplayName,
                            userProfile?.profileImageUri
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = stringResource(id = R.string.save))
        }

        // Display success message
        if (updateSuccess == true) {
            Text(text = stringResource(id = R.string.profile_updated))

            // Clear the success message after displaying it
            userProfileViewModel.clearErrorMessage()
        }

        // Display error message
        if (!errorMessage.isNullOrBlank()) {
            Text(text = errorMessage!!)

            // Clear the error message after displaying it
            userProfileViewModel.clearErrorMessage()
        }
    }
}