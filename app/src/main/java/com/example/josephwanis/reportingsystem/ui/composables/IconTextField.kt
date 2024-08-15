package com.example.josephwanis.reportingsystem.ui.composables

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun IconTextField(
    icon: ImageVector,
    placeholder: String,
    text: MutableState<String>,
    onValueChange: (String) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        value = text.value,
        onValueChange = { newValue ->
            text.value = newValue
            onValueChange(newValue)
        },
        leadingIcon = { Icon(icon, contentDescription = null) },
        placeholder = { Text(placeholder) },
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        textStyle = textStyle,
        modifier = modifier
    )
}






/*@Composable
fun IconTextField(
    icon: ImageVector,
    placeholder: String,
    textField: @Composable () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)){
            if (textField().toString().isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            textField()
        }
    }
}*/

/*@Composable
fun IconTextField(
    icon: ImageVector,
    placeholder: String,
    textField: @Composable () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)){
            // Get the text from the text box.
            val text = textField().text

            // Check if the text is empty.
            if (text.isEmpty()) {
                // Display the placeholder text.
                Text(placeholder)
            } else {
                // Display the text from the text box.
                textField()
            }
        }
    }
}*/




