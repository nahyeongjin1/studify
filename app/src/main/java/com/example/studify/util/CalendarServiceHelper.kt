package com.example.studify.util

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes

object CalendarServiceHelper {
    @JvmStatic
    fun getCalendarService(
        context: Context,
        account: GoogleSignInAccount
    ): Calendar {
        val credential =
            GoogleAccountCredential
                .usingOAuth2(context, listOf(CalendarScopes.CALENDAR_EVENTS))
                .apply {
                    selectedAccount = account.account
                }

        return Calendar.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName("Studify")
            .build()
    }
}
