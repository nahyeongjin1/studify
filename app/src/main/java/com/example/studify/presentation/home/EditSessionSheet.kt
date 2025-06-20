package com.example.studify.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studify.domain.model.StudySession
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSessionSheet(
    initial: StudySession,
    onDismiss: () -> Unit,
    onSave: (StudySession) -> Unit
) {
    var subject by remember { mutableStateOf(initial.subject) }
    var start by remember { mutableStateOf(LocalTime.parse(initial.startTime)) }
    var end by remember { mutableStateOf(LocalTime.parse(initial.endTime)) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(subject, { subject = it }, label = { Text("과목") })
            // 아주 단순한 HH:MM 입력 필드 – 실제 앱이면 TimePicker 추천
            OutlinedTextField(start.toString(), {
                runCatching { start = LocalTime.parse(it) }
            }, label = { Text("시작") })
            OutlinedTextField(end.toString(), {
                runCatching { end = LocalTime.parse(it) }
            }, label = { Text("종료") })

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
