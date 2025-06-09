package com.example.studify.data.local.db

import androidx.room.TypeConverter
import java.time.LocalDateTime

object DateConverters {
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }
}
