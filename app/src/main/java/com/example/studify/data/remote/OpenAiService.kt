package com.example.studify.data.remote

import com.example.studify.domain.repository.SubjectInput
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiService {
    @POST("chat/completions")
    suspend fun getSchedule(
        // Bearer {API_KEY}
        @Header("Authorization") auth: String,
        @Body request: LlmScheduleRequest
    ): LlmScheduleResponse
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
