package com.example.josephwanis.reportingsystem.data.models

data class ChatSession(
    val sessionId: String,
    val participants: List<String>,
    var lastMessage: Message?,
    var timestamp: Long
) {
    // Function to check if a user is a participant in this chat session
    fun isUserParticipant(userId: String): Boolean {
        return participants.contains(userId)
    }

    // Function to add a new participant to the chat session
    fun addParticipant(userId: String) {
        if (!participants.contains(userId)) {
            participants.toMutableList().add(userId)
        }
    }

    // Function to remove a participant from the chat session
    fun removeParticipant(userId: String) {
        participants.toMutableList().remove(userId)
    }

    // Add a toMap function to convert ChatSession to Map
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "sessionId" to sessionId,
            "participants" to participants,
            "lastMessage" to lastMessage?.toMap(), // Convert lastMessage to Map
            "timestamp" to timestamp
        )
    }

    companion object {
        fun fromMap(data: Map<String, Any>): ChatSession? {
            return try {
                val sessionId = data["sessionId"] as? String ?: return null
                val participants = data["participants"] as? List<String> ?: return null
                val lastMessage = data["lastMessage"] as? Message
                val timestamp = data["timestamp"] as? Long ?: return null

                ChatSession(sessionId, participants, lastMessage, timestamp)
            } catch (e: Exception) {
                null
            }
        }
    }
}