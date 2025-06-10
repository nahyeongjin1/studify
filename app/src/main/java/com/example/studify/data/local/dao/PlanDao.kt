package com.example.studify.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studify.data.local.entity.StudyPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(plan: StudyPlanEntity): Long

    @Query("SELECT * FROM study_plans ORDER BY createdAt DESC")
    fun observePlans(): Flow<List<StudyPlanEntity>>
}
