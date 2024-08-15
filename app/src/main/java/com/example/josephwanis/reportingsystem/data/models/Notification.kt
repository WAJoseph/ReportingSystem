package com.example.josephwanis.reportingsystem.data.models

data class Notification(
    val notificationId: String,
    val receiverId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long
) {

    // Add a toMap function to convert Notification to Map
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "notificationId" to notificationId,
            "receiverId" to receiverId,
            "senderId" to senderId,
            "content" to content,
            "timestamp" to timestamp
        )
    }

    companion object {
        fun fromMap(data: Map<String, Any>): Notification? {
            return try {
                val notificationId = data["notificationId"] as? String ?: return null
                val receiverId = data["receiverId"] as? String ?: return null
                val senderId = data["senderId"] as? String ?: return null
                val content = data["content"] as? String ?: return null
                val timestamp = data["timestamp"] as? Long ?: return null

                Notification(notificationId, receiverId, senderId, content, timestamp)
            } catch (e: Exception) {
                null
            }
        }
    }
}