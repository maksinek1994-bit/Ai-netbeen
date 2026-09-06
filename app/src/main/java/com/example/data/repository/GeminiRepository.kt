package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class CodeFixResult(
    val explanation: String,
    val fixedCode: String?
)

data class ExecutionResult(
    val output: String,
    val exitCode: Int,
    val executionTimeMs: Long,
    val isSimulated: Boolean = true
)

class GeminiRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("netbeans_settings", Context.MODE_PRIVATE)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    // Model mandated by Gemini API guidelines
    private val modelName = "gemini-3.5-flash"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

    fun getEffectiveApiKey(): String {
        val userKey = prefs.getString("custom_gemini_api_key", "")?.trim() ?: ""
        if (userKey.isNotEmpty()) {
            return userKey
        }
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    fun saveCustomApiKey(key: String) {
        prefs.edit().putString("custom_gemini_api_key", key.trim()).apply()
    }

    fun isApiKeyConfigured(): Boolean {
        val key = getEffectiveApiKey()
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun generateCode(
        prompt: String,
        language: String,
        contextCode: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        val systemInstruction = """
            Jesteś zaawansowanym asystentem programistycznym w NetBeans Studio Mobile IDE.
            Twórz czysty, nowoczesny, poprawny składniowo i skomentowany kod w języku $language.
            Zwracaj gotowy, działający kod z krótkim wyjaśnieniem po polsku.
            Umieszczaj główny kod w blokach ```$language ... ```.
        """.trimIndent()

        val fullPrompt = if (contextCode.isNotBlank()) {
            "Kontekst aktualnego pliku:\n```$language\n$contextCode\n```\n\nZadanie użytkownika:\n$prompt"
        } else {
            "Zadanie programistyczne w języku $language:\n$prompt"
        }

        callGemini(fullPrompt, systemInstruction)
    }

    suspend fun debugAndFixCode(
        code: String,
        language: String,
        userIssue: String = ""
    ): Result<CodeFixResult> = withContext(Dispatchers.IO) {
        val systemInstruction = """
            Jesteś ekspertem kompilatora i debugera NetBeans Studio Mobile IDE.
            Przeanalizuj poniższy kod w języku $language pod kątem błędów składniowych, logicznych, null-pointerów i wydajności.
            Odpowiedz w dwóch częściach:
            1. Wyjaśnienie błędów i wskazówki po polsku.
            2. Poprawiony, kompletny kod w bloku ```$language ... ``` gotowy do wklejenia.
        """.trimIndent()

        val prompt = buildString {
            appendLine("Kod źródłowy ($language):")
            appendLine("```$language")
            appendLine(code)
            appendLine("```")
            if (userIssue.isNotBlank()) {
                appendLine("Opis problemu przez użytkownika: $userIssue")
            } else {
                appendLine("Zbadaj kod, znajdź błędy i podaj poprawioną wersję.")
            }
        }

        val responseResult = callGemini(prompt, systemInstruction)
        responseResult.map { rawText ->
            val codeRegex = Regex("```(?:[a-zA-Z0-9_-]+)?\\s*([\\s\\S]*?)```")
            val match = codeRegex.find(rawText)
            val extractedCode = match?.groups?.get(1)?.value?.trim()
            CodeFixResult(
                explanation = rawText,
                fixedCode = extractedCode
            )
        }
    }

    suspend fun explainCode(
        code: String,
        language: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val systemInstruction = """
            Jesteś nauczycielem programowania w NetBeans Studio.
            Wyjaśnij działanie poniższego kodu w języku $language krok po kroku po polsku.
            Uwzględnij:
            1. Cel i strukturę programu
            2. Kluczowe metody, klasy i biblioteki
            3. Złożoność obliczeniową (jeśli dotyczy algorytmów)
            4. Dobre praktyki i ewentualne możliwości usprawnienia
        """.trimIndent()

        val prompt = "Wyjaśnij ten kod ($language):\n```$language\n$code\n```"
        callGemini(prompt, systemInstruction)
    }

    suspend fun chatWithCodeContext(
        history: List<Pair<String, String>>, // role to message
        currentCode: String,
        fileName: String,
        language: String,
        userMessage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val systemInstruction = """
            Jesteś inteligentnym programistą-partnerem (AI Pair Programmer) w NetBeans Studio Mobile IDE.
            Aktualnie otwarty plik: $fileName (język: $language).
            Odpowiadaj po polsku, merytorycznie, podając zwięzłe i precyzyjne przykłady kodu.
        """.trimIndent()

        val contentsArray = JSONArray()

        // Provide file context in first message or prompt
        val firstContext = "Kontekst otwartego pliku $fileName:\n```$language\n$currentCode\n```"

        val firstTurn = JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().apply {
                put(JSONObject().apply { put("text", firstContext) })
            })
        }
        contentsArray.put(firstTurn)

        val modelAck = JSONObject().apply {
            put("role", "model")
            put("parts", JSONArray().apply {
                put(JSONObject().apply { put("text", "Rozumiem, widzę otwarty plik $fileName. W czym mogę pomóc?") })
            })
        }
        contentsArray.put(modelAck)

        // Previous turns (last 6 to preserve context limit)
        val recentHistory = history.takeLast(6)
        for ((role, text) in recentHistory) {
            val jsonRole = if (role == "user") "user" else "model"
            contentsArray.put(JSONObject().apply {
                put("role", jsonRole)
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", text) })
                })
            })
        }

        // New user prompt
        contentsArray.put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().apply {
                put(JSONObject().apply { put("text", userMessage) })
            })
        })

        callGeminiRaw(contentsArray, systemInstruction)
    }

    suspend fun simulateExecutionWithGemini(
        code: String,
        language: String
    ): Result<ExecutionResult> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val systemInstruction = """
            Działasz jako wirtualny kompilator i silnik wykonawczy NetBeans Studio dla języka $language.
            Wykonaj podany kod mentalnie/analitycznie i zwróć DOKŁADNIE to, co program wypisałby na standardowe wyjście (STDOUT/STDERR).
            Jeśli w kodzie jest błąd kompilacji lub wyjątek czasu wykonania, wypisz dokładny komunikat błędu jak kompilator.
            Nie dodawaj żadnych zbędnych komentarzy typu 'Oto wynik', tylko czysty output konsoli!
        """.trimIndent()

        val prompt = "URUCHOM KOD I WYPISZ OUTPUT:\n```$language\n$code\n```"
        val res = callGemini(prompt, systemInstruction)
        val elapsed = System.currentTimeMillis() - startTime

        res.map { output ->
            val hasError = output.contains("Exception in thread") ||
                    output.contains("error:") ||
                    output.contains("Error:") ||
                    output.contains("Traceback (most recent call last)")
            ExecutionResult(
                output = output.trim(),
                exitCode = if (hasError) 1 else 0,
                executionTimeMs = elapsed,
                isSimulated = true
            )
        }
    }

    private suspend fun callGemini(prompt: String, systemInstruction: String): Result<String> {
        val contentsArray = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", prompt) })
                })
            })
        }
        return callGeminiRaw(contentsArray, systemInstruction)
    }

    private suspend fun callGeminiRaw(
        contentsArray: JSONArray,
        systemInstruction: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException(
                    "Klucz Gemini API nie jest skonfigurowany. Dodaj swój klucz w Ustawieniach NetBeans Studio lub w panelu Secrets Google AI Studio."
                )
            )
        }

        try {
            val url = "$baseUrl?key=$apiKey"
            val rootJson = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.3)
                    put("topP", 0.95)
                })
            }

            val requestBody = rootJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody)
                    errJson.getJSONObject("error").getString("message")
                } catch (e: Exception) {
                    "HTTP ${response.code}: ${response.message}"
                }
                return@withContext Result.failure(Exception("Błąd Gemini API ($errorMsg)"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val contentObj = candidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    return@withContext Result.success(text)
                }
            }
            Result.failure(Exception("Brak odpowiedzi od modelu Gemini"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
