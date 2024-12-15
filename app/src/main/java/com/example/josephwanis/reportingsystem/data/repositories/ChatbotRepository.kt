package com.example.josephwanis.reportingsystem.data.repositories

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.VolleyError
import com.example.josephwanis.reportingsystem.data.models.Message
import com.example.josephwanis.reportingsystem.data.network.VolleySingleton
import org.json.JSONObject
import kotlinx.coroutines.CompletableDeferred

class ChatbotRepository(private val context: Context) {

    suspend fun getResponseFromLlama3(prompt: String, conversationHistory: List<Message>): String {
        val url = "http://10.0.2.2:11434/api/generate"

        val systemPrompt = """
    You are an AI assistant specifically designed to help employees solve work-related problems within their organization's reporting system. Your primary objectives are:

    1. Problem-Solving Assistant:
       - Provide practical, actionable solutions to work-related issues
       - Help employees troubleshoot and resolve challenges quickly
       - Reduce the need for direct manager intervention

    2. Conversation Guidelines:
       - Focus strictly on work-related problems and solutions
       - Maintain a professional and constructive tone
       - Do not encourage bypassing official complaint procedures
       - If a problem requires formal escalation, guide the employee to use the official complaint system

    3. Scope of Assistance:
       - Address technical, procedural, and interpersonal workplace challenges
       - Provide clear, step-by-step guidance
       - Offer resources or suggest appropriate internal channels when necessary

    4. Limitations:
       - Cannot resolve complex legal or HR issues that require human judgment
       - Will not replace formal complaint or escalation processes
       - Cannot access private or sensitive organizational data

    5. Interaction Principles:
       - Listen carefully to the employee's description of the problem
       - Ask clarifying questions if the issue is not clear
       - Provide solutions that are practical and aligned with organizational policies
       - Encourage proactive problem-solving and self-service

    Important Note: If the problem cannot be resolved through this conversation or requires formal investigation, you will explicitly advise the employee to submit a formal complaint through the designated reporting system.
    """

        // Ensure the conversation history is formatted as plain text for Llama3
        val historyText = conversationHistory.joinToString(separator = "\n") { message ->
            "${message.senderUserId}: ${message.content}"
        }

        // Create the final prompt that includes history and the user's new question
//        val fullPrompt = "Please note, the following prompt is a history of conversations:\n\n" +
//                "$historyText\nuser: $prompt"

        val fullPrompt = """
    CONTEXT PRESERVATION INSTRUCTIONS:
    - You MUST maintain continuous context across all messages
    - CAREFULLY review the entire conversation history before responding
    - Treat this as ONE CONTINUOUS CONVERSATION
    - Refer back to previous messages and their context

    SYSTEM ROLE AND GUIDELINES:
    $systemPrompt

    CONVERSATION HISTORY (CRITICAL CONTEXT):
    $historyText

    CURRENT USER QUERY:
    user: $prompt

    RESPONSE REQUIREMENTS:
    - Provide a response that directly addresses the current query
    - Ensure coherence with previous conversation context
    - Follow the system role and guidelines precisely
    """.trimIndent()

        // Construct the JSON request payload
        val json = JSONObject().apply {
            put("model", "llama3")
            put("prompt", fullPrompt)
            put("output", "text")
            put("stream", false)
        }

        // Initialize Deferred to await result asynchronously
        val deferred = CompletableDeferred<String>()

        // Start timing the request for performance insight
        val startTime = System.currentTimeMillis()

        // Debug info to verify the complete prompt structure before sending
        Log.d("ChatbotRepository", "Full Prompt Sent to Llama3: $fullPrompt")

        val request = JsonObjectRequest(
            Request.Method.POST, url, json,
            { response ->
                val endTime = System.currentTimeMillis()
                Log.d("ChatbotRepository", "Request successful in ${endTime - startTime}ms")

                val serverResponse = response.optString("response", "No response from server")
                Log.d("ChatbotRepository", "Received response: $serverResponse")

                deferred.complete(serverResponse)
            },
            { error: VolleyError ->
                val endTime = System.currentTimeMillis()
                Log.e("ChatbotRepository", "Request failed in ${endTime - startTime}ms", error)

                // Provide a user-friendly error message based on network response
                val networkError = when {
                    error.networkResponse == null -> "Network unreachable or server not responding."
                    error.networkResponse.statusCode == 404 -> "Error 404: Endpoint not found at $url."
                    error.networkResponse.statusCode == 500 -> "Error 500: Server error. Please check server logs."
                    else -> "Unknown error: ${error.message}"
                }

                deferred.complete("Connection was not successful: $networkError")
            }
        )

        // Configure retry policy for the request
        request.retryPolicy = DefaultRetryPolicy(
            180000, // Timeout in milliseconds (180 seconds)
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Add the request to the Volley queue
        VolleySingleton.getInstance(context).addToRequestQueue(request)

        // Await and return the result or error message
        return deferred.await()
    }
}
