package com.example.studify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.data.local.dao.DayDoneDao
import com.example.studify.data.local.dao.DayGoalDao
import com.example.studify.data.local.entity.DayGoalEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class StatViewModel
    @Inject
    constructor(private val dayGoalDao: DayGoalDao, private val dayDoneDao: DayDoneDao) : ViewModel() {
        private val todayDate: String =
            OffsetDateTime.now().toLocalDate().toString() // "YYYY-MM-DD"

        val todayGoals: StateFlow<List<DayGoalEntity>> =
            dayGoalDao.findDayGoal(todayDate)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        private val tomorrowDate: String =
            OffsetDateTime.now().plusDays(1).toLocalDate().toString() // "YYYY-MM-DD"

        val tomorrowGoals: StateFlow<List<DayGoalEntity>> =
            dayGoalDao.findDayGoal(tomorrowDate)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

//        val weeklyGoals: StateFlow<List<SubjectGoalSummary>> =
//            dayGoalDao.findPeriodicalGoalsGroupedBySubject(
//                OffsetDateTime.now().plusDays(-7).toLocalDate().toString(),
//                todayDate
//            )
//                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//        val monthlyGoals: StateFlow<List<SubjectGoalSummary>> =
//            dayGoalDao.findPeriodicalGoalsGroupedBySubject(
//                OffsetDateTime.now().plusDays(-30).toLocalDate().toString(),
//                todayDate
//            )
//                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

enum class PeriodFilter {
    ONE_DAY,
    SEVEN_DAYS,
    ALL
}
