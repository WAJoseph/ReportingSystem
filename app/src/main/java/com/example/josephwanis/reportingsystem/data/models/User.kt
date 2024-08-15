package com.example.josephwanis.reportingsystem.data.models

data class User(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val profileImageUri: String? = null,
    val blockedByUsers: MutableSet<String> = mutableSetOf(),
    val adminUserIds: MutableSet<String> = mutableSetOf(),
    val isKnown: Boolean = false
) {
    // No-argument constructor for Firestore deserialization
    constructor() : this("", "", "", null, mutableSetOf(), mutableSetOf(), false)
    // Function to check if the user is an admin
    fun isAdmin(): Boolean {
        // Add your logic here to determine if the user is an admin
        // For example, you can check against the adminUserIds set
        return adminUserIds.contains(userId)
    }

    // Function to block the user
    fun blockUser(blockedUser: User) {
        blockedByUsers.add(blockedUser.userId)
    }

    // Function to unblock the user
    fun unblockUser(unblockedUser: User) {
        blockedByUsers.remove(unblockedUser.userId)
    }

    // Function to convert the User object to a Map
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "displayName" to displayName,
            "email" to email,
            "profileImageUri" to profileImageUri,
            "blockedByUsers" to blockedByUsers.toList(), // Convert to Set
            "adminUserIds" to adminUserIds.toList(), // Convert to Set
            "isKnown" to isKnown
        )
    }

    companion object {
        // Function to create a User object from a Map
        fun fromMap(data: Map<String, Any?>): User {
            return User(
                userId = data["userId"] as String,
                displayName = data["displayName"] as String,
                email = data["email"] as String,
                profileImageUri = data["profileImageUri"] as String?,
                blockedByUsers = (data["blockedByUsers"] as List<String>).toMutableSet(), // Convert to MutableSet
                adminUserIds = (data["adminUserIds"] as List<String>).toMutableSet(), // Convert to MutableSet
                isKnown = data["isKnown"] as Boolean
            )
        }
    }

}