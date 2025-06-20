package com.example.studify.data.repository

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.example.studify.BuildConfig
import com.example.studify.data.local.dao.PlanDao
import com.example.studify.data.local.dao.StudySessionDao
import com.example.studify.data.local.dao.SubjectDao
import com.example.studify.data.local.db.CategoryType
import com.example.studify.data.local.db.StudifyDatabase
import com.example.studify.data.local.entity.DayGoalEntity
import com.example.studify.data.local.entity.StudyPlanEntity
import com.example.studify.data.local.entity.StudySessionEntity
import com.example.studify.data.local.entity.SubjectEntity
import com.example.studify.data.remote.ChatCompletionRequest
import com.example.studify.data.remote.LlmScheduleResponse.LlmSession
import com.example.studify.data.remote.OpenAiService
import com.example.studify.data.remote.PromptBuilder
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.SubjectInput
import com.example.studify.util.CalendarServiceHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt

@Singleton
class PlanRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val openAi: OpenAiService,
        private val planDao: PlanDao,
        private val subjectDao: SubjectDao,
        private val sessionDao: StudySessionDao,
        private val db: StudifyDatabase
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
            withContext(Dispatchers.IO) {
                // Google 계정 & bearer
                val account =
                    GoogleSignIn.getLastSignedInAccount(context)
                        ?: error("No Google account")
                val bearer = "Bearer ${BuildConfig.OPEN_API_KEY}"

                // LLM 호출
                val chatReq =
                    ChatCompletionRequest(
                        messages =
                            listOf(
                                PromptBuilder.buildSystem(),
                                PromptBuilder.buildUser(subjects)
                            )
                    )
                val chatRes = openAi.chatCompletion(bearer, chatReq)

                // Parsing & 사전 검증
                val raw = chatRes.choices.first().message.content.trim()
                var sessions = parseLlmJson(raw)
                sessions = validateAndRebalance(sessions, subjects)

                val examMap = subjects.associate { it.subject to it.examDate }
                sessions =
                    sessions.filter {
                        OffsetDateTime.parse(it.start).toLocalDate() < examMap.getValue(it.subject)
                    }

                // 캘린더에서 기존 이벤트 삭제 (지나간 일정은 보존)
                CalendarServiceHelper.purgeStudyEvents(
                    context = context,
                    account = account,
                    from = OffsetDateTime.now()
                )

                // 과목별 날짜 횟수 (시간) 셈
                val groupedByDate: Map<String, Map<String, Int>> =
                    sessions
                        .groupBy { session ->
                            // 날짜만 추출 (YYYY-MM-DD)
                            OffsetDateTime.parse(session.start).toLocalDate().toString()
                        }
                        .mapValues { (_, sessionList) ->
                            // 날짜에 해당하는 세션들 → 과목별로 그룹화 후 개수 세기
                            sessionList.groupingBy { it.subject }.eachCount()
                        }

                groupedByDate.forEach { (date, subjectCounts) ->
                    subjectCounts.forEach { (subject, count) ->
                        val entity =
                            DayGoalEntity(
                                subject = subject,
                                hours = count,
                                date = date
                            )
                        db.dayGoalDao().insert(entity)
                    }
                }

                // DB & 캘린더 삽입 (한 트랜잭션)
                runCatching {
                    db.withTransaction {
                        val planId = planDao.upsert(StudyPlanEntity())

                        // subjects
                        subjects.forEach {
                            subjectDao.upsert(
                                SubjectEntity(
                                    planId = planId,
                                    name = it.subject,
                                    credits = it.credits,
                                    importance = it.importance,
                                    category = it.category,
                                    examDate = it.examDate.toString()
                                )
                            )
                        }

                        // sessions + Calendar insert
                        val sessionEntities =
                            sessions.map { llm ->
                                val start = parseIsoDateTime(llm.start)
                                val end = parseIsoDateTime(llm.end)

                                val eventId =
                                    CalendarServiceHelper.createEvent(
                                        context = context,
                                        account = account,
                                        title = llm.subject,
                                        startTime = start,
                                        endTime = end
                                    ) ?: error("캘린더 이벤트 생성 실패")

                                StudySessionEntity(
                                    planId = planId,
                                    subject = llm.subject,
                                    date = start.toLocalDate().toString(),
                                    startTime = start.toLocalTime().toString(),
                                    endTime = end.toLocalTime().toString(),
                                    examDate = examMap.getValue(llm.subject).toString(),
                                    calendarEventId = eventId
                                )
                            }

                        sessionDao.upsertAll(sessionEntities)
                    }
                }.onFailure { e ->
                    CalendarServiceHelper.purgeStudyEvents(
                        context = context,
                        account = account,
                        from = OffsetDateTime.now()
                    )
                    throw e
                }.getOrThrow()
            }
        }

        override suspend fun deletePlan(id: Long) {
            // no-op
        }
    }

