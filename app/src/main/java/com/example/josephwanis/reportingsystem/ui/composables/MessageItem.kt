package com.example.josephwanis.reportingsystem.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

data class MessageItemParams(val username: String, val message: String)

class MessageItemParamsProvider : PreviewParameterProvider<MessageItemParams> {
    override val values: Sequence<MessageItemParams> = sequenceOf(
        MessageItemParams(username = "Joseph", message = "Hello, how are you?"),
        MessageItemParams(username = "Anna", message = "I'm fine, thank you!"),
        MessageItemParams(username = "John", message = "What are we working on?")
    )
}

@Preview(showBackground = true)
@Composable
fun MessageItemPreview(
    @PreviewParameter(MessageItemParamsProvider::class) params: MessageItemParams
) {
    MessageItem(username = params.username, message = params.message)
}

@Composable
fun MessageItem(username: String, message: String) {
    val gradient = Brush.linearGradient(
        colors = listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.background)
    )
    OutlinedCard(
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .padding(10.dp)
            .widthIn(max = 319.dp)
            .background(gradient, shape = RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "$username:",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
