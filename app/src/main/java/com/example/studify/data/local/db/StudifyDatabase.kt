package com.example.studify.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studify.data.local.dao.DayGoalDao
import com.example.studify.data.local.dao.PlanDao
import com.example.studify.data.local.dao.StudySessionDao
import com.example.studify.data.local.dao.SubjectDao
import com.example.studify.data.local.entity.StudyPlanEntity
import com.example.studify.data.local.entity.StudySessionEntity
import com.example.studify.data.local.entity.SubjectEntity

@Database(
    entities = [StudySessionEntity::class, StudyPlanEntity::class, SubjectEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(DateConverters::class)
abstract class StudifyDatabase : RoomDatabase() {
    abstract fun studySessionDao(): StudySessionDao

    abstract fun planDao(): PlanDao

    abstract fun subjectDao(): SubjectDao

    abstract fun dayGoalDao(): DayGoalDao
}
