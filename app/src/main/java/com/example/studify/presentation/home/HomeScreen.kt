package com.example.studify.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studify.domain.model.StudySession
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel(),
    onEditClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val ui by vm.uiState.collectAsState()
    var editing by remember { mutableStateOf<StudySession?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("홈") })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "형진 님, 안녕하세요 👋",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        // TODO: 오늘 공부한 시간 계산해서 표시
                        Text(
                            text = "Studied for ${ui.studiedText} today",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            }

            // 캘린더
            WeeklyCalendar(
                selectedDate = ui.selectedDate,
                onDateSelected = vm::selectDate,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Date header
            val headerFmt = DateTimeFormatter.ofPattern("EEEE, LLL. d", Locale.getDefault())
            Text(
                text = ui.selectedDate.format(headerFmt),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            // 세션 리스트
            if (ui.sessions.isEmpty()) {
                Text(
                    text = "오늘은 계획된 일정이 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                ) {
                    items(ui.sessions, key = { it.id }) { s ->
                        SessionCard(
                            s.toUi(),
                            onClick = { editing = s },
                            onDelete = { vm.delete(s.id) }
                        )
                    }
                }
            }
        }
        editing?.let { sel ->
            EditSessionSheet(
                initial = sel,
                onDismiss = { editing = null },
                onSave = {
                    vm.update(it)
                    editing = null
                }
            )
        }
    }
}
