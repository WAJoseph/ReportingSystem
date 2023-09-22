package com.example.josephwanis.reportingsystem.data.repositories

import com.example.josephwanis.reportingsystem.data.models.ChatSession
import com.example.josephwanis.reportingsystem.data.models.Message
import com.example.josephwanis.reportingsystem.data.remote.firebase.FirestoreManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepository(private val userRepository: UserRepository) {

    // Function to create a new chat session in Firestore
    suspend fun createChatSession(participants: List<String>): ChatSession? {
        // Check if a chat session with the same participants already exists
        val existingChatSession = findChatSessionByParticipants(participants)
        if (existingChatSession != null) {
            return existingChatSession
        }

        val chatSession = ChatSession(UUID.randomUUID().toString(), participants, null, Timestamp.now().seconds)

        return try {
            val documentId = FirestoreManager.addDocument("chatSessions", chatSession.toMap() as Map<String, Any>)
            chatSession.copy(sessionId = documentId ?: "")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createChatSessionsWithKnownUsers(userId: String) {
        val knownUsers = userRepository.getAllKnownUsersExceptCurrentUser(userId)
        for (knownUser in knownUsers) {
            val participants = listOf(userId, knownUser.userId)
            createChatSession(participants)
        }
    }


    // Function to find a chat session by participants
    private suspend fun findChatSessionByParticipants(participants: List<String>): ChatSession? {
        val querySnapshot = FirestoreManager.getCollection("chatSessions")
            .whereEqualTo("participants", participants)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()?.data?.let { data ->
            ChatSession.fromMap(data)
        }
    }


    suspend fun createChatSessionsWithAllUsers(currentUserUid: String) {
        val otherUsers = userRepository.getAllUsersExceptCurrentUser(currentUserUid)
        for (otherUser in otherUsers) {
            val participants = listOf(currentUserUid, otherUser.userId)
            createChatSession(participants)
        }
    }

    // Function to send a welcome message to all other users
    suspend fun sendWelcomeMessageToAllUsers(currentUserUid: String) {
        val otherUsers = userRepository.getAllUsersExceptCurrentUser(currentUserUid)
        for (otherUser in otherUsers) {
            val chatParticipants :List<String> = listOf(currentUserUid,otherUser.userId)
            val chatSession = createChatSession(chatParticipants)
            if (chatSession != null) {
                val senderId = currentUserUid
                val content = "Welcome to our chat!"
                sendMessage(chatSession.sessionId, senderId, content)
            }
        }
    }

    // Function to send a message to a chat session
    suspend fun sendMessage(sessionId: String, senderId: String, content: String): Boolean {
        val message = Message(UUID.randomUUID().toString(), senderId, content, Timestamp.now().seconds)

        val chatSession = FirestoreManager.getDocumentJustForTesting("chatSessions", sessionId)?.let { ChatSession.fromMap(it) }

        if (chatSession != null && chatSession.isUserParticipant(senderId)) {
            val batch = FirestoreManager.firestore.batch()

            // Get the document ID of the chat session
            val query = FirestoreManager.firestore.collection("chatSessions")
                .whereEqualTo("sessionId",sessionId)
                .get()
                .await()

            val documents = query.documents
            if(documents.isNotEmpty()){
                val documentId = documents.first().id

                // Create a new message document in Firestore
                val messageRef = FirestoreManager.getCollection("chatSessions").document(documentId).collection("messages").document()
                batch.set(messageRef, message.toMap())

                // Update the last message and timestamp in the chat session
                chatSession.lastMessage = message
                chatSession.timestamp = Timestamp.now().seconds
                val chatSessionRef = FirestoreManager.getCollection("chatSessions").document(documentId)
                batch.set(chatSessionRef, chatSession.toMap(), SetOptions.merge())

                return try {
                    batch.commit().await()
                    true
                } catch (e: Exception) {
                    false
                }
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
            //Get the document ID of the chatSession
            val query = FirestoreManager.firestore.collection("chatSessions")
                .whereEqualTo("sessionId",sessionId)
                .get()
                .await()

            val documents = query.documents
            if(documents.isNotEmpty()){
                val documentId = documents.first().id

                val querySnapshot = FirestoreManager.getCollection("chatSessions")
                    .document(documentId)
                    .collection("messages")
                    .orderBy("timestamp")
                    .get()
                    .await()

                querySnapshot.documents.mapNotNull { it.data?.let { data -> Message.fromMap(data) } }

            }else{
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}