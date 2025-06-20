package com.example.studify.presentation.stat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studify.presentation.viewmodel.StatViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun StatScreen(
    navController: NavHostController,
    viewModel: StatViewModel = hiltViewModel()
) {
    val goals by viewModel.todayGoals.collectAsState()
    val done by viewModel.todayDone.collectAsState()

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        item {
            Text(
                text = "📈 오늘 공부 달성률",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(goals.size) { index ->
            val goal = goals[index]
            val goalInSeconds = goal.hours * 3600
            val doneForSubject = done.find { it.subject == goal.subject }?.seconds ?: 0
            val progress =
                if (goalInSeconds > 0) {
                    (doneForSubject.toFloat() / goalInSeconds).coerceIn(0f, 1f)
                } else {
                    0f
                }

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📚 ${goal.subject}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = progress,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(10.dp),
                        color = if (progress >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text =
                            String.format(
                                "⏱️ %d%% 완료 (%d / %d 초)",
                                (progress * 100).toInt(),
                                doneForSubject,
                                goalInSeconds
                            ),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (progress >= 1f) Color(0xFF4CAF50) else Color.Gray
                    )
                }
            }
        }
    }
}
