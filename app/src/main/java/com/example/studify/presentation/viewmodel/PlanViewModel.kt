package com.example.studify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.data.local.dao.PlanWithSubjects
import com.example.studify.domain.repository.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlanViewModel
    @Inject
    constructor(
        repo: PlanRepository
    ) : ViewModel() {
        val plans: StateFlow<List<PlanWithSubjects>> =
            repo.observePlansWithSubjects()
                .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
