package com.example.studify.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.domain.repository.PlanRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: PlanRepository
) : ViewModel() {

    // MutableStateFlow 내부 상태
    private val _scheduleByDate = MutableStateFlow<Map<LocalDate, List<String>>>(emptyMap())
    val scheduleByDate = _scheduleByDate

    var userName = mutableStateOf("사용자")
        private set

    init {
        loadUserName()
        loadGeneratedPlan()  // generatePlan() 대신 로드 함수 호출
    }

    private fun loadUserName() {
        val name = FirebaseAuth.getInstance().currentUser?.displayName
        if (!name.isNullOrBlank()) {
            userName.value = name
        }
    }

    fun loadGeneratedPlan() {
        viewModelScope.launch {
            val plan = repo.getGeneratedPlan() // 날짜별 과목 리스트
            _scheduleByDate.value = plan
        }
    }

    fun loadStudySessions() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.getGeneratedPlan() // 날짜별 subject 리스트 반환
            }
            scheduleByDate.value = result
        }
    }


}
