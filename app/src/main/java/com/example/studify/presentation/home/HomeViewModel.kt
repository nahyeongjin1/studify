package com.example.studify.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.data.local.dao.DayDoneDao
import com.example.studify.data.local.dao.DayGoalDao
import com.example.studify.domain.model.StudySession
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
        private val studyRepo: StudyRepository,
        private val planRepo: PlanRepository,
        private val dayGoalDao: DayGoalDao,
        private val dayDoneDao: DayDoneDao
    ) : ViewModel() {
        private val today = LocalDate.now()
        private val selectedDate = MutableStateFlow(today)

        private val monday = today.with(previousOrSame(DayOfWeek.MONDAY))

        val subjects: StateFlow<List<String>> =
            planRepo.observePlansWithSubjects()
                .map { plans -> plans.firstOrNull()?.subjects?.map { it.name } ?: emptyList() }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val uiState: StateFlow<HomeUiState> =
            selectedDate
                .flatMapLatest { date ->
                    combine(
                        studyRepo.getAllSessions()
                            .map { list -> list.filter { it.date == date.toString() } },
                        dayDoneDao.getAll(date.toString())
                    ) { sessions, doneList ->
                        val totalSec = doneList.sumOf { it.seconds }
                        HomeUiState(
                            selectedWeekMonday = monday,
                            selectedDate = date,
                            sessions = sessions,
                            studiedSeconds = totalSec
                        )
                    }
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState(monday, today))

        fun selectDate(date: LocalDate) {
            selectedDate.value = date
        }

        fun delete(id: Int) =
            viewModelScope.launch {
                studyRepo.deleteSession(id)
            }

        fun update(session: StudySession) =
            viewModelScope.launch {
                studyRepo.updateSession(session)
            }
    }
