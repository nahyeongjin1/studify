package com.example.studify.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studify.data.local.dao.StudySessionDao
import com.example.studify.data.local.entity.StudySessionEntity

@Database(
    entities = [StudySessionEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class StudifyDatabase : RoomDatabase() {
    abstract fun studySessionDao(): StudySessionDao
}
