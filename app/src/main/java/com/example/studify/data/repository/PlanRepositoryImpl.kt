package com.example.studify.data.repository

import com.example.studify.BuildConfig
import com.example.studify.data.local.dao.PlanDao
import com.example.studify.data.local.dao.PlanWithSubjects
import com.example.studify.data.local.dao.StudySessionDao
import com.example.studify.data.local.dao.SubjectDao
import com.example.studify.data.local.entity.StudyPlanEntity
import com.example.studify.data.local.entity.StudySessionEntity
import com.example.studify.data.local.entity.SubjectEntity
import com.example.studify.data.remote.LlmScheduleRequest
import com.example.studify.data.remote.OpenAiService
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.SubjectInput
import kotlinx.coroutines.flow.flowOf
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepositoryImpl
    @Inject
    constructor(
        private val openAi: OpenAiService,
        private val planDao: PlanDao,
        private val subjectDao: SubjectDao,
        private val sessionDao: StudySessionDao
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
            val resp = openAi.getSchedule(bearer, req)

            val planId = planDao.upsert(StudyPlanEntity())

            subjects.forEach { s ->
                subjectDao.upsert(
                    SubjectEntity(
                        planId = planId,
                        name = s.subject,
                        credits = s.credits,
                        importance = s.importance,
                        category = s.category,
                        examDate = s.examDate.toString()
                    )
                )
            }

            resp.schedule.forEach { llm ->
                val start = OffsetDateTime.parse(llm.start)
                val end = OffsetDateTime.parse(llm.end)

                sessionDao.upsert(
                    StudySessionEntity(
                        planId = planId,
                        subject = llm.subject,
                        date = start.toLocalDate().toString(),
                        startTime = start.toLocalTime().toString(),
                        endTime = end.toLocalTime().toString(),
                        examDate = subjects.first { it.subject == llm.subject }.examDate.toString()
                    )
                )
            }
        }

        override suspend fun deletePlan(id: Long) {
            // no-op
        }
    }
