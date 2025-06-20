package com.example.studify.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.data.local.dao.DayDoneDao
import com.example.studify.data.local.dao.DayGoalDao
import com.example.studify.data.local.entity.DayDoneEntity
import com.example.studify.data.local.entity.DayGoalEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
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

        // For Test
//        private val tomorrowDate: String =
//            OffsetDateTime.now().plusDays(1).toLocalDate().toString() // "YYYY-MM-DD"
//        val tomorrowGoals: StateFlow<List<DayGoalEntity>> =
//            dayGoalDao.findDayGoal(tomorrowDate)
//                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val todayDone: StateFlow<List<DayDoneEntity>> =
            dayDoneDao.getAll(todayDate)
                .map { list ->
                    list.groupBy { it.subject }
                        .map { (subject, entries) ->
                            DayDoneEntity(
                                subject = subject,
                                date = todayDate,
                                seconds = entries.sumOf { it.seconds }
                            )
                        }
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        data class DailyProgress(
            val date: String,
            val progress: Float
        )

        val weeklyProgress: StateFlow<List<DailyProgress>> =
            combine(
                dayGoalDao.findPeriodicalGoals(
                    OffsetDateTime.now().minusDays(6).toLocalDate().toString(),
                    OffsetDateTime.now().toLocalDate().toString()
                ),
                dayDoneDao.findPeriodicalDone(
                    OffsetDateTime.now().minusDays(6).toLocalDate().toString(),
                    OffsetDateTime.now().toLocalDate().toString()
                )
            ) { goals, dones ->
                val groupedGoals = goals.groupBy { it.date }.mapValues { it.value.sumOf { g -> g.totalHours * 3600 } }
                val groupedDones = dones.groupBy { it.date }.mapValues { it.value.sumOf { d -> d.totalSeconds } }

                // 시작일 → 종료일까지 정방향 날짜 리스트 생성
                val startDate = OffsetDateTime.now().minusDays(6).toLocalDate()
                val dateList = (0..6).map { i -> startDate.plusDays(i.toLong()).toString() }

                dateList.map { date ->
                    val goal = groupedGoals[date] ?: 0
                    val done = groupedDones[date] ?: 0
                    Log.i("weeklyProgress", "$date $goal $done")
                    DailyProgress(
                        date = date,
                        progress = if (goal > 0) (done.toFloat() / goal).coerceIn(0f, 1f) else 0f
                    )
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        private val _mostStudiedDay = MutableStateFlow<DayDoneDao.DayMaxDone?>(null)
        val mostStudiedDay: StateFlow<DayDoneDao.DayMaxDone?> = _mostStudiedDay

        private val _streakCount = MutableStateFlow(0)
        val streakCount: StateFlow<Int> = _streakCount

        init {
            viewModelScope.launch {
                try {
                    _mostStudiedDay.value = dayDoneDao.getMostStudiedDay()
                } catch (e: Exception) {
                    Log.e("StatViewModel", "getMostStudiedDay failed: ${e.message}")
                }

                val studyDates = dayDoneDao.getStudyDatesDesc().map { LocalDate.parse(it) }
                val today = LocalDate.now()
                var streak = 0
                var current = today

                for (date in studyDates) {
                    if (date == current) {
                        streak++
                        current = current.minusDays(1)
                    } else {
                        break
                    }
                }

                _streakCount.value = streak
            }
        }
    }
