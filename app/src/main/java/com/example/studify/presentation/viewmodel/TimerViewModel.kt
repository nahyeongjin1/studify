package com.example.studify.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.data.local.dao.DayDoneDao
import com.example.studify.data.local.dao.DayGoalDao
import com.example.studify.data.local.db.DateConverters.toLocalDate
import com.example.studify.data.local.entity.DayDoneEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

@HiltViewModel
class TimerViewModel
    @Inject
    constructor(
        private val goalDao: DayGoalDao,
        private val doneDao: DayDoneDao
    ) : ViewModel() {
        private val _subjects = MutableStateFlow<List<String>>(emptyList())
        val subjects: StateFlow<List<String>> = _subjects

        init {
            val today = OffsetDateTime.now().toLocalDate().toString()
            Log.i("TimerVIewModel/init", "$today")
            viewModelScope.launch {
                goalDao.findDayGoal(today).collect { goals ->
                    _subjects.value = goals.map { it.subject }
                }
            }
        }

        suspend fun insertDone(
            subject: String,
            seconds: Int
        ) {
            val today = OffsetDateTime.now().toLocalDate().toString()
            doneDao.insert(DayDoneEntity(subject = subject, date = today, seconds = seconds))
            Log.i("TimerViewModel", "$subject $today $seconds")
        }
    }
