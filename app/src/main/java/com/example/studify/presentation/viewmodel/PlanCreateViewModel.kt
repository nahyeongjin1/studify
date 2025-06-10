package com.example.studify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.data.local.db.CategoryType
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.SubjectInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TempSubject(
    val id: Long,
    val name: String,
    val credits: Int,
    val importance: Int,
    val category: CategoryType,
    val examDate: LocalDate
)

@HiltViewModel
class PlanCreateViewModel
    @Inject
    constructor(
        private val repo: PlanRepository
    ) : ViewModel() {
        private val _subjects = MutableStateFlow<List<TempSubject>>(emptyList())
        val subjects = _subjects

        private val _done = MutableSharedFlow<Unit>()
        val done = _done

        fun upsert(s: TempSubject) {
            _subjects.update { list ->
                list.filterNot { it.id == s.id } + s
            }
        }

        fun savePlan() =
            viewModelScope.launch {
                val inputs =
                    _subjects.value.map { s ->
                        SubjectInput(
                            subject = s.name,
                            credits = s.credits,
                            importance = s.importance,
                            category = s.category,
                            examDate = s.examDate,
                        )
                    }
                repo.createPlanLocal(inputs)
                _done.emit(Unit)
            }
    }
