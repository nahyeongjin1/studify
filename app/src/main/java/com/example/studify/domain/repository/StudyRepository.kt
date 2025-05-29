package com.example.studify.domain.repository

import com.example.studify.domain.model.StudySession
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    /**
     * 로컬 DB에 저장된 모든 공부 세션을 스트리밍 방식으로 가져옴
     */
    fun getAllSessions(): Flow<List<StudySession>>

    /**
     * 새로운 공부 세션을 추가
     * - Room DB에 저장
     * - Google Calendar에도 이벤트 생성
     */
    suspend fun addSession(session: StudySession)

    /**
     * 특정 세션을 삭제
     * - Room DB 삭제
     * - Google Calendar 이벤트 삭제
     */
    suspend fun deleteSession(sessionId: Int)

    /**
     * Google Calendar 이벤트 ID를 저장
     * - sessionId 기준으로 업데이트
     */
    suspend fun updateCalendarEventId(
        sessionId: Int,
        calendarEventId: String,
    )

    /**
     * 이미 존재하는 세션을 Google Calendar에 동기화 (수동 동기화)
     */
    suspend fun syncWithGoogleCalendar(session: StudySession)
}
