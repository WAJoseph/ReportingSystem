package com.example.josephwanis.reportingsystem.data.repositories

import com.example.josephwanis.reportingsystem.data.models.Multimedia
import com.example.josephwanis.reportingsystem.data.models.MultimediaType
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirestoreManager
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await

class MultimediaRepository {

    // Function to create a new multimedia entry in Firestore
    suspend fun createMultimedia(userId: String, type: MultimediaType, url: String): Boolean {
        val multimedia = Multimedia("", userId, type, url, Timestamp.now().seconds)

        return try {
            val documentId = FirestoreManager.addDocument("multimedia", multimedia.toMap() as Map<String, Any>)
            documentId != null
        } catch (e: Exception) {
            false
        }
    }

    // Function to fetch multimedia entries for a user from Firestore
    suspend fun getMultimediaForUser(userId: String): List<Multimedia> {
        val querySnapshot = FirestoreManager.getCollection("multimedia")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { it.data?.let { data -> Multimedia.fromMap(data) } }
    }
}