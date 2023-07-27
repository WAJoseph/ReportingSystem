package com.example.josephwanis.reportingsystem.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.viewmodels.RegistrationViewModel
import com.example.josephwanis.reportingsystem.ui.composables.IconTextField
import com.example.josephwanis.reportingsystem.ui.theme.ReportingSystemTheme

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContent{
            ReportingSystemTheme{
                val registrationViewModel: RegistrationViewModel = viewModel()

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "registration") {
                        composable("registration") {
                            RegistrationScreen(registrationViewModel, navController)
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

@Composable
fun RegistrationScreen(registrationViewModel: RegistrationViewModel, navController: NavHostController){
    var displayNameState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }

    //Observe the registrationResult LiveData
    val registrationResult = registrationViewModel.registrationSuccess.observeAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Spacer(modifier = Modifier.height(32.dp))

        //Display name Text Field
        IconTextField(
            icon = Icons.Default.Person,
            placeholder = stringResource(id = R.string.display_name),
            textField = {
                BasicTextField(
                    value = displayNameState,
                    onValueChange = { displayNameState = it },
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

        //Email Text Field
        IconTextField(
            icon = Icons.Default.Email,
            placeholder = stringResource(id = R.string.email),
            textField = {
                BasicTextField(
                    value = emailState,
                    onValueChange = { emailState = it },
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

        //Password Text field
        IconTextField(
            icon = Icons.Default.Lock,
            placeholder = stringResource(id = R.string.password),
            textField = {
                BasicTextField(
                    value = passwordState,
                    onValueChange = { passwordState = it },
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

        //Register Button
        Button(
            onClick = {
                val email = emailState
                val password = passwordState
                val displayName = displayNameState
                //call the registerUser function in RegistrationViewModel passing the email, password, and display name
                registrationViewModel.registerUser(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = stringResource(id = R.string.register))
        }

        //Display the registration result
        when (registrationResult.value) {
            true -> {
                navController.navigate("chatList")
            }
            false -> {
                // Handle the case when registrationResult is false (registration failed)
                // For example, display an error message or take appropriate action
                Text(text = "Registration failed. Please try again later.")
            }
            null -> {
                // Handle the case when registrationResult is null
                // For example, you can display a loading indicator or handle it as per your requirement
                CircularProgressIndicator()
            }
        }
    }
}

