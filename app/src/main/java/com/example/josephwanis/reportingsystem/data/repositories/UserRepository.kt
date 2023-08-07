package com.example.josephwanis.reportingsystem.data.repositories

import com.example.josephwanis.reportingsystem.data.models.User
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirebaseAuthManager
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirestoreManager
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(private val firebaseAuthManager: FirebaseAuthManager) {

    // Function to handle user registration using Firebase Authentication
    suspend fun registerUser(email: String, password: String, displayName: String): User {
        return withContext(Dispatchers.IO) {
            try {
                val user = firebaseAuthManager.registerUserWithEmailAndPassword(email, password)
                // Create a new user with the provided email, display name, and user ID from authentication
                val newUser = User(user.uid, displayName, email, null, mutableSetOf(), mutableSetOf(), false)

                // Launch a separate coroutine for adding the user to Firestore
                val addUserJob = launch {
                    try {
                        FirestoreManager.addUserRegistrationDocument("users", user.uid, newUser.toMap())
                    } catch (e: Exception) {
                        // Handle Firestore operation failure, if needed
                    }
                }

                // Wait for the Firestore operation to complete
                addUserJob.join()

                // Return the new user if added successfully, otherwise return null
                newUser
            } catch (e: Exception) {
                // Handle registration failure (e.g., email already exists)
                // Here, we'll throw an exception with an appropriate error message
                when (e) {
                    is FirebaseAuthUserCollisionException -> {
                        throw Exception("Email is already registered. Please use a different email.")
                    }
                    is FirebaseNetworkException -> {
                        throw Exception("Network error. Please check your internet connection and try again.")
                    }
                    else -> {
                        throw Exception("An unexpected error occurred during registration. Please try again later.")
                    }
                }
            }
        }
    }


    // Function to handle login using Firebase Authentication
    suspend fun loginUser(email: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                // Attempt to log in the user using Firebase Authentication
                val user = firebaseAuthManager.loginUserWithEmailAndPassword(email, password)

                // If the login is successful, fetch the user data from Firestore
                FirestoreManager.getDocument("users", user.uid)?.let { userData -> User.fromMap(userData) }
            } catch (e: Exception) {
                // Handle login failure and provide appropriate error messages
                when (e) {
                    is FirebaseAuthInvalidUserException -> {
                        // If the user account does not exist
                        throw Exception("User not found. Please check your email and password.")
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        // If the user provided invalid credentials (wrong password)
                        throw Exception("Invalid credentials. Please check your email and password.")
                    }
                    is FirebaseNetworkException -> {
                        // If there is a network error
                        throw Exception("Network error. Please check your internet connection and try again.")
                    }
                    else -> {
                        // For any other unexpected errors
                        throw Exception("An unexpected error occurred during login. Please try again later.")
                    }
                }
            }
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
        return FirestoreManager.getDocument("users", userId)?.let { User.fromMap(it.toMap()) }
    }

    // Function to fetch blocked users for a specific user
    suspend fun getBlockedUsers(userId: String): List<User>? {
        val user = FirestoreManager.getDocument("users", userId)?.let { User.fromMap(it) }

        return user?.blockedByUsers?.mapNotNull { blockedUserId ->
            FirestoreManager.getDocument("users", blockedUserId)?.let { User.fromMap(it) }
        }
    }
}