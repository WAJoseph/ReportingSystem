package com.example.josephwanis.reportingsystem.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.josephwanis.reportingsystem.R

@Composable
fun SettingsScreen() {
    var displayNameState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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

        //Save Button
        Button(
            onClick = {
                // Implement the logic to save the settings here
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}