package com.example.studify.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.studify.data.local.entity.StudyPlanEntity
import com.example.studify.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

data class PlanWithSubjects(
    @Embedded val plan: StudyPlanEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "planId"
    )
    val subjects: List<SubjectEntity>
)

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(plan: StudyPlanEntity): Long

    @Query("SELECT * FROM study_plans ORDER BY createdAt DESC")
    fun observePlans(): Flow<List<StudyPlanEntity>>

    @Transaction
    @Query("SELECT * FROM study_plans ORDER BY createdAt DESC")
    fun observePlansWithSubjects(): Flow<List<PlanWithSubjects>>
}
