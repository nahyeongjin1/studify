package com.example.studify.presentation.plan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studify.data.local.db.CategoryType
import com.example.studify.presentation.viewmodel.TempSubject
import java.time.LocalDate
import java.util.UUID
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectSheet(
    initial: TempSubject? = null,
    onDismiss: () -> Unit,
    onSave: (TempSubject) -> Unit
) = ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var category by remember { mutableStateOf(initial?.category ?: CategoryType.Major) }
    var credits by remember { mutableIntStateOf(initial?.credits ?: 3) }
    var importance by remember { mutableFloatStateOf(initial?.importance?.toFloat() ?: 5f) }
    var examDate by remember { mutableStateOf(initial?.examDate ?: LocalDate.now()) }

    var showDatePicker by remember { mutableStateOf(false) }
    val dateState =
        rememberDatePickerState(
            initialSelectedDateMillis = examDate.toEpochDay() * 24 * 60 * 60 * 1000
        )

    var creditsExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("과목명") }
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
        ) {
            OutlinedTextField(
                value = examDate.toString(),
                onValueChange = {},
                enabled = false,
                label = { Text("시험 날짜") },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        disabledTextColor = LocalContentColor.current,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            )
        }

        SingleChoiceSegmentedButtonRow {
            CategoryType.values().forEach {
                SegmentedButton(
                    selected = category == it,
                    onClick = { category = it },
                    label = { Text(it.label) },
                    shape = SegmentedButtonDefaults.baseShape
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("학점")
            ExposedDropdownMenuBox(
                expanded = creditsExpanded,
                onExpandedChange = { creditsExpanded = !creditsExpanded }
            ) {
                OutlinedTextField(
                    value = credits.toString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = creditsExpanded) },
                    modifier =
                        Modifier
                            .menuAnchor()
                            .width(80.dp)
                )
                ExposedDropdownMenu(
                    expanded = creditsExpanded,
                    onDismissRequest = { creditsExpanded = false }
                ) {
                    (1..5).forEach { num ->
                        DropdownMenuItem(
                            text = { Text("$num") },
                            onClick = {
                                credits = num
                                creditsExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Text("중요도 ${importance.toInt()}")
        Slider(
            value = importance,
            onValueChange = { importance = it },
            valueRange = 1f..10f,
            steps = 8
        )

        Button(
            onClick = {
                onSave(
                    TempSubject(
                        id = initial?.id ?: UUID.randomUUID().mostSignificantBits,
                        name = name,
                        credits = credits,
                        importance = importance.roundToInt(),
                        category = category,
                        examDate = examDate
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("저장") }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        dateState.selectedDateMillis?.let {
                            examDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }) { Text("확인") }
                }
            ) {
                DatePicker(state = dateState)
            }
        }
    }
}
