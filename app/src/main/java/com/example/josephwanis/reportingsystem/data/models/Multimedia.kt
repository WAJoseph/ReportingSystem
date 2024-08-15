package com.example.josephwanis.reportingsystem.data.models

data class Multimedia(
    val multimediaId: String,
    val userId: String,
    val type: MultimediaType,
    val url: String,
    val timestamp: Long
) {

    // Add a toMap function to convert Multimedia to Map
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "multimediaId" to multimediaId,
            "userId" to userId,
            "type" to type.name,
            "url" to url,
            "timestamp" to timestamp
        )
    }

    companion object {
        fun fromMap(data: Map<String, Any>): Multimedia? {
            return try {
                val multimediaId = data["multimediaId"] as? String ?: return null
                val userId = data["userId"] as? String ?: return null
                val type = data["type"] as? String ?: return null
                val url = data["url"] as? String ?: return null
                val timestamp = data["timestamp"] as? Long ?: return null

                Multimedia(multimediaId, userId, MultimediaType.valueOf(type), url, timestamp)
            } catch (e: Exception) {
                null
            }
        }
    }
}

enum class MultimediaType {
    IMAGE,
    VIDEO,
    AUDIO
}