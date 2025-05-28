package com.example.studify.domain.model

data class StudySession(
    val id: Int = 0,                       // Room entity primary key
    val subject: String,                   // 과목명
    val date: String,                      // yyyy-MM-dd
    val startTime: String,                 // HH:mm
    val endTime: String,                   // HH:mm
    val examDate: String,                  // yyyy-MM-dd
    val calendarEventId: String? = null    // Google Calendar event ID
)
