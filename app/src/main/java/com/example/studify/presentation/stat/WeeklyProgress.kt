package com.example.studify.presentation.stat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studify.presentation.viewmodel.StatViewModel
import com.example.studify.presentation.viewmodel.StatViewModel.DailyProgress
import kotlin.collections.forEach

@Composable
fun WeeklyProgress(viewModel: StatViewModel = hiltViewModel()) {
    val weekly by viewModel.weeklyProgress.collectAsState()

    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "📊 최근 7일간 달성률 그래프",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    WeeklyBarChart(data = weekly)
}

@Composable
fun WeeklyBarChart(
    data: List<DailyProgress>,
    modifier: Modifier = Modifier
) {
    val maxBarHeight = 150.dp
    val barWidth = 32.dp

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        data.forEach { day ->
            val ratio = day.progress.coerceIn(0f, 1f)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier =
                        Modifier
                            .height(maxBarHeight * ratio)
                            .width(barWidth)
                            .background(
                                if (ratio >= 1f) {
                                    Color(0xFF4CAF50)
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    // MM-DD
                    text = day.date.substring(5),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "${(ratio * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
