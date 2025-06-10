package com.example.studify.data.repository

import com.example.studify.data.local.dao.StudySessionDao
import com.example.studify.data.local.entity.StudySessionEntity
import com.example.studify.domain.model.StudySession
import com.example.studify.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepositoryImpl
    @Inject
    constructor(
        private val dao: StudySessionDao,
    ) : StudyRepository {
        override fun getAllSessions(): Flow<List<StudySession>> {
            return dao.getAllSessions().map { list ->
                list.map { it.toDomainModel() }
            }
        }

        override suspend fun addSession(session: StudySession) {
            dao.insertSession(session.toEntity())
            // TODO: Google Calendar 연동 로직 삽입 예정
        }

        override suspend fun deleteSession(sessionId: Int) {
            dao.deleteSessionById(sessionId)
            // TODO: Google Calendar 이벤트 삭제도 함께
        }

        override suspend fun updateCalendarEventId(
            sessionId: Int,
            calendarEventId: String,
        ) {
            val all =
                dao.getAllSessions()
                    .map { list -> list.find { it.id == sessionId } }
                    .firstOrNull()

            all?.let {
                val updated = it.copy(calendarEventId = calendarEventId)
                dao.updateSession(updated)
            }
        }

        override suspend fun syncWithGoogleCalendar(session: StudySession) {
            // TODO: Google Calendar 연동 로직 작성
        }

        // --- 매핑 함수들 ---

        private fun StudySessionEntity.toDomainModel() =
            StudySession(
                id = id,
                subject = subject,
                date = date,
                startTime = startTime,
                endTime = endTime,
                examDate = examDate,
                calendarEventId = calendarEventId,
            )

        private fun StudySession.toEntity() =
            StudySessionEntity(
                id = id,
                subject = subject,
                date = date,
                startTime = startTime,
                endTime = endTime,
                examDate = examDate,
                calendarEventId = calendarEventId,
            )
    }
