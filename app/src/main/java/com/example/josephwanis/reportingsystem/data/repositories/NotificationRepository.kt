package com.example.josephwanis.reportingsystem.data.repositories

import com.example.josephwanis.reportingsystem.data.models.Notification
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirestoreManager
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    // Function to create a new notification in Firestore
    suspend fun createNotification(notification: Notification): Boolean {
        val data = notification.toMap()

        return try {
            val documentId = FirestoreManager.addDocument("notifications", data as Map<String, Any>)
            documentId != null
        } catch (e: Exception) {
            false
        }
    }

    // Function to get all notifications for a user from Firestore
    suspend fun getNotificationsForUser(userId: String): List<Notification> {
        val querySnapshot = FirestoreManager.getCollection("notifications")
            .whereEqualTo("receiverId", userId)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { it.data?.let { data -> Notification.fromMap(data) } }
    }
}