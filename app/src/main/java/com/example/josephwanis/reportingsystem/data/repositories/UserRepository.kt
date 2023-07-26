package com.example.josephwanis.reportingsystem.data.repositories

import com.example.josephwanis.reportingsystem.data.models.User
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirestoreManager
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserRepository(private val firebaseAuthManager: FirebaseAuthManager) {

    // Function to handle user registration using Firebase Authentication
    suspend fun registerUser(email: String, password: String): User? {
        try {
            // Call the FirebaseAuthManager's registerUserWithEmailAndPassword function to register the user
            val firebaseUser = firebaseAuthManager.registerUserWithEmailAndPassword(email, password)

            if (firebaseUser != null) {
                // Create a new user with the provided email and user ID from authentication
                val newUser = User(firebaseUser.uid, email, email, null, mutableSetOf(), mutableSetOf(), false)

                // Add the user to Firestore
                val documentId = FirestoreManager.addDocument("users", newUser.toMap() as Map<String, Any>)

                // Return the new user if added successfully, otherwise return null
                return if (documentId != null) {
                    newUser
                } else {
                    null
                }
            } else {
                return null
            }
        } catch (e: Exception) {
            // Handle registration failure (e.g., email already exists)
            return null
        }
    }

    // Function to handle login using Firebase Authentication
    suspend fun loginUser(email: String, password: String): User? {
        val firebaseUser = firebaseAuthManager.loginUserWithEmailAndPassword(email, password)
        return if (firebaseUser != null) {
            // Fetch the user data from Firestore using the user ID
            FirestoreManager.getDocument("users", firebaseUser.uid)?.let { userData ->
                User.fromMap(userData)
            }
        }else {
            null
        }
    }

    // Function to update the user's profile image URI in Firestore
    suspend fun updateUserProfileImage(userId: String, profileImageUri: String): Boolean {
        val data = mapOf("profileImageUri" to profileImageUri)
        return FirestoreManager.updateDocument("users", userId, data)
    }

    // Function to update the user's profile in Firestore
    suspend fun updateUserProfile(userId: String, displayName: String, profileImageUri: String?): Boolean {
        val data = mapOf(
            "displayName" to displayName,
            "profileImageUri" to profileImageUri
        )

        return try {
            FirestoreManager.updateDocument("users", userId, data)
            true
        } catch (e: Exception) {
            false
        }
    }

    // Function to block a user
    suspend fun blockUser(userId: String, blockedUserId: String): Boolean {
        val user = FirestoreManager.getDocument("users", userId)?.let { User.fromMap(it) }
        val blockedUser = FirestoreManager.getDocument("users", blockedUserId)?.let { User.fromMap(it) }

        if (user != null && blockedUser != null) {
            user.blockUser(blockedUser)
            blockedUser.blockedByUsers.add(userId)

            val batch = FirestoreManager.firestore.batch()
            val userRef = FirestoreManager.getCollection("users").document(userId)
            val blockedUserRef = FirestoreManager.getCollection("users").document(blockedUserId)

            batch.set(userRef, user.toMap(), SetOptions.merge())
            batch.set(blockedUserRef, blockedUser.toMap(), SetOptions.merge())

            return try {
                batch.commit().await()
                true
            } catch (e: Exception) {
                false
            }
        }

        return false
    }

    // Function to unblock a user
    suspend fun unblockUser(userId: String, unblockedUserId: String): Boolean {
        val user = FirestoreManager.getDocument("users", userId)?.let { User.fromMap(it) }
        val unblockedUser = FirestoreManager.getDocument("users", unblockedUserId)?.let { User.fromMap(it) }

        if (user != null && unblockedUser != null) {
            user.unblockUser(unblockedUser)
            unblockedUser.blockedByUsers.remove(userId)

            val batch = FirestoreManager.firestore.batch()
            val userRef = FirestoreManager.getCollection("users").document(userId)
            val unblockedUserRef = FirestoreManager.getCollection("users").document(unblockedUserId)

            batch.set(userRef, user.toMap(), SetOptions.merge())
            batch.set(unblockedUserRef, unblockedUser.toMap(), SetOptions.merge())

            return try {
                batch.commit().await()
                true
            } catch (e: Exception) {
                false
            }
        }

        return false
    }

    // Function to fetch the user profile for a specific user
    suspend fun getUserProfile(userId: String): User? {
        return FirestoreManager.getDocument("users", userId)?.let { userData ->
            User.fromMap(userData)
        }
    }

    // Function to fetch blocked users for a specific user
    suspend fun getBlockedUsers(userId: String): List<User>? {
        val user = FirestoreManager.getDocument("users", userId)?.let { User.fromMap(it) }

        return user?.blockedByUsers?.mapNotNull { blockedUserId ->
            FirestoreManager.getDocument("users", blockedUserId)?.let { User.fromMap(it) }
        }
    }

}