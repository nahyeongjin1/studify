package com.example.studify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_goal")
data class DayGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val date: String,
    val hours: Int
)
