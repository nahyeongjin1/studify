package com.example.studify.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studify.domain.model.StudySession
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSessionSheet(
    initial: StudySession,
    onDismiss: () -> Unit,
    onSave: (StudySession) -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    // subject list (전체 과목) – 이후 플랜별 필터로 교체
    val subjects by vm.subjects.collectAsState()

    var subject by remember { mutableStateOf(initial.subject) }
    var start by remember { mutableStateOf(LocalTime.parse(initial.startTime)) }
    var end by remember { mutableStateOf(LocalTime.parse(initial.endTime)) }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    if (showStartPicker) {
        TimePickerDialog24h(
            initial = start,
            onDismiss = { showStartPicker = false },
            onConfirm = { time ->
                start = time
                showStartPicker = false
            }
        )
    }
    if (showEndPicker) {
        TimePickerDialog24h(
            initial = end,
            onDismiss = { showEndPicker = false },
            onConfirm = { time ->
                end = time
                showEndPicker = false
            }
        )
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // 드롭다운
            if (subjects.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = subject,
                        onValueChange = {},
                        label = { Text("과목") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        subjects.forEach { sub ->
                            DropdownMenuItem(text = { Text(sub) }, onClick = {
                                subject = sub
                                expanded = false
                            })
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = "No subjects",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("과목") }
                )
            }

            // Time pickers
            OutlinedButton(
                onClick = { showStartPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("시작  ${start.format(timeFmt)}") }

            OutlinedButton(
                onClick = { showEndPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("종료  ${end.format(timeFmt)}") }

            Button(
                onClick = {
                    onSave(
                        initial.copy(
                            subject = subject,
                            startTime = start.toString(),
                            endTime = end.toString()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("저장") }
        }
    }
}

private val timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog24h(
    initial: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
) {
    // ① TimePickerState 기억
    val state =
        rememberTimePickerState(
            initialHour = initial.hour,
            initialMinute = initial.minute,
            is24Hour = true
        )

    // ② Dialog 래퍼
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(Modifier.padding(24.dp)) {
                TimePicker(state = state)

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("취소") }
                    TextButton(onClick = {
                        onConfirm(LocalTime.of(state.hour, state.minute))
                    }) { Text("확인") }
                }
            }
        }
    }
}
