package com.example.studify.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studify.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(subject: SubjectEntity): Long

    @Query("SELECT * FROM subjects WHERE planId = :planId ORDER BY examDate")
    fun observeByPlan(planId: Long): Flow<List<SubjectEntity>>

    @Query("SELECT name FROM subjects")
    suspend fun getAllSubjectNames(): List<String>


}
