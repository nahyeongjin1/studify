package com.example.studify.domain.model

data class StudySession(
    val id: Int = 0,
    val subject: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val examDate: String,
    val calendarEventId: String? = null,
)
