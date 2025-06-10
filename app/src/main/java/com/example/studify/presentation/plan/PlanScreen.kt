package com.example.studify.presentation.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studify.data.local.db.CategoryType
import com.example.studify.presentation.viewmodel.PlanViewModel

@Composable
fun PlanScreen(
    navController: NavHostController,
    vm: PlanViewModel = hiltViewModel()
) {
    val plans by vm.plans.collectAsState()

    Scaffold { inner ->
        LazyColumn(
            contentPadding = inner,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(plans) { pw ->
                val majorCount = pw.subjects.count { it.category == CategoryType.Major }
                val generalCount = pw.subjects.size - majorCount
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text("Plan #${pw.plan.id}", style = MaterialTheme.typography.titleMedium)
                        Text(pw.plan.createdAt.toLocalDate().toString())
                        Text(
                            "Subjects: ${pw.subjects.size} (Major: $majorCount, General: $generalCount)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
