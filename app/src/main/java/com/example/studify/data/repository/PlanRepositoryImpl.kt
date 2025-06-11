package com.example.studify.data.repository

import android.content.Context
import com.example.studify.BuildConfig
import com.example.studify.data.local.dao.PlanDao
import com.example.studify.data.local.dao.StudySessionDao
import com.example.studify.data.local.dao.SubjectDao
import com.example.studify.data.local.entity.StudyPlanEntity
import com.example.studify.data.local.entity.StudySessionEntity
import com.example.studify.data.local.entity.SubjectEntity
import com.example.studify.data.remote.ChatCompletionRequest
import com.example.studify.data.remote.LlmScheduleResponse
import com.example.studify.data.remote.OpenAiService
import com.example.studify.data.remote.PromptBuilder
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.SubjectInput
import com.example.studify.util.CalendarServiceHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val openAi: OpenAiService,
        private val planDao: PlanDao,
        private val subjectDao: SubjectDao,
        private val sessionDao: StudySessionDao
    ) : PlanRepository {
        // 아직 미구현
        override fun observePlans() = planDao.observePlans()

        override fun observePlansWithSubjects() = planDao.observePlansWithSubjects()

        override suspend fun createPlanLocal(subjects: List<SubjectInput>) {
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
        }

        override suspend fun createPlanWithLLM(subjects: List<SubjectInput>) {
            // Google 계정 & bearer
            val account =
                GoogleSignIn.getLastSignedInAccount(context)
                    ?: error("No Google account")
            val bearer = "Bearer ${BuildConfig.OPEN_API_KEY}"

            // ChatCompletion 호출
            val chatReq =
                ChatCompletionRequest(
                    messages =
                        listOf(
                            PromptBuilder.buildSystem(),
                            PromptBuilder.buildUser(subjects)
                        )
                )
            val chatRes = openAi.chatCompletion(bearer, chatReq)

            // JSON 텍스트 -> LlmScheduleResponse
            val scheduleJson = chatRes.choices.first().message.content.trim()
            val schedule: List<LlmScheduleResponse.LlmSession> =
                Gson().fromJson(
                    scheduleJson,
                    object : TypeToken<List<LlmScheduleResponse.LlmSession>>() {}.type
                )

            // 새 plan + subject 저장
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

            schedule.forEach { llm ->
                val start = OffsetDateTime.parse(llm.start)
                val end = OffsetDateTime.parse(llm.end)

                val eventId =
                    CalendarServiceHelper.createEvent(
                        context = context,
                        account = account,
                        title = llm.subject,
                        startTime = start,
                        endTime = end
                    )

                sessionDao.upsert(
                    StudySessionEntity(
                        planId = planId,
                        subject = llm.subject,
                        date = start.toLocalDate().toString(),
                        startTime = start.toLocalTime().toString(),
                        endTime = end.toLocalTime().toString(),
                        examDate = subjects.first { it.subject == llm.subject }.examDate.toString(),
                        calendarEventId = eventId
                    )
                )
            }
        }

        override suspend fun deletePlan(id: Long) {
            // no-op
        }
    }
