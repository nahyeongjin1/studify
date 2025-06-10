package com.example.studify.di

import android.app.Application
import androidx.room.Room
import com.example.studify.data.local.dao.PlanDao
import com.example.studify.data.local.dao.StudySessionDao
import com.example.studify.data.local.db.StudifyDatabase
import com.example.studify.data.repository.FakePlanRepository
import com.example.studify.data.repository.StudyRepositoryImpl
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.StudyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    // @Binds
    @Binds
    @Singleton
    abstract fun bindPlanRepo(impl: FakePlanRepository): PlanRepository

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
            ).build()

        @Provides
        fun provideStudySessionDao(db: StudifyDatabase): StudySessionDao = db.studySessionDao()

        @Provides
        fun providePlanDao(db: StudifyDatabase): PlanDao = db.planDao()
    }
}
