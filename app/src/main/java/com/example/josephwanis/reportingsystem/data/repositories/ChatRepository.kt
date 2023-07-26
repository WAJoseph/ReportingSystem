package com.example.josephwanis.reportingsystem.data.repositories

import com.example.josephwanis.reportingsystem.data.models.ChatSession
import com.example.josephwanis.reportingsystem.data.models.Message
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirestoreManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class ChatRepository {

    // Function to create a new chat session in Firestore
    suspend fun createChatSession(participants: List<String>): ChatSession? {
        val chatSession = ChatSession("", participants, null, Timestamp.now().seconds)

        return try {
            val documentId = FirestoreManager.addDocument("chatSessions", chatSession.toMap() as Map<String, Any>)
            chatSession.copy(sessionId = documentId ?: "")
        } catch (e: Exception) {
            null
        }
    }

    // Function to send a message to a chat session
    suspend fun sendMessage(sessionId: String, senderId: String, content: String): Boolean {
        val message = Message("", senderId, content, Timestamp.now().seconds)

        val chatSession = FirestoreManager.getDocument("chatSessions", sessionId)?.let { ChatSession.fromMap(it) }

        if (chatSession != null && chatSession.isUserParticipant(senderId)) {
            val batch = FirestoreManager.firestore.batch()

            // Create a new message document in Firestore
            val messageRef = FirestoreManager.getCollection("chatSessions").document(sessionId).collection("messages").document()
            batch.set(messageRef, message.toMap())

            // Update the last message and timestamp in the chat session
            chatSession.lastMessage = message
            chatSession.timestamp = Timestamp.now().seconds
            val chatSessionRef = FirestoreManager.getCollection("chatSessions").document(sessionId)
            batch.set(chatSessionRef, chatSession.toMap(), SetOptions.merge())

            return try {
                batch.commit().await()
                true
            } catch (e: Exception) {
                false
            }
        }

        return false
    }

    // Function to fetch chat sessions for a user from Firestore
    suspend fun getChatSessionsForUser(userId: String): List<ChatSession> {
        val querySnapshot = FirestoreManager.getCollection("chatSessions")
            .whereArrayContains("participants", userId)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { it.data?.let { data -> ChatSession.fromMap(data) } }
    }

    // Function to get all messages for a chat session
    suspend fun getAllMessagesForChatSession(sessionId: String): List<Message> {
        return try {
            val querySnapshot = FirestoreManager.getCollection("chatSessions")
                .document(sessionId)
                .collection("messages")
                .orderBy("timestamp")
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.data?.let { data -> Message.fromMap(data) } }
        } catch (e: Exception) {
            emptyList()
        }
    }
}