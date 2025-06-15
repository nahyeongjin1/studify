package com.example.studify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.data.local.db.CategoryType
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.SubjectInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

sealed interface UiEvent {
    object Success : UiEvent

    data class Error(val msg: String) : UiEvent
}

@HiltViewModel
class PlanCreateViewModel
    @Inject
    constructor(
        private val repo: PlanRepository
    ) : ViewModel() {
        private val _subjects = MutableStateFlow<List<TempSubject>>(emptyList())
        val subjects = _subjects

        private val _event = MutableSharedFlow<UiEvent>()
        val event = _event

        private val _loading = MutableStateFlow(false)
        val loading = _loading

        fun upsert(s: TempSubject) {
            _subjects.update { list ->
                list.filterNot { it.id == s.id } + s
            }
        }

        fun savePlan() =
            viewModelScope.launch {
                val inputs = _subjects.value.map { it.toDomain() }
                _loading.value = true

                try {
                    withContext(Dispatchers.IO) {
                        repo.createPlanWithLLM(inputs)
                    }
                    _event.emit(UiEvent.Success)
                } catch (t: Throwable) {
                    _event.emit(UiEvent.Error(t.message ?: "오류 발생"))
                } finally {
                    _loading.value = false
                }
            }

        private fun TempSubject.toDomain() =
            SubjectInput(
                subject = name,
                credits,
                importance,
                category,
                examDate
            )
    }
