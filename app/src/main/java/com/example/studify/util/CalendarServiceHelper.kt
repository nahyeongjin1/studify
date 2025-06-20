package com.example.studify.util

import android.R.attr.timeZone
import android.content.Context
import android.util.Log
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
import java.util.TimeZone

object CalendarServiceHelper {
    @JvmStatic
    fun getCalendarService(
        context: Context,
        account: GoogleSignInAccount
    ): Calendar {
        val credential =
            GoogleAccountCredential
                .usingOAuth2(context, listOf(CalendarScopes.CALENDAR))
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
                    start = EventDateTime().apply {
                        dateTime = DateTime(startTime.toInstant().toEpochMilli())
                        timeZone = "Asia/Seoul"
                    }
                    end = EventDateTime().apply {
                        dateTime = DateTime(endTime.toInstant().toEpochMilli())
                        timeZone = "Asia/Seoul"
                    }
                }
            val inserted = service.events().insert("primary", event).execute()
            Log.d("CalendarService", "이벤트 생성 성공: ${inserted.id}")
            inserted.id
        }.getOrElse {
            Log.e("CalendarService", "이벤트 생성 중 오류", it)
            null
        }

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


    fun getEventsInRange(
        context: Context,
        account: GoogleSignInAccount,
        start: OffsetDateTime,
        end: OffsetDateTime
    ): List<Event> {
        val service = getCalendarService(context, account)

        return runCatching {
            service.events()
                .list("primary")
                .setTimeMin(DateTime(start.toInstant().toEpochMilli()))
                .setTimeMax(DateTime(end.toInstant().toEpochMilli()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()
                .items
        }.getOrElse {
            it.printStackTrace()
            emptyList()
        }
    }
}


