package com.example.studify.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studify.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity)

    @Update
    suspend fun updateSession(session: StudySessionEntity)

    @Delete
    suspend fun deleteSession(session: StudySessionEntity)

    @Query("SELECT * FROM study_sessions ORDER BY date, startTime")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE date = :date ORDER BY startTime")
    fun getSessionsByDate(date: String): Flow<List<StudySessionEntity>>

    @Query("DELETE FROM study_sessions")
    suspend fun clearAll()

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Int)
}
