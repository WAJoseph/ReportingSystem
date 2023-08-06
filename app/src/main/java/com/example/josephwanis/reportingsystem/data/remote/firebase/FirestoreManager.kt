package com.example.josephwanis.reportingsystem.data.remote.firebase

import android.util.Log
import com.example.josephwanis.reportingsystem.data.models.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object FirestoreManager {

    // Reference to the root Firestore collection
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Function to get the reference of a specific collection in Firestore
    fun getCollection(collectionName: String): CollectionReference {
        return firestore.collection(collectionName)
    }

    // Function to add a document to a specific collection in Firestore
    suspend fun addDocument(collectionName: String, data: Map<String, Any?>): String {
        return try {
            val docRef = firestore.collection(collectionName).add(data).await()
            docRef.id // Return the document ID directly
        } catch (e: Exception) {
            // Handle the exception if something goes wrong during the document creation
            throw e
        }
    }

    // Function to add a document to a specific collection in Firestore
    suspend fun addUserRegistrationDocument(collectionName: String, documentId: String, data: Map<String, Any?>): String {
        return try {
            val docRef = firestore.collection(collectionName).document(documentId)
            docRef.set(data, SetOptions.merge()).await() // Use set() with SetOptions.merge()
            documentId // Return the specified document ID
        } catch (e: Exception) {
            // Handle the exception if something goes wrong during the document creation
            throw e
        }
    }

    // Function to update a document in a specific collection in Firestore
    suspend fun updateDocument(collectionName: String, documentId: String, data: Map<String, String?>): Boolean {
        return try {
            firestore.collection(collectionName).document(documentId).update(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Function to delete a document from a specific collection in Firestore
    suspend fun deleteDocument(collectionName: String, documentId: String): Boolean {
        return try {
            firestore.collection(collectionName).document(documentId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Function to fetch all documents from a specific collection in Firestore
    suspend fun getAllDocuments(collectionName: String): List<Map<String, Any>> {
        return try {
            val snapshot = firestore.collection(collectionName).get().await()
            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Function to fetch a specific document from a specific collection in Firestore
    suspend fun getDocument(collectionName: String, documentId: String): Map<String, Any>? {
        return try {
            val snapshot = firestore.collection(collectionName).document(documentId).get().await()
            val data = snapshot.data
            if (data != null) {
                data
            } else {
                Log.d("FirestoreManager", "getDocument: Document not found or null")
                null
            }
        } catch (e: Exception) {
            // Print the exception for debugging purposes
            Log.e("FirestoreManager", "Error retrieving document: $e")
            null
        }
    }

}
