package com.example.studify.presentation.stat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studify.presentation.viewmodel.StatViewModel

@Composable
fun MostStudied(viewModel: StatViewModel = hiltViewModel()) {
    val mostStudied by viewModel.mostStudiedDay.collectAsState()

    if (mostStudied != null) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "🏆 가장 열심히 공부한 날",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📅 ${mostStudied!!.date}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        "⏰ ${(mostStudied!!.totalSeconds / 3600)}시간 " +
                            "${(mostStudied!!.totalSeconds % 3600) / 60}분 " +
                            "공부했어요!🔥🔥🔥",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
