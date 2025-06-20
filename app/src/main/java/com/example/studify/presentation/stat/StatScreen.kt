package com.example.studify.presentation.stat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studify.presentation.viewmodel.StatViewModel
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studify.presentation.viewmodel.StatViewModel.DailyProgress

@Composable
fun StatScreen(
    navController: NavHostController,
    viewModel: StatViewModel = hiltViewModel()
) {
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
