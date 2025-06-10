package com.example.studify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.studify.data.local.db.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
    constructor() : ViewModel() {
        private val _subjects = MutableStateFlow<List<TempSubject>>(emptyList())
        val subjects = _subjects

        fun upsert(s: TempSubject) {
            _subjects.update { list ->
                list.filterNot { it.id == s.id } + s
            }
        }
    }
