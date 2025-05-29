package com.example.studify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    val subject: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val examDate: String,
    val calendarEventId: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
