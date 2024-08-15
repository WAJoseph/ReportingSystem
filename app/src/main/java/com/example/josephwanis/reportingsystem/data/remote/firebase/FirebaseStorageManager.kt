package com.example.josephwanis.reportingsystem.data.remote.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FirebaseStorageManager {

    // Function to upload a file to Firebase Storage
    fun uploadFile(fileUri: Uri, folderPath: String, fileName: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = getStorageReference(folderPath, fileName)
        storageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Function to get the download URL of a file in Firebase Storage
    fun getFileDownloadUrl(folderPath: String, fileName: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = getStorageReference(folderPath, fileName)
        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Function to delete a file from Firebase Storage
    fun deleteFile(folderPath: String, fileName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = getStorageReference(folderPath, fileName)
        storageRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Function to get a StorageReference for a file
    private fun getStorageReference(folderPath: String, fileName: String): StorageReference {
        val storage = FirebaseStorage.getInstance()
        return storage.reference.child("$folderPath/$fileName")
    }
}