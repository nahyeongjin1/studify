package com.example.studify.data.repository

import android.util.Log
import com.example.studify.BuildConfig
import com.example.studify.data.local.dao.PlanWithSubjects
import com.example.studify.data.local.entity.StudyPlanEntity
import com.example.studify.data.remote.LlmScheduleRequest
import com.example.studify.data.remote.OpenAiService
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.SubjectInput
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepositoryImpl
    @Inject
    constructor(
        private val openAi: OpenAiService
    ) : PlanRepository {
        // 아직 미구현
        override fun observePlans() = flowOf(emptyList<StudyPlanEntity>())

        override fun observePlansWithSubjects() = flowOf(emptyList<PlanWithSubjects>())

        override suspend fun createPlanLocal(subjects: List<SubjectInput>) {
            // no-op
        }

        override suspend fun createPlanWithLLM(subjects: List<SubjectInput>) {
            val req = LlmScheduleRequest(subjects)
            val bearer = "Bearer ${BuildConfig.OPEN_API_KEY}"
            try {
                val res = openAi.getSchedule(bearer, req)
                // TODO: map -> SessionEntity + Calendar
            } catch (e: Exception) {
                Log.e("PlanRepo", "LLM call failed", e)
                throw e
            }
        }

        override suspend fun deletePlan(id: Long) {
            // no-op
        }
    }
