package com.example.josephwanis.reportingsystem.data.models



data class Message(
    val messageId: String,
    val senderUserId: String,
    val content: String,
    val timestamp: Long
) {
    // Function to convert Message object to a map for Firestore
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "messageId" to messageId,
            "senderUserId" to senderUserId,
            "content" to content,
            "timestamp" to timestamp
        )
    }

    companion object {
        // Function to create a Message object from a map retrieved from Firestore
        fun fromMap(data: Map<String, Any?>): Message {
            val messageId = data["messageId"] as? String ?: ""
            val senderUserId = data["senderUserId"] as? String ?: ""
            val content = data["content"] as? String ?: ""
            val timestamp = (data["timestamp"] as? Long) ?: 0
            return Message(messageId, senderUserId, content, timestamp)
        }
    }
}