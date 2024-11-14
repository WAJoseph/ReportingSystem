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

        // Ensure the conversation history is formatted as plain text for Llama3
        val historyText = conversationHistory.joinToString(separator = "\n") { message ->
            "${message.senderUserId}: ${message.content}"
        }

        // Create the final prompt that includes history and the user's new question
        val fullPrompt = "Please note, the following prompt is a history of conversations:\n\n" +
                "$historyText\nuser: $prompt"

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
