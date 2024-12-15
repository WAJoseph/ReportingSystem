package com.example.josephwanis.reportingsystem.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.josephwanis.reportingsystem.R
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.repositories.ChatRepository
import com.example.josephwanis.reportingsystem.data.repositories.UserRepository
import com.example.josephwanis.reportingsystem.data.viewmodels.AppViewModel
import com.example.josephwanis.reportingsystem.data.viewmodels.RegistrationResult
import com.example.josephwanis.reportingsystem.data.viewmodels.RegistrationViewModel
import com.example.josephwanis.reportingsystem.ui.composables.IconTextField
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButtonColors
import androidx.compose.ui.text.style.TextAlign
import com.example.josephwanis.reportingsystem.ui.composables.CoolRadioButtonGroup

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegistrationScreen(navController: NavHostController, appViewModel: AppViewModel) {
    val firebaseAuth = FirebaseAuthManager
    val userRepository = UserRepository(firebaseAuth)
    val context = LocalContext.current
    val chatRepository = ChatRepository(userRepository)
    val registrationViewModel = remember {
        RegistrationViewModel(userRepository, chatRepository, appViewModel)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Use remember to create mutable states for input fields
    val displayNameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isKnownUser by remember { mutableStateOf(false) }

    // Observe the registrationResult LiveData
    val registrationResult by registrationViewModel.registrationResult.observeAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.register_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                // Welcome Text
                Text(
                    text = "Create an Account",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Text(
                    text = "Sign up to get started",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Display Name Text Field
                IconTextField(
                    icon = Icons.Default.Person,
                    placeholder = stringResource(id = R.string.display_name),
                    text = displayNameState,
                    onValueChange = {
                        if (it.length <= 64) displayNameState.value = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Text Field
                IconTextField(
                    icon = Icons.Default.Email,
                    placeholder = stringResource(id = R.string.email),
                    text = emailState,
                    onValueChange = {
                        if (it.length <= 64) emailState.value = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Text Field
                Box {
                    Column {
                        IconTextField(
                            icon = Icons.Default.Lock,
                            placeholder = stringResource(id = R.string.password),
                            text = passwordState,
                            onValueChange = { input ->
                                if (input.length <= 64) passwordState.value = input
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Password
                            ),
                            singleLine = true,
                            visualTransformation = if (isPasswordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible)
                                            Icons.Filled.Visibility
                                        else
                                            Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Strength Bar
                        val strength = evaluatePasswordStrength(passwordState.value)
                        PasswordStrengthBar(strength)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Known User Radio Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "User Type",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // New radio button group integration
                        CoolRadioButtonGroup(
                            selectedOption = isKnownUser,
                            onOptionSelected = { selected -> isKnownUser = selected }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Register Button
                Button(
                    onClick = {
                        val email = emailState.value
                        val password = passwordState.value
                        val displayName = displayNameState.value

                        // Validate inputs before registration
                        if (displayName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Please fill in all fields",
                                    actionLabel = "Dismiss"
                                )
                            }
                        } else {
                            registrationViewModel.registerUser(email, password, displayName, isKnownUser)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.register),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Loading Indicator
                AnimatedVisibility(
                    visible = registrationResult is RegistrationResult.Loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(50.dp)
                    )
                }

                // Handle Registration Result
                when (val result = registrationResult) {
                    is RegistrationResult.Success -> {
                        val userId = result.user.userId
                        val isKnown = result.user.isKnown
                        navController.navigate("chatList/$userId/$isKnown") {
                            launchSingleTop = true
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    is RegistrationResult.Error -> {
                        // Show error using Snackbar
                        LaunchedEffect(result) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Registration Error: ${result.error}",
                                    actionLabel = "Dismiss"
                                )
                            }
                        }
                    }
                    is RegistrationResult.Loading -> {
                        // Already handled by AnimatedVisibility
                    }
                    null -> {
                        // Initial state or no result yet
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Navigation
                Button(
                    onClick = {
                        navController.navigateUp() // or navigate back to login screen
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Already have an account? Login",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Terms of Service Text
                Text(
                    text = "By signing up, you agree to our Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

// Helper Function to Evaluate Password Strength
private fun evaluatePasswordStrength(password: String): Int {
    var strength = 0
    if (password.length >= 8) strength++
    if (password.any { it.isUpperCase() }) strength++
    if (password.any { it.isLowerCase() }) strength++
    if (password.any { it.isDigit() }) strength++
    if (password.any { "!@#$%^&*()-_=+[{]}|;:'\",<.>/?".contains(it) }) strength++
    return strength // Scale of 0 (Weak) to 5 (Strong)
}

// Composable for the Password Strength Bar
@Composable
fun PasswordStrengthBar(strength: Int) {
    val colors = listOf(
        Color.Red, // Weak
        Color.hsl(39F, 1F, 0.5F), // Fair
        Color.Yellow, // Moderate
        Color.Green, // Strong
        Color.Cyan // Very Strong
    )
    val descriptions = listOf("Weak", "Fair", "Moderate", "Strong", "Very Strong")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(strength / 5f) // Dynamic width based on strength
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(colors.getOrElse(strength - 1) { Color.Red })
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = descriptions.getOrElse(strength - 1) { "Weak" },
            style = MaterialTheme.typography.bodySmall.copy(
                color = colors.getOrElse(strength - 1) { Color.Red },
                fontWeight = FontWeight.Bold
            )
        )
    }
}
