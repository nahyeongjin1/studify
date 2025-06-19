package com.example.studify.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studify.data.local.entity.DayGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DayGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DayGoalEntity)

    @Query("SELECT * FROM day_goal WHERE subject= :subject and date = :date")
    suspend fun findDayGoal(
        subject: String,
        date: String
    ): DayGoalEntity

    @Query("SELECT * FROM day_goal WHERE date = :date ORDER BY subject")
    fun findDayGoal(date: String): Flow<List<DayGoalEntity>>

    @Query(
        """
    SELECT subject, SUM(hours) as totalHours
    FROM day_goal
    WHERE date BETWEEN :startDate AND :endDate
    GROUP BY subject
    ORDER BY subject
"""
    )
    fun findPeriodicalGoalsGroupedBySubject(
        startDate: String,
        endDate: String
    ): Flow<List<SubjectGoalSummary>>

    data class SubjectGoalSummary(
        val subject: String,
        val totalHours: Int
    )
}
