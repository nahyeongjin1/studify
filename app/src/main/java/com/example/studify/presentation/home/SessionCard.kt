package com.example.studify.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studify.domain.model.StudySession
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SessionCard(
    session: StudyUi,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState =
        rememberDismissState(
            confirmStateChange = {
                if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                    onDelete()
                    true
                } else {
                    false
                }
            }
        )
    SwipeToDismiss(
        state = dismissState,
        background = {
            val clr = MaterialTheme.colorScheme.errorContainer
            val icon = Icons.Default.Delete
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(clr)
                        .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = null) }
        },
        dismissContent = {
            Card(
                onClick = onClick,
                modifier = modifier.padding(vertical = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(session.subject, fontWeight = FontWeight.Bold)
                    Text("${session.start}  ~  ${session.end}", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        modifier = modifier
    )
}

data class StudyUi(
    val id: Int,
    val subject: String,
    val start: String,
    val end: String
)

private val isoTime = DateTimeFormatter.ISO_TIME

fun StudySession.toUi() =
    StudyUi(
        id,
        subject,
        LocalTime.parse(startTime, isoTime).toString().substring(0, 5),
        LocalTime.parse(endTime, isoTime).toString().substring(0, 5)
    )
