package com.example.josephwanis.reportingsystem.data.models

data class User(
    val userId: String,
    val displayName: String,
    val email: String,
    val profileImageUri: String?,
    val isBlocked: Boolean = false
) {
    // Additional properties or functions can be added here as needed

    // Function to check if the user is an admin
    fun isAdmin(): Boolean {
        // Add your logic here to determine if the user is an admin
        // For example, you can check against a list of admin user IDs
        return false
    }

    // Function to block the user
    fun blockUser() {
        // Add logic to block the user here
        // For example, you can update the `isBlocked` property in the database
    }

    // Function to unblock the user
    fun unblockUser() {
        // Add logic to unblock the user here
        // For example, you can update the `isBlocked` property in the database
    }
}