private fun parseIsoDateTime(str: String): OffsetDateTime {
    // 이미 오프셋이 있으면 그대로 파싱
    if (str.contains('+') || str.endsWith('Z')) return OffsetDateTime.parse(str)

    // 없으면 기본 +09:00(한국) 붙이기 - 필요 시 TimeZone 얻어와 동적으로 변환
    Log.w("PlanParser", "timestamp missing offset -> auto-fixing: $str -> +09:00")
    return OffsetDateTime.parse("$str+09:00")
}

private fun validateAndRebalance(
    sessions: List<LlmSession>,
    subjects: List<SubjectInput>
): List<LlmSession> {
    val weight =
        subjects.associate { s ->
            val w = s.credits * s.importance * if (s.category == CategoryType.Major) 2 else 1
            s.subject to w
        }
    val totalWeight = weight.values.sum()
    val totalSessions = sessions.size
    val expected =
        weight.mapValues { (_, w) ->
            max(1, (w * totalSessions / totalWeight.toDouble()).roundToInt())
        }

    // 시험일이 지난 & 금지 시각 (12:00, 18:00) 세션 제거
    val examDateMap = subjects.associate { it.subject to it.examDate }
    val forbiddenStarts = setOf("12:00", "18:00")
    val filtered =
        sessions.filterNot { sess ->
            val startTs = OffsetDateTime.parse(sess.start)
            val exam = examDateMap[sess.subject]
            val isOnOrAfterExam = exam != null && startTs.toLocalDate() >= exam
            val isForbiddenTime = startTs.toLocalTime().toString().substring(0, 5) in forbiddenStarts
            isOnOrAfterExam || isForbiddenTime
        }

    val bySubj = filtered.groupBy { it.subject }
    val imbalanced =
        bySubj.any { (subj, list) ->
            val ratio = list.size.toDouble() / expected.getValue(subj)
            ratio !in 0.6..1.4
        }

    return if (imbalanced) {
        Log.w("PlanGen", "imbalanced detected -> rebalancing")
        val queues = bySubj.mapValues { it.value.toMutableList() }.toMutableMap()
        rebalanceByWeight(queues, expected.toMutableMap())
            .sortedBy { it.start }
    } else {
        filtered
    }
}

private fun rebalanceByWeight(
    queues: MutableMap<String, MutableList<LlmSession>>,
    remaining: MutableMap<String, Int>
): List<LlmSession> {
    val result = mutableListOf<LlmSession>()
    while (result.size < remaining.values.sum()) {
        val nextEntry =
            remaining
                .filter { (subj, _) -> queues[subj]?.isNotEmpty() == true }
                .maxByOrNull { (subj, need) -> need - result.count { it.subject == subj } }
                ?: break
        val subj = nextEntry.key
        queues[subj]?.removeFirstOrNull()?.let(result::add)
    }
    return result
}

private fun parseLlmJson(raw: String): List<LlmSession> {
    val clean =
        raw
            .removePrefix("```json")
            .removeSuffix("```")
            .trim()

    val json =
        when (val elem = JsonParser.parseString(clean)) {
            is com.google.gson.JsonArray -> clean
            is com.google.gson.JsonObject ->
                elem["schedule"]?.toString()
                    ?: error("schedule field missing")
            else -> error("Unexpected JSON: $clean")
        }

    return Gson().fromJson(
        json,
        object : TypeToken<List<LlmSession>>() {}.type
    )
}
