package com.example.studify.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen() {
    val subjects = listOf("운영체제", "데이터베이스", "소프트웨어")
    var selectedSubject by remember { mutableStateOf(subjects[0]) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(25 * 60) } // 25분

    // 타이머 로직
    LaunchedEffect(isRunning, isPaused) {
        while (isRunning && !isPaused && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F0FF))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("타이머", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("과목을 선택하고 공부 시간을 기록하세요")

        Spacer(modifier = Modifier.height(20.dp))
        // 과목 선택 드롭다운
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = selectedSubject,
                onValueChange = {},
                readOnly = true,
                label = { Text("과목 선택") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                subjects.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            selectedSubject = it
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(String.format("%02d:%02d", minutes, seconds), fontSize = 48.sp)

        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { isRunning = true; isPaused = false }, colors = buttonColors(containerColor = Color.Green)) {
                Text("시작")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = { isPaused = true }, colors = buttonColors(containerColor = Color.Blue)) {
                Text("일시정지")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(onClick = {
                isRunning = false
                isPaused = false
                timeLeft = 25 * 60
            }, colors = buttonColors(containerColor = Color.Red)) {
                Text("종료")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        Text("과목별 진행률", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))
        SubjectProgress("OS", 5, 10)
        SubjectProgress("SW", 2, 6)
        SubjectProgress("DB", 1, 8)
    }
}

@Composable
fun SubjectProgress(name: String, current: Int, total: Int) {
    val percent = (current.toFloat() / total.toFloat())
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text("$name")
        LinearProgressIndicator(
            progress = percent,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.LightGray
        )
        Text("${current}h / ${total}h (${(percent * 100).toInt()}%)", fontSize = 12.sp)
    }
}


