package com.example.josephwanis.reportingsystem.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MessageItem(username: String, message: String) {
    val gradient = Brush.linearGradient(
        colors = listOf(MaterialTheme.colorScheme.primary,MaterialTheme.colorScheme.primary)
    )
    Column(
        modifier = Modifier
            .padding(10.dp)
            .widthIn(max = 200.dp)
            .background(gradient, shape = RoundedCornerShape(20.dp))
    ) {
        Text(
            text = "$username:",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}