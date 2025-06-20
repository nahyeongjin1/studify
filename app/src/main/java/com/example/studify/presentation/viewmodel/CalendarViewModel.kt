package com.example.studify.presentation.viewmodel

import android.content.Context
import android.util.Log
import com.example.studify.util.CalendarServiceHelper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.calendar.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class CalendarViewModel(
    private val context: Context,
    private val account: GoogleSignInAccount
) : ViewModel() {

    private val _eventsByDate = MutableStateFlow<Map<LocalDate, List<Event>>>(emptyMap())
    val eventsByDate = _eventsByDate.asStateFlow()

    fun loadEventsForWeek(currentDate: LocalDate) {
        viewModelScope.launch {
            val startOfWeek = currentDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
            val startDateTime = startOfWeek.atStartOfDay().atOffset(ZoneOffset.ofHours(9))  // 한국시간 +09:00
            val endDateTime = startOfWeek.plusDays(6).atTime(23,59,59).atOffset(ZoneOffset.ofHours(9))

            val events = CalendarServiceHelper.getEventsInRange(context, account, startDateTime, endDateTime)
            val grouped = events.groupBy { event ->
                // 이벤트 시작 날짜 (LocalDate 변환)
                event.start.dateTime?.let {
                    // DateTime -> Instant -> LocalDate 변환
                    val instant = java.time.Instant.ofEpochMilli(it.value)
                    instant.atZone(java.time.ZoneId.of("Asia/Seoul")).toLocalDate()
                } ?: event.start.date?.let {
                    // all-day 이벤트는 date 필드에 날짜만 있음 (yyyy-MM-dd)
                    val dateStr = it.toStringRfc3339() // "yyyy-MM-dd"
                    LocalDate.parse(dateStr)
                } ?: startOfWeek  // fallback
            }

            grouped.forEach { (date, list) ->
            }

            _eventsByDate.value = grouped
        }
    }
}
