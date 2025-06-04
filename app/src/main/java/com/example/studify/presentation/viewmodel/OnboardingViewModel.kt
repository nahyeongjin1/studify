// app/src/main/java/com/example/studify/presentation/viewmodel/OnboardingViewModel.kt
package com.example.studify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.studify.data.local.pref.OnboardingPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val prefs: OnboardingPrefs
    ) : ViewModel() {
        fun setSeen() {
            // ViewModelScope 안 쓰는 이유: DataStore I/O 매우 짧음
            CoroutineScope(Dispatchers.IO).launch { prefs.setSeen() }
        }
    }
