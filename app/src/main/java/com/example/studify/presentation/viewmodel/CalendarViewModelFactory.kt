package com.example.studify.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class CalendarViewModelFactory(
    private val context: Context,
    private val account: GoogleSignInAccount
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(context, account) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
