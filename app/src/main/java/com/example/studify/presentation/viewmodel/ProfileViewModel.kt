package com.example.studify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    private val _userName = MutableStateFlow("난딩치맥 님")
    val userName: StateFlow<String> = _userName

    private val _email = MutableStateFlow("account@gmail.com")
    val email: StateFlow<String> = _email

    private val _isNotificationOn = MutableStateFlow(true)
    val isNotificationOn: StateFlow<Boolean> = _isNotificationOn

    fun setNotification(enabled: Boolean) {
        _isNotificationOn.value = enabled
    }

    fun logout() {
        // FirebaseAuth.getInstance().signOut() 등 추가 가능
    }
}
