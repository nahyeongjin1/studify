package com.example.studify.presentation.stat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun StatScreen(navController: NavHostController) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        item {
            ConsecutiveDays()
        }

        item {
            DailyProgress()
        }

        item {
            WeeklyProgress()
        }

        item {
            MostStudied()
        }
    }
}
