package com.example.josephwanis.reportingsystem.data.util
import java.security.MessageDigest

object HashUtils {
    fun hashEmail(email: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(email.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

