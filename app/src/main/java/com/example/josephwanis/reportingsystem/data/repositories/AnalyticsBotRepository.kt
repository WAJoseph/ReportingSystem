package com.example.josephwanis.reportingsystem.data.repositories

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.josephwanis.reportingsystem.data.network.VolleySingleton
import kotlinx.coroutines.CompletableDeferred
import org.json.JSONObject

class AnalyticsBotRepository(private val context: Context) {

    suspend fun analyzeMessagesForChart(messages: List<String>): Map<String, Float> {
        val url = "http://10.0.2.2:11434/api/generate"

        val systemPrompt = """
You are an AI data analyst. Your role is to analyze collections of messages and categorize them into major topics with percentages.

Output Rules:
1. Respond with a JSON object only. Do not include any explanations, headers, or additional text.
2. The JSON format must strictly follow this structure:
{
    "Category A": percentage (float),
    "Category B": percentage (float),
    ...
}
3. Percentages must sum to 100. Categories with 0% should be excluded.
4. Ensure the JSON object is well-formed and valid.

Example Response:
{
    "Technical Issues": 50.0,
    "Customer Complaints": 30.0,
    "Marketing Feedback": 20.0
}

Do not include any additional information, such as explanations, metadata, or summaries. Respond with the JSON object only.
"""

        // Join messages into a single input for analysis
        val formattedMessages = messages.joinToString(separator = "\n") { "- $it" }

        // Construct the full prompt
        val fullPrompt = """
SYSTEM INSTRUCTIONS:
$systemPrompt

MESSAGES FOR ANALYSIS:
$formattedMessages
""".trimIndent()

        // Create the JSON request payload
        val json = JSONObject().apply {
            put("model", "llama3")
            put("prompt", fullPrompt)
            put("output", "json")
            put("stream", false)
        }

        val deferred = CompletableDeferred<Map<String, Float>>()

        val request = JsonObjectRequest(
            Request.Method.POST, url, json,
            { response ->
                try {
                    Log.d("AnalyticsBotRepository", "Raw server response: $response")

                    // Extract the "response" field containing the JSON string
                    val jsonResponseString = response.optString("response", "")
                    if (jsonResponseString.isNotBlank()) {
                        val responseData = JSONObject(jsonResponseString)

                        // Parse the JSON object into a map
                        val result = mutableMapOf<String, Float>()
                        responseData.keys().forEach { key ->
                            result[key] = responseData.optDouble(key).toFloat()
                        }
                        Log.d("AnalyticsBotRepository", "Parsed result: $result")
                        deferred.complete(result)
                    } else {
                        Log.e("AnalyticsBotRepository", "No valid JSON response field found")
                        deferred.complete(emptyMap())
                    }
                } catch (e: Exception) {
                    Log.e("AnalyticsBotRepository", "Error parsing server response", e)
                    deferred.complete(emptyMap())
                }
            },
            { error ->
                Log.e("AnalyticsBotRepository", "Request failed", error)
                deferred.complete(emptyMap())
            }
        )

        // Configure retry policy
        request.retryPolicy = DefaultRetryPolicy(
            180000, // Timeout in milliseconds
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Add the request to the Volley queue
        VolleySingleton.getInstance(context).addToRequestQueue(request)

        return deferred.await()
    }
}
