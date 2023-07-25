package com.example.josephwanis.reportingsystem.data.repositories

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class UserRepository(private val firebaseAuth: FirebaseAuth) {

    // Function to handle login using Firebase Authentication
    suspend fun loginUser(email: String, password: String): Boolean {
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            // Authentication successful
            return true
        } catch (e: Exception) {
            // Handle authentication failure (e.g., incorrect credentials)
            return false
        }
    }

    // Function to handle user registration using Firebase Authentication
    suspend fun registerUser(email: String, password: String): Boolean {
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            // Registration successful
            return true
        } catch (e: Exception) {
            // Handle registration failure (e.g., email already exists)
            return false
        }
    }
}