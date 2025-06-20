package com.example.studify.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun toOffset(
    date: String,
    time: String
): OffsetDateTime =
    OffsetDateTime.of(
        LocalDate.parse(date),
        LocalTime.parse(time),
        ZoneOffset.ofHours(9)
    )
