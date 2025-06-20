package com.example.studify.presentation.home

import com.example.studify.domain.model.StudySession
import java.time.LocalDate

data class HomeUiState(
    val selectedWeekMonday: LocalDate,
    val selectedDate: LocalDate,
    val sessions: List<StudySession> = emptyList(),
    val studiedSeconds: Int = 0,
    val loading: Boolean = false,
) {
    val studiedText: String
        get() {
            val h = studiedSeconds / 3600
            val m = (studiedSeconds % 3600) / 60
            return "%dh %02dm".format(h, m)
        }
}
