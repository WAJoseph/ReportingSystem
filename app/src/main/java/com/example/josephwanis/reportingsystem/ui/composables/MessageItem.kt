package com.example.josephwanis.reportingsystem.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MessageItem(message: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Gray,
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .widthIn(max = 200.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = message,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}