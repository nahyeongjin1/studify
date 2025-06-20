package com.example.studify.presentation.timer

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studify.presentation.viewmodel.TimerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeScreen(
    navController: NavHostController,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val subjects by viewModel.subjects.collectAsState()

    var selectedSubject by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var timeElapsed by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var lastTick by remember { mutableStateOf(0L) }

    var showTimer by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            if (isRunning && lastTick > 0L) {
                delay(1000L)
                val now = System.currentTimeMillis()
                val elapsed = ((now - lastTick) / 1000).toInt()
                if (elapsed > 0) {
                    timeElapsed += elapsed
                    lastTick = now
                    Log.d("타이머", "Tick: +$elapsed → 총 $timeElapsed 초")
                }
            } else {
                lastTick = 0L
                delay(100L)
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "📘 오늘의 공부 타이머",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 드롭다운
        if (subjects.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
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
                                isRunning = false
                                timeElapsed = 0
                                showTimer = true
                            }
                        )
                    }
                }
            }
        } else {
            Text("📭 오늘의 계획이 없습니다.", color = Color.Red)
        }

        Spacer(Modifier.height(40.dp))

        if (showTimer) {
            Text(
                text = String.format("%02d:%02d", timeElapsed / 60, timeElapsed % 60),
                fontSize = 64.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (selectedSubject.isNotBlank()) {
                        lastTick = System.currentTimeMillis()
                        isRunning = true
                        showTimer = true
                    }
                },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            ) {
                Text("시작")
            }

            Button(
                onClick = {
                    if (selectedSubject.isNotBlank()) {
                        val now = System.currentTimeMillis()
                        val elapsed = ((now - lastTick) / 1000).toInt()
                        if (isRunning && elapsed > 0) timeElapsed += elapsed

                        isRunning = false
                        lastTick = 0L

                        val toInsert = timeElapsed
                        if (toInsert > 0) {
                            coroutineScope.launch {
                                viewModel.insertDone(selectedSubject, toInsert)
                                Log.i("종료", "$selectedSubject ${LocalDate.now()} $toInsert")
                            }
                        }

                        timeElapsed = 0
                        showTimer = false
                    }
                },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            ) {
                Text("종료")
            }
        }
    }
}
