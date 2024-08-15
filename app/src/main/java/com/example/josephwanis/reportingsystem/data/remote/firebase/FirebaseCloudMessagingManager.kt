package com.example.josephwanis.reportingsystem.data.remote.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.tasks.await

object FirebaseCloudMessagingManager {

    private const val TAG = "FirebaseCloudMessaging"

    // Function to subscribe to a topic (using coroutines)
    suspend fun subscribeToTopic(topic: String): Boolean {
        return try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
            Log.d(TAG, "Subscribed to topic: $topic")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to subscribe to topic: $topic", e)
            false
        }
    }

    // Function to unsubscribe from a topic (using coroutines)
    suspend fun unsubscribeFromTopic(topic: String): Boolean {
        return try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).await()
            Log.d(TAG, "Unsubscribed from topic: $topic")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unsubscribe from topic: $topic", e)
            false
        }
    }
}

// Custom Firebase Messaging Service
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle incoming messages here
        Log.d(TAG, "Received a new FCM message: ${remoteMessage.data}")
    }

    override fun onNewToken(token: String) {
        // Handle token refresh here
        Log.d(TAG, "Refreshed FCM token: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMessaging"
    }
}