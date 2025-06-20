package com.example.studify.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.domain.model.StudySession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.previousOrSame
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repo: FakeHomeRepository
    ) : ViewModel() {
        private val today = LocalDate.now()
        private val monday = today.with(previousOrSame(DayOfWeek.MONDAY))

        private val selectedDate = MutableStateFlow(today)

        val uiState: StateFlow<HomeUiState> =
            selectedDate
                .flatMapLatest { date ->
                    repo.observeSessions(date).map { list -> date to list }
                }
                .map { (date, list) ->
                    HomeUiState(
                        selectedWeekMonday = monday,
                        selectedDate = date,
                        sessions = list
                    )
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState(monday, today))

        init {
            // 더미 시드
            viewModelScope.launch {
                repo.seedIfEmpty(today)
            }
        }

        fun selectDate(date: LocalDate) {
            selectedDate.value = date
        }

        fun delete(id: Int) =
            viewModelScope.launch {
                repo.delete(id)
            }

        fun update(session: StudySession) =
            viewModelScope.launch {
                repo.updateSession(session)
            }
    }
