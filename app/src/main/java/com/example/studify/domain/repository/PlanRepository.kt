package com.example.studify.domain.repository

import com.example.studify.data.local.dao.PlanWithSubjects
import com.example.studify.data.local.db.CategoryType
import com.example.studify.data.local.entity.StudyPlanEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class SubjectInput(
    val subject: String,
    val credits: Int,
    val importance: Int,
    val category: CategoryType,
    val examDate: LocalDate
)

interface PlanRepository {
    fun observePlans(): Flow<List<StudyPlanEntity>>

    fun observePlansWithSubjects(): Flow<List<PlanWithSubjects>>

    suspend fun createPlanLocal(subjects: List<SubjectInput>)

    suspend fun createPlanWithLLM(subjects: List<SubjectInput>)

    suspend fun deletePlan(id: Long)
    suspend fun getAllSubjects(): List<String>
    suspend fun getGeneratedPlan(): Map<LocalDate, List<String>>

}
