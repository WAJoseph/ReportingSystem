package com.example.josephwanis.reportingsystem.data.remote.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

object FirebaseCloudMessagingManager {

    private const val TAG = "FirebaseCloudMessaging"

    // Function to subscribe to a topic
    fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Subscribed to topic: $topic")
                } else {
                    Log.e(TAG, "Failed to subscribe to topic: $topic")
                }
            }
    }

    // Function to unsubscribe from a topic
    fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Unsubscribed from topic: $topic")
                } else {
                    Log.e(TAG, "Failed to unsubscribe from topic: $topic")
                }
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