package com.example.josephwanis.reportingsystem.data.models

data class UserInteraction (
    val interactionId: String,
    val senderUserId: String,
    val receiverUserId: String,
    val message: String,
    val timestamp: Long
) {

}