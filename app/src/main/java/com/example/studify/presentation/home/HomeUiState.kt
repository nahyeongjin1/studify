package com.example.studify.presentation.home

import com.example.studify.domain.model.StudySession
import java.time.LocalDate

data class HomeUiState(
    val selectedWeekMonday: LocalDate,
    val selectedDate: LocalDate,
    val sessions: List<StudySession> = emptyList(),
    val loading: Boolean = false,
)
