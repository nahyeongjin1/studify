package com.example.studify.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studify.presentation.viewmodel.PlanCreateViewModel
import com.example.studify.presentation.viewmodel.TempSubject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studify.presentation.viewmodel.CalendarViewModel
import com.example.studify.presentation.viewmodel.CalendarViewModelFactory
import com.example.studify.presentation.viewmodel.HomeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: PlanCreateViewModel = hiltViewModel(),
    Viewmodel: HomeViewModel = hiltViewModel(),
    account: GoogleSignInAccount
) {
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val subjects by viewModel.subjects.collectAsState()
    val userName = Viewmodel.userName.value

    val context = LocalContext.current
    val factory = remember { CalendarViewModelFactory(context, account) }
    val calendarViewModel: CalendarViewModel = viewModel(factory = factory)
    val eventsByDate by calendarViewModel.eventsByDate.collectAsState()

    // 실제 오늘 날짜에 해당하는 Google Calendar에서 가져온 과목 제목 리스트
    val todayEventSubjects = eventsByDate[selectedDate.value]?.map { it.summary } ?: emptyList()

    LaunchedEffect(selectedDate.value) {
        calendarViewModel.loadEventsForWeek(LocalDate.now())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("${userName} 님, 안녕하세요? 👋", fontSize = 20.sp)
                        Text("Studied for 2h 15m today", fontSize = 14.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB2EBF2) // 단일 색상 사용
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* 일정 편집 기능 */ }) {
                Text("edit")
            }
        },

        ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            WeekCalendar(
                currentDate = selectedDate.value,
                eventsByDate = eventsByDate,
                onDateSelected = { date ->
                    selectedDate.value = date
                }
            )

            Text(
                text = selectedDate.value.format(DateTimeFormatter.ofPattern("EEEE, MMM. dd")),
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (todayEventSubjects.isEmpty()) {
                    item {
                        Text(
                            text = "오늘은 계획된 일정이 없습니다.",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray
                        )
                    }
                } else {
                    items(todayEventSubjects.size) { index ->
                        val subjectName = todayEventSubjects[index]
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Icon(Icons.Default.Book, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(subjectName, fontWeight = FontWeight.Bold)
                                    Text(
                                        "계획된 공부 세션",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
