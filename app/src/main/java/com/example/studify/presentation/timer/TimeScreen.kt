package com.example.studify.presentation.timer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studify.data.local.dao.PlanDao
import com.example.studify.data.local.entity.StudyPlanEntity
import com.example.studify.data.local.entity.SubjectEntity
import com.example.studify.domain.repository.SubjectInput
import com.example.studify.presentation.viewmodel.TimerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeScreen(
    navController: NavHostController,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dataStore = remember { StudyTimeDataStore(context) }
    val coroutineScope = rememberCoroutineScope()

    val subjects by viewModel.subjects.collectAsState(initial = emptyList())
    val goalTimeMap = remember {
        subjects.associateWith { subject: String ->
            when (subject) {
                "운영체제" -> 600
                "데이터베이스" -> 480
                "소프트웨어" -> 360
                else -> 300 // 기본 목표 시간 (분 단위)
            }
        }
    }
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull() ?: "") }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(25 * 60) }
    var lastTick by remember { mutableStateOf(System.currentTimeMillis()) }

    // 공부 시간 저장용 (분 단위)
    var studyTimeMap by remember { mutableStateOf(mutableMapOf<String, Int>()) }

    // 저장된 시간 불러오기
    LaunchedEffect(subjects) {
        if(subjects.isNotEmpty() && selectedSubject.isBlank()){
            selectedSubject = subjects.first()
            val loadedMap = dataStore.getAllStudyTimes(subjects).first()
            studyTimeMap = loadedMap.toMutableMap()
        }
    }

    // 타이머 로직
    LaunchedEffect(isRunning, isPaused, selectedSubject) {
        while (isRunning && !isPaused && timeLeft > 0) {
            delay(1000L)
            timeLeft--

            // 자동 저장용: 1초마다 체크해 누적
            val now = System.currentTimeMillis()
            val elapsed = ((now - lastTick) / 1000).toInt()
            lastTick = now

            if (elapsed > 0) {
                val newTime = studyTimeMap.getOrDefault(selectedSubject, 0) + elapsed
                studyTimeMap[selectedSubject] = newTime

                coroutineScope.launch {
                    dataStore.setStudyTime(selectedSubject, newTime)
                }
            }
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
        Text("\"계획에서 등록된 과목을 선택하고 공부하세요")

        Spacer(modifier = Modifier.height(20.dp))

        // 과목 선택 드롭다운
        var expanded by remember { mutableStateOf(false) }
        if (subjects.isNotEmpty()) {
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
                                timeLeft = 25 * 60
                                isRunning = false
                                isPaused = false
                            }
                        )
                    }
                }
            }
        } else {
            Text("계획에서 과목을 먼저 추가해주세요.", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(String.format("%02d:%02d", minutes, seconds), fontSize = 48.sp)

        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                isRunning = true
                isPaused = false
                lastTick = System.currentTimeMillis()
            }, colors = buttonColors(containerColor = Color.Green)) {
                Text("시작")
            }
            Button(onClick = { isPaused = true }, colors = buttonColors(containerColor = Color.Blue)) {
                Text("일시정지")
            }
            Button(onClick = {
                isRunning = false
                isPaused = false
                timeLeft = 25 * 60
            }, colors = buttonColors(containerColor = Color.Red)) {
                Text("종료")
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
        Text("과목별 진행률", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(20.dp))

        // 동적으로 과목 진행률 표시
        subjects.forEach { subject ->
            val studiedMinutes = studyTimeMap.getOrDefault(subject, 0) / 60f
            val goalMinutes = goalTimeMap[subject]?.toFloat() ?: 1f
            SubjectProgress(
                name = subject,
                current = studiedMinutes,
                total = goalMinutes
            )
        }
    }
}


@Composable
fun SubjectProgress(name: String, current: Float, total: Float) {
    val percent = (current / total).coerceIn(0f, 1f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(name)
        LinearProgressIndicator(
            progress = { percent },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.LightGray
        )
        Text(
            String.format("%.1fh / %.1fh (%.0f%%)", current, total, percent * 100),
            fontSize = 12.sp
        )
    }
}




