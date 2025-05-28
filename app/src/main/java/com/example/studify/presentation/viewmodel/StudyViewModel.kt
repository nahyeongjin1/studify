package com.example.studify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.domain.model.StudySession
import com.example.studify.domain.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val repository: StudyRepository
) : ViewModel() {

    // 전체 세션을 state flow로 구독
    val studySessions: StateFlow<List<StudySession>> =
        repository.getAllSessions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 새로운 세션 추가
    fun addSession(session: StudySession) {
        viewModelScope.launch {
            repository.addSession(session)
        }
    }

    // 세션 삭제
    fun deleteSession(sessionId: Int) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
        }
    }

    // Calendar ID 업데이트 (예정)
    fun updateCalendarEventId(sessionId: Int, eventId: String) {
        viewModelScope.launch {
            repository.updateCalendarEventId(sessionId, eventId)
        }
    }

    // 수동 동기화 (예정)
    fun syncCalendar(session: StudySession) {
        viewModelScope.launch {
            repository.syncWithGoogleCalendar(session)
        }
    }
}
