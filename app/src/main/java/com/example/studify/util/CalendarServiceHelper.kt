package com.example.studify.util

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.time.OffsetDateTime

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

    fun createEvent(
        context: Context,
        account: GoogleSignInAccount,
        title: String,
        startTime: OffsetDateTime,
        endTime: OffsetDateTime,
    ): String? =
        runCatching {
            val service = getCalendarService(context, account)
            val event =
                Event().apply {
                    summary = "[Studify] $title"
                    start = EventDateTime().setDateTime(DateTime(startTime.toInstant().toEpochMilli()))
                    end = EventDateTime().setDateTime(DateTime(endTime.toInstant().toEpochMilli()))
                }
            service.events().insert("primary", event).execute().id
        }.getOrNull()

    fun purgeStudyEvents(
        context: Context,
        account: GoogleSignInAccount,
        from: OffsetDateTime,
    ) {
        val service = getCalendarService(context, account)
        val events =
            service.events().list("primary")
                .setTimeMin(DateTime(from.toInstant().toEpochMilli()))
                .setQ("[Studify]")
                .execute()
                .items
        events.forEach { service.events().delete("primary", it.id).execute() }
    }
}
