package com.example.josephwanis.reportingsystem.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.viewmodels.UserProfileViewModel

@Composable
fun UserProfileScreen(userProfileViewModel: UserProfileViewModel) {
    var displayNameState by remember { mutableStateOf("") }

    // Observe the userProfile LiveData
    val userProfile by userProfileViewModel.userProfile.observeAsState()

    // Observe the updateSuccess LiveData to display success message
    val updateSuccess by userProfileViewModel.updateSuccess.observeAsState()

    // Observe the errorMessage LiveData to display error message
    val errorMessage by userProfileViewModel.errorMessage.observeAsState()

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
        }

        // Display error message
        if (!errorMessage.isNullOrBlank()) {
            Text(text = errorMessage!!)
        }
    }
}