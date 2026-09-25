package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.generator.DashboardGeneratorEngine
import com.example.data.model.Dashboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiDashboardService {
    private const val TAG = "GeminiDashboardService"
    private const val PRIMARY_MODEL = "gemini-3.1-pro-preview"
    private const val FALLBACK_MODEL = "gemini-3.5-flash"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateDashboard(userPrompt: String): Dashboard = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val baseDashboard = DashboardGeneratorEngine.generateVariations(userPrompt)

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("placeholder", ignoreCase = true)) {
            Log.d(TAG, "Using high-performance local generator engine (No Gemini key provided)")
            return@withContext baseDashboard
        }

        try {
            // Attempt Gemini Pro call to enhance AI insights and custom variation descriptions
            val aiResponse = callGemini(apiKey, PRIMARY_MODEL, userPrompt)
                ?: callGemini(apiKey, FALLBACK_MODEL, userPrompt)

            if (!aiResponse.isNullOrBlank()) {
                Log.d(TAG, "Gemini generated intelligent response successfully: ${aiResponse.take(120)}")
                return@withContext baseDashboard.copy(
                    description = "Gemini Pro ($PRIMARY_MODEL) synthesized architecture: " + aiResponse.take(280) + "..."
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking Gemini API: ${e.message}", e)
        }

        baseDashboard
    }

    private fun callGemini(apiKey: String, model: String, prompt: String): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
        val systemPrompt = "You are Jeddah San, principal enterprise dashboard architect. Analyze the user's dashboard requirement and return a 2-sentence executive technical summary outlining the optimal real-time metrics, data pipelines (CSV/SQL/WordPress Webhooks), and security controls."

        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "$systemPrompt\n\nUser Dashboard Request: $prompt"))
                    })
                })
            })
        }

        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string()
            if (response.isSuccessful && !body.isNullOrBlank()) {
                val json = JSONObject(body)
                val candidates = json.optJSONArray("candidates")
                val first = candidates?.optJSONObject(0)
                val content = first?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")
                text
            } else {
                Log.w(TAG, "Gemini call non-success: ${response.code} $body")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "HTTP error in callGemini: ${e.message}")
            null
        }
    }
}
