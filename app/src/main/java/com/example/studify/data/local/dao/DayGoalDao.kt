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
    SELECT date, subject, SUM(hours) as totalHours
    FROM day_goal
    WHERE date BETWEEN :startDate AND :endDate
    GROUP BY date, subject
    ORDER BY date
    """
    )
    fun findPeriodicalGoals(
        startDate: String,
        endDate: String
    ): Flow<List<DailyGoalSummary>>

    data class DailyGoalSummary(
        val date: String,
        val subject: String,
        val totalHours: Int
    )
}
