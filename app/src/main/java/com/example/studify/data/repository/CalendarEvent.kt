package com.example.studify.data.repository

import java.time.LocalDate
import java.time.LocalTime

interface CalendarRepository {
    suspend fun getEventsInRange(start: LocalDate, end: LocalDate): List<CalendarEvent>
}

data class CalendarEvent(
    val id: String,
    val title: String,
    val date: LocalDate,
    val startTime: LocalTime?,
    val endTime: LocalTime?
)

