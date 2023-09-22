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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.AppViewModel
import com.example.josephwanis.reportingsystem.data.viewmodels.LoginResult
import com.example.josephwanis.reportingsystem.data.viewmodels.LoginViewModel
import com.example.josephwanis.reportingsystem.ui.composables.IconTextField

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavHostController, appViewModel: AppViewModel) {

    val firebaseAuth = FirebaseAuthManager
    val userRepository = UserRepository(firebaseAuth)
    val context = LocalContext.current
    val loginViewModel = remember {
        LoginViewModel(context, userRepository ,appViewModel)
    }
    val loginPreferences = loginViewModel.getLoginPreferences()


    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }

    if (loginPreferences.first != null && loginPreferences.second != null) {
        emailState = loginPreferences.first!!
        passwordState = loginPreferences.second!!
    }

    // Observe the loginResult LiveData
    val loginResult by loginViewModel.loginResult.observeAsState()


    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.login_title)) }) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(160.dp))

            // Email Text Field
            IconTextField(
                icon = Icons.Default.Email,
                placeholder = stringResource(id = R.string.email),
                textField = {
                    BasicTextField(
                        value = emailState,
                        onValueChange = { if (it.length <= 64) emailState = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        textStyle = typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        decorationBox = @Composable { innerTextField ->
                            if (emailState.isEmpty()) {
                                Text(stringResource(id = R.string.email), color = Color.Gray)
                            } else {
                                innerTextField()
                            }
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Text Field
            IconTextField(
                icon = Icons.Default.Lock,
                placeholder = stringResource(id = R.string.password),
                textField = {
                    BasicTextField(
                        value = passwordState,
                        onValueChange = { if(it.length <= 64 ) passwordState = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        decorationBox = @Composable { innerTextField ->
                            if (passwordState.isEmpty()) {
                                Text(stringResource(id = R.string.password), color = Color.Gray)
                            } else {
                                innerTextField()
                            }
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = {
                    val email = emailState
                    val password = passwordState
                    // Call the loginUser function in LoginViewModel passing the email and password
                    loginViewModel.loginUser(email, password)
                    loginViewModel.saveLoginPreferences(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = stringResource(id = R.string.login_button))
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Register Button
            Button(
                onClick = {
                   navController.navigate("registration")// {
                     //  launchSingleTop = true
                  //  }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(text = stringResource(id = R.string.register_button))
            }

            // Display the login result
            when (val result = loginResult) {
                is LoginResult.Loading -> {
                    // Show loading indicator
                    CircularProgressIndicator()
                }
                is LoginResult.Success -> {
                    // Get the userId from the LoginResult.Success object
                    val userId = result.user.userId
                    val isKnown = result.user.isKnown
                    // Navigate to chatList destination with userId as an argument
                    navController.navigate("chatList/$userId/$isKnown") {
                        launchSingleTop = true
                        popUpTo("login") { inclusive = true } // Clear the back stack up to login screen
                    }
                }
                is LoginResult.Error -> {
                    // Show error message
                    Text(text = "Error: ${result.error}")
                }
                null -> {
                    Text(text = "Login result is null")
                }
            }
        }
    }
}
