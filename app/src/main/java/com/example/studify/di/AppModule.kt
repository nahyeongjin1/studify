package com.example.studify.di

import android.app.Application
import androidx.room.Room
import com.example.studify.data.local.dao.DayDoneDao
import com.example.studify.data.local.dao.DayGoalDao
import com.example.studify.data.local.dao.PlanDao
import com.example.studify.data.local.dao.StudySessionDao
import com.example.studify.data.local.dao.SubjectDao
import com.example.studify.data.local.db.StudifyDatabase
import com.example.studify.data.repository.PlanRepositoryImpl
import com.example.studify.data.repository.StudyRepositoryImpl
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.StudyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    // @Binds
    @Binds
    @Singleton
    abstract fun bindPlanRepo(impl: PlanRepositoryImpl): PlanRepository

    @Binds
    @Singleton
    abstract fun bindStudyRepo(impl: StudyRepositoryImpl): StudyRepository

    // @Provides
    companion object {
        @Provides
        @Singleton
        fun provideDatabase(app: Application): StudifyDatabase =
            Room.databaseBuilder(
                app,
                StudifyDatabase::class.java,
                "studify_database",
            )
                .fallbackToDestructiveMigration(true) // TODO 나중에 Migration 정책 세워야 함. 우선은 그냥 데이터 삭제되도록.
                .build()

        @Provides
        fun provideDatabaseDispatcher(): CoroutineDispatcher = Dispatchers.IO

        @Provides
        fun provideStudySessionDao(db: StudifyDatabase): StudySessionDao = db.studySessionDao()

        @Provides
        fun providePlanDao(db: StudifyDatabase): PlanDao = db.planDao()

        @Provides
        fun provideSubjectDao(db: StudifyDatabase): SubjectDao = db.subjectDao()

        @Provides
        fun provideDayGoalDao(db: StudifyDatabase): DayGoalDao = db.dayGoalDao()

        @Provides
        fun provideDayDoneDao(db: StudifyDatabase): DayDoneDao = db.dayDoneDao()
    }
}
