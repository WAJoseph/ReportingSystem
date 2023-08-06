package com.example.josephwanis.reportingsystem.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.SettingsViewModel
import com.example.josephwanis.reportingsystem.ui.composables.IconTextField

@Composable
fun SettingsScreen(userId: String, navController: NavController) {
    val userRepository = UserRepository(FirebaseAuthManager) // Instantiate UserRepository

    val settingsViewModel = SettingsViewModel(userRepository)

    // Observe the updateSuccess LiveData to display success message
    val updateSuccess by settingsViewModel.updateSuccess.observeAsState()

    // Observe the errorMessage LiveData to display error message
    val errorMessage by settingsViewModel.errorMessage.observeAsState()

    // Use TextFieldValues to wrap the String values
    var displayNameState by remember { mutableStateOf(settingsViewModel.displayNameState.value?.let {
        TextFieldValue(
            it
        )
    }) }
    var emailState by remember { mutableStateOf(settingsViewModel.emailState.value?.let {
        TextFieldValue(
            it
        )
    }) }
    var passwordState by remember { mutableStateOf(settingsViewModel.passwordState.value?.let {
        TextFieldValue(
            it
        )
    }) }

    // Call the necessary functions to initialize the text fields
    LaunchedEffect(Unit) {
        displayNameState?.let { settingsViewModel.onDisplayNameChange(it.text) }
        emailState?.let { settingsViewModel.onEmailChange(it.text) }
        passwordState?.let { settingsViewModel.onPasswordChange(it.text) }
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
                displayNameState?.let {
                    BasicTextField(
                        value = it,
                        onValueChange = {
                            displayNameState = it
                            settingsViewModel.onDisplayNameChange(it.text) // Update the view model's displayNameState
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        decorationBox = @Composable { innerTextField ->
                            if (displayNameState!!.text.isEmpty()) {
                                Text(stringResource(id = R.string.display_name), color = Color.Gray)
                            } else {
                                innerTextField()
                            }
                        }
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Text Field
        IconTextField(
            icon = Icons.Default.Email,
            placeholder = stringResource(id = R.string.email),
            textField = {
                emailState?.let {
                    BasicTextField(
                        value = it,
                        onValueChange = {
                            emailState = it
                            settingsViewModel.onEmailChange(it.text) // Update the view model's emailState
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        decorationBox = @Composable { innerTextField ->
                            if (emailState!!.text.isEmpty()) {
                                Text(stringResource(id = R.string.email), color = Color.Gray)
                            } else {
                                innerTextField()
                            }
                        }
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Text field
        IconTextField(
            icon = Icons.Default.Lock,
            placeholder = stringResource(id = R.string.password),
            textField = {
                passwordState?.let {
                    BasicTextField(
                        value = it,
                        onValueChange = {
                            passwordState = it
                            settingsViewModel.onPasswordChange(it.text) // Update the view model's passwordState
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        decorationBox = @Composable { innerTextField ->
                            if (passwordState!!.text.isEmpty()) {
                                Text(stringResource(id = R.string.password), color = Color.Gray)
                            } else {
                                innerTextField()
                            }
                        }
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                displayNameState?.let { settingsViewModel.updateUserProfile(userId, it.text, null) } // Replace `null` with the profile image URI if needed
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

        // Blocked Users Button
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Navigate to the Blocked Users screen with the necessary arguments
                navController.navigate("blockedUsers/$userId")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Filled.Block, contentDescription = "Blocked Users")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.blocked_users))
            }
        }

    }
}