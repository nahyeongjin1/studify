package com.example.studify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studify.data.local.pref.OnboardingPrefs
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        onboardingPrefs: OnboardingPrefs
    ) : ViewModel() {
        sealed interface StartDest {
            data object Pending : StartDest

            data object Onboarding : StartDest

            data object Login : StartDest

            data object Home : StartDest
        }

        val startDestination: StateFlow<StartDest> =
            onboardingPrefs.seenFlow
                .map { seen ->
                    val loggedIn = FirebaseAuth.getInstance().currentUser != null
                    when {
                        !seen -> StartDest.Onboarding
                        loggedIn -> StartDest.Home
                        else -> StartDest.Login
                    }
                }
                .stateIn(viewModelScope, SharingStarted.Eagerly, StartDest.Pending)
    }
