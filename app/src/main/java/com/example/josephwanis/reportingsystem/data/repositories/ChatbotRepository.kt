package com.example.josephwanis.reportingsystem.data.repositories

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.VolleyError
import com.example.josephwanis.reportingsystem.data.network.VolleySingleton
import org.json.JSONObject
import kotlinx.coroutines.CompletableDeferred

class ChatbotRepository(private val context: Context) {

    suspend fun getResponseFromLlama3(prompt: String): String {
        val url = "http://10.0.2.2:11434/api/generate"
        val json = JSONObject().apply {
            put("model", "llama3")
            put("prompt", prompt)
            put("output", "text")
            put("stream", false)
        }

        // Initialize Deferred to await result asynchronously
        val deferred = CompletableDeferred<String>()

        // Start timing the request for performance insight
        val startTime = System.currentTimeMillis()

        // Debug info to check initial conditions
        Log.d("ChatbotRepository", "Sending request to URL: $url with data: $json")

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

                // Check if network is reachable
                val networkError = when {
                    error.networkResponse == null -> "Network unreachable or server not responding."
                    error.networkResponse.statusCode == 404 -> "Error 404: Endpoint not found at $url."
                    error.networkResponse.statusCode == 500 -> "Error 500: Server error. Please check server logs."
                    else -> "Unknown error: ${error.message}"
                }

                deferred.complete("Connection was not successful: $networkError")
            }
        )

        // Add the request to the Volley queue
        VolleySingleton.getInstance(context).addToRequestQueue(request)

        // Await and return the result or error message
        return deferred.await()
    }
}
