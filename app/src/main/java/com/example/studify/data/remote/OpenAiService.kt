package com.example.studify.data.remote

import com.example.studify.domain.repository.SubjectInput
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit interface for OpenAI API
 */
interface OpenAiService {
    @POST("chat/completions")
    suspend fun getSchedule(
        // Bearer {API_KEY}
        @Header("Authorization") auth: String,
        @Body request: LlmScheduleRequest
    ): LlmScheduleResponse

    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") auth: String,
        @Body req: ChatCompletionRequest
    ): ChatCompletionResponse
}

// DTO
data class LlmScheduleRequest(val inputs: List<SubjectInput>)

data class LlmScheduleResponse(val schedule: List<LlmSession>) {
    data class LlmSession(
        val subject: String,
        val start: String,
        val end: String
    )
}

// DTO for chat API
data class ChatMessage(val role: String, val content: String)

data class ChatCompletionRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<ChatMessage>
)

data class ChatCompletionResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val message: ChatMessage
    )
}
