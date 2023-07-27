package com.example.josephwanis.reportingsystem.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.josephwanis.reportingsystem.data.viewmodels.LoginResult
import com.example.josephwanis.reportingsystem.data.viewmodels.LoginViewModel
import com.example.josephwanis.reportingsystem.ui.composables.IconTextField
import com.example.josephwanis.reportingsystem.ui.theme.ReportingSystemTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContent{
            ReportingSystemTheme{
                val loginViewModel: LoginViewModel = viewModel()

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(loginViewModel, navController)
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
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavHostController){
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember {mutableStateOf("")}

    //Observe the loginResult LiveData
    val loginResult = loginViewModel.loginResult.observeAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Spacer(modifier = Modifier.height(32.dp))

        //Email Text Field
        IconTextField(
            icon = Icons.Default.Email,
            placeholder = stringResource(id = R.string.email),
            textField = {
                BasicTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
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
                        if (emailState.value.isEmpty()) {
                            Text(stringResource(id = R.string.email), color = Color.Gray)
                        } else {
                            innerTextField()
                        }
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        //password Text field
        IconTextField(
            icon = Icons.Default.Lock,
            placeholder = "Password",
            textField = {
                BasicTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
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
                        if (passwordState.value.isEmpty()) {
                            Text(stringResource(id = R.string.password), color = Color.Gray)
                        } else {
                            innerTextField()
                        }
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Login Button
        Button(
            onClick = {
                val email = emailState.value
                val password = passwordState.value
                //call the loginUser function in LoginViewModel passing the email and password
                loginViewModel.loginUser(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = "Login")
        }

        //Display the login result
        when (val result = loginResult.value){
            is LoginResult.Loading -> {
                //Show loading indicator
                CircularProgressIndicator()
            }
            is LoginResult.Success -> {
                navController.navigate("chatList")
            }
            is LoginResult.Error -> {
                //Show error message
                Text(text = "Error: ${result.error}")
            }
            null -> {
                Text(text = "Login result is null")
            }
        }
    }
}

//@Composable
//fun IconTextField(
//    icon: ImageVector,
//    placeholder: String,
//    textField: @Composable () -> Unit
//) {
//    Row(verticalAlignment = Alignment.CenterVertically) {
//        Icon(imageVector = icon, contentDescription = null)
//        Spacer(modifier = Modifier.width(8.dp))
//        Box(modifier = Modifier.weight(1f)){
//            textField()
//        }
//    }
//}
