package com.example.studify.presentation.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studify.presentation.navigation.Screen
import com.example.studify.presentation.viewmodel.PlanCreateViewModel
import com.example.studify.presentation.viewmodel.TempSubject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanCreateScreen(
    navController: NavHostController,
    vm: PlanCreateViewModel = hiltViewModel()
) {
    var showSheet by remember { mutableStateOf<TempSubject?>(null) }
    val done by vm.done.collectAsState(null)

    LaunchedEffect(done) {
        if (done != null) {
            navController.popBackStack(Screen.Plan.route, false)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("계획 작성") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = null }) {
                Icon(Icons.Default.Add, contentDescription = "과목 추가")
            }
        }
    ) { inner ->
        val subjects by vm.subjects.collectAsState()
        LazyColumn(
            contentPadding = inner,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(subjects) { s ->
                SubjectCard(subject = s, onClick = { showSheet = s })
            }
        }

        Button(
            onClick = {
                vm.savePlan()
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
        ) { Text("계획 생성하기") }
    }

    showSheet?.let { editing ->
        AddSubjectSheet(
            initial = editing,
            onDismiss = { showSheet = null },
            onSave = {
                vm.upsert(it)
                showSheet = null
            }
        )
    }
}

@Composable
private fun SubjectCard(
    subject: TempSubject,
    onClick: () -> Unit
) = Card(onClick) {
    Column(Modifier.padding(16.dp)) {
        Text(subject.name, style = MaterialTheme.typography.titleMedium)
        Text("학점 ${subject.credits} · 중요도 ${subject.importance}")
        Text("${subject.category.label} / 시험 ${subject.examDate}")
    }
}
