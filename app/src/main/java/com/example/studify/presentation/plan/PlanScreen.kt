package com.example.studify.presentation.plan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studify.data.local.db.CategoryType
import com.example.studify.presentation.navigation.Screen
import com.example.studify.presentation.viewmodel.PlanCreateViewModel
import com.example.studify.presentation.viewmodel.TempSubject
import com.example.studify.presentation.viewmodel.UiEvent
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    navController: NavHostController,
    vm: PlanCreateViewModel = hiltViewModel()
) {
    var showSheet by remember { mutableStateOf<TempSubject?>(null) }
    val subjects by vm.subjects.collectAsState()
    val loading by vm.loading.collectAsState()
    val ctx = LocalContext.current
    val event by vm.event.collectAsState(initial = null)

    // 이벤트 처리
    event?.let {
        when (it) {
            UiEvent.Success ->
                navController.popBackStack(Screen.Home.route, false)

            is UiEvent.Error ->
                Toast.makeText(ctx, it.msg, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("계획 작성") },
                    windowInsets = WindowInsets(0)
                )
                HorizontalDivider()
            }
        }
    ) { inner ->
        Column(
            modifier =
                Modifier
                    .padding(inner)
                    .fillMaxSize()
        ) {
            val cardHeight = 96.dp

            AddSubjectCard(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(cardHeight)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    showSheet =
                        TempSubject(
                            id = System.currentTimeMillis(),
                            name = "",
                            credits = 3,
                            importance = 5,
                            category = CategoryType.Major,
                            examDate = LocalDate.now()
                        )
                }
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp)
                        .weight(1f)
            ) {
                items(subjects, key = { it.id }) { s ->
                    SubjectCard(
                        subject = s,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(cardHeight)
                    ) { showSheet = s }
                }
            }

            // 하단 버튼
            FilledTonalButton(
                onClick = vm::savePlan,
                enabled = subjects.isNotEmpty(),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .imePadding()
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, null)
                Spacer(Modifier.width(8.dp))
                Text("계획 생성하기")
            }
        }

        // 로딩 오버레이
        if (loading) LoaderOverlay()

        // 과목 편집/추가 시트
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
}

@Composable
private fun AddSubjectCard(
    modifier: Modifier,
    onClick: () -> Unit
) = Card(
    modifier = modifier.clickable(onClick = onClick),
    colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    elevation = CardDefaults.elevatedCardElevation(0.dp)
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Add, null)
        Spacer(Modifier.width(6.dp))
        Text("과목 추가", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun LoaderOverlay() =
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }

@Composable
private fun SubjectCard(
    modifier: Modifier,
    subject: TempSubject,
    onClick: () -> Unit
) = Card(
    onClick = onClick,
    elevation = CardDefaults.elevatedCardElevation(4.dp)
) {
    val categoryColor =
        if (subject.category == CategoryType.Major) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.secondary
        }
    Row(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(categoryColor)
        )
        Column(modifier = modifier.padding(16.dp)) {
            Text(subject.name, style = MaterialTheme.typography.titleMedium)
            Text("학점 ${subject.credits} · 중요도 ${subject.importance}")
            Text("${subject.category.label} / 시험 ${subject.examDate}")
        }
    }
}
