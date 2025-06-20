package com.example.studify.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studify.domain.model.StudySession
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@OptIn(ExperimentalMaterialApi::class)
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
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        },
        dismissContent = {
            val stripe = 8.dp
            val stripeColor = subjectColor(session.subject)
            Card(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 72.dp)
                        .clickable(onClick = onClick)
                        .drawBehind {
                            drawRoundRect(
                                color = stripeColor,
                                size = Size(stripe.toPx(), size.height),
                                cornerRadius = CornerRadius(12.dp.toPx())
                            )
                        }
                        .padding(start = stripe + 8.dp),
                elevation = CardDefaults.elevatedCardElevation(2.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(session.subject, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${session.start}  ~  ${session.end}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    )
}

// subject → 파스텔 컬러
@Composable
private fun subjectColor(name: String): Color {
    val palette =
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = .25f),
            MaterialTheme.colorScheme.secondary.copy(alpha = .25f),
            MaterialTheme.colorScheme.tertiary.copy(alpha = .25f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .25f),
        )
    return palette[abs(name.hashCode()) % palette.size]
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
