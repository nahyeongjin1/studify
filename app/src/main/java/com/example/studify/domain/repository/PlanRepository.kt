package com.example.studify.domain.repository

import com.example.studify.data.local.entity.StudyPlanEntity
import kotlinx.coroutines.flow.Flow

data class SubjectInput(
    val subject: String,
    val credits: Int,
    val importance: Int,
    val examDate: String
)

interface PlanRepository {
    fun observePlans(): Flow<List<StudyPlanEntity>>

    suspend fun createPlanWithLLM(inputs: List<SubjectInput>)

    suspend fun deletePlan(id: Long)
}
