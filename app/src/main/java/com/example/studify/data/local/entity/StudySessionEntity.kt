package com.example.studify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    val subject: String,
    val date: String,       // yyyy-MM-dd
    val startTime: String,  // HH:mm
    val endTime: String,    // HH:mm
    val examDate: String,   // yyyy-MM-dd
    val calendarEventId: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
