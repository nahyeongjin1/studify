package com.example.studify.data.local.db

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

object DateConverters {
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromCategory(value: CategoryType?): String? = value?.name

    @TypeConverter
    fun toCategory(value: String?): CategoryType? = value?.let { CategoryType.valueOf(it) }
}
