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
class StudySessionViewModel
    @Inject
    constructor(
        private val repo: StudyRepository
    ) : ViewModel() {
        val sessions: StateFlow<List<StudySession>> =
            repo.getAllSessions()
                .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        fun add(s: StudySession) = viewModelScope.launch { repo.addSession(s) }

        fun delete(id: Int) = viewModelScope.launch { repo.deleteSession(id) }

        fun sync(s: StudySession) = viewModelScope.launch { repo.syncWithGoogleCalendar(s) }
    }
