package com.example.studify.presentation.stat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studify.presentation.viewmodel.StatViewModel
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun StatScreen(
    navController: NavHostController,
    viewModel: StatViewModel = hiltViewModel()
) {
    val goals by viewModel.tomorrowGoals.collectAsState()
//    val done by viewModel.todayDone.collectAsState()

    // 오늘 목표 대비 달성률
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
    ) {
        items(goals.size) { index ->
            val goal = goals[index]
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
            ) {
                Text(
                    "📚 [${goal.subject}] : ${goal.hours}시간",
                    style = MaterialTheme.typography.bodyLarge
                )
                LinearProgressIndicator(
                    // 현재 goal과 같은 과목의 done으로 progress 계산
                    progress = 1f,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(top = 4.dp)
                )
            }
        }
    }
}
