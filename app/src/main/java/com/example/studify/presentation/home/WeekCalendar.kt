package com.example.studify.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.api.services.calendar.model.Event
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun WeekCalendar(
    currentDate: LocalDate,
    eventsByDate: Map<LocalDate, List<Event>>,
    onDateSelected: (LocalDate) -> Unit
) {

    val startOfWeek = currentDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val dates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        dates.forEach { date ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfWeek.name.take(2),
                    fontSize = 12.sp
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    modifier = Modifier
                        .clickable { onDateSelected(date) }
                        .background(
                            if (date == currentDate) Color.LightGray else Color.Transparent,
                            shape = CircleShape
                        )
                        .padding(8.dp)
                )
                if (eventsByDate[date]?.isNotEmpty() == true) {
                    Box(Modifier.size(6.dp).background(Color.Red, shape = CircleShape))
                }
            }
        }
    }
}
