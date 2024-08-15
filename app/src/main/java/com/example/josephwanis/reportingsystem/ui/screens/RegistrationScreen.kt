package com.example.josephwanis.reportingsystem.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.AppViewModel
import com.example.josephwanis.reportingsystem.data.viewmodels.RegistrationResult
import com.example.josephwanis.reportingsystem.data.viewmodels.RegistrationViewModel
import com.example.josephwanis.reportingsystem.ui.composables.CoolRadioButtonGroup
import com.example.josephwanis.reportingsystem.ui.composables.IconTextField

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavHostController, appViewModel: AppViewModel) {

    val firebaseAuth = FirebaseAuthManager
    val userRepository = UserRepository(firebaseAuth)
    val chatRepository = ChatRepository(userRepository)
    val registrationViewModel = RegistrationViewModel(userRepository,chatRepository ,appViewModel)

    val displayNameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    var isKnownUser by remember { mutableStateOf(false) }

    // Observe the registrationResult LiveData
    val registrationResult by registrationViewModel.registrationResult.observeAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.register_title)) }) }
    ) { Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        // Display name Text Field
        IconTextField(
            icon = Icons.Default.Person,
            placeholder = stringResource(id = R.string.display_name),
            text = displayNameState,
            onValueChange = { if (it.length <= 64)displayNameState.value = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp)
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Email Text Field
        IconTextField(
            icon = Icons.Default.Email,
            placeholder = stringResource(id = R.string.email),
            text = emailState,
            onValueChange = { if (it.length <= 64) emailState.value = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Text Field
        IconTextField(
            icon = Icons.Default.Lock,
            placeholder = stringResource(id = R.string.password),
            text = passwordState,
            onValueChange = { if(it.length <= 64 ) passwordState.value = it },
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
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CoolRadioButtonGroup(
            selectedOption = isKnownUser,
            onOptionSelected = { isKnownUser = it }
        )

        // Register Button
        Button(
            onClick = {
                val email = emailState.value
                val password = passwordState.value
                val displayName = displayNameState.value
                // call the registerUser function in RegistrationViewModel passing the email, password, and display name
                registrationViewModel.registerUser(email, password, displayName, isKnownUser)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = stringResource(id = R.string.register))
        }

        // Display the registration result
        when (val result = registrationResult) {
            is RegistrationResult.Loading -> {
                // Handle the case when registrationResult is null
                CircularProgressIndicator()
            }
            is RegistrationResult.Success -> {
                val userId = result.user.userId
                val isKnown = result.user.isKnown
                navController.navigate("chatList/$userId/$isKnown"){
                    launchSingleTop = true
                    popUpTo("login") {inclusive = true}
                }
            }
            is RegistrationResult.Error -> {
                // Handle the case when registrationResult is false (registration failed)
                Text(text = result.error)
            }
            null -> {
                Text(text = "Unexpected registration result")
            }
        }
    }}
}
