package com.example.studify.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = StudyPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planId")]
)
data class StudySessionEntity(
    val planId: Long? = null,
    val subject: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val examDate: String,
    val calendarEventId: String? = null,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
