package com.example.studify.domain.model

import com.example.studify.data.local.entity.StudySessionEntity

data class StudySession(
    val id: Int = 0,
    val subject: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val examDate: String,
    val calendarEventId: String? = null,
    val planId: Long = 0
)

fun StudySession.toEntity(): StudySessionEntity {
    return StudySessionEntity(
        id = id,
        planId = planId,
        subject = subject,
        date = date,
        startTime = startTime,
        endTime = endTime,
        examDate = examDate,
        calendarEventId = calendarEventId
    )
}
