package com.example.josephwanis.reportingsystem.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object FirebaseAuthManager {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // Function to check if a user is currently signed in
    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    // Function to get the current user ID if signed in, or null if not signed in
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    // Function to register a new user with email and password
    suspend fun registerUserWithEmailAndPassword(email: String, password: String): String {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("User registration failed.")
    }

    // Function to log in a user with email and password
    suspend fun loginUserWithEmailAndPassword(email: String, password: String): String {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("Login failed.")
    }

    // Function to sign out the current user
    suspend fun signOutUser() {
        firebaseAuth.signOut()
    }

    // Function to reset the user's password
    suspend fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }
}