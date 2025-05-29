package com.example.studify.di

import android.app.Application
import androidx.room.Room
import com.example.studify.data.local.dao.StudySessionDao
import com.example.studify.data.local.db.StudifyDatabase
import com.example.studify.data.repository.StudyRepositoryImpl
import com.example.studify.domain.repository.StudyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): StudifyDatabase {
        return Room.databaseBuilder(
            app,
            StudifyDatabase::class.java,
            "studify_database",
        ).build()
    }

    @Provides
    fun provideStudySessionDao(db: StudifyDatabase): StudySessionDao {
        return db.studySessionDao()
    }

    @Provides
    @Singleton
    fun provideStudyRepository(dao: StudySessionDao): StudyRepository {
        return StudyRepositoryImpl(dao)
    }
}
