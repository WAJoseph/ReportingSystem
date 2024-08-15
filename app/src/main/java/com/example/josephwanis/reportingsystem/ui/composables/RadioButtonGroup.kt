package com.example.josephwanis.reportingsystem.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CoolRadioButtonGroup(
    selectedOption: Boolean,
    onOptionSelected: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CoolRadioButton(
            text = "Known User  ",
            isSelected = selectedOption,
            onSelected = { onOptionSelected(true) }
        )

        CoolRadioButton(
            text = "Anonymous  ",
            isSelected = !selectedOption,
            onSelected = { onOptionSelected(false) }
        )
    }
}

@Composable
fun CoolRadioButton(
    text: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onSelected() }
            .padding(14.dp)
            .background(
                shape = CircleShape ,
                color = if (isSelected) Color(0xFF5165D6) else Color.Transparent
            ),
        contentAlignment = Alignment.Center,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = text,
                    style = TextStyle(
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                )
            }
        }
    )
}