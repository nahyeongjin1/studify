package com.example.studify.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

import androidx.room.Query
import com.example.studify.data.local.entity.DayDoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DayDoneEntity)

    //
    @Query("SELECT * FROM day_done WHERE subject = :subject AND date = :date")
    fun get(
        subject: String,
        date: String
    ): Flow<List<DayDoneEntity>>

    @Query("SELECT * FROM day_done WHERE date = :date")
    fun getAll(date: String): Flow<List<DayDoneEntity>>

    @Query(
        """
    SELECT date, subject, SUM(seconds) as totalSeconds
    FROM day_done
    WHERE date BETWEEN :startDate AND :endDate
    GROUP BY date, subject
    ORDER BY date
    """
    )
    fun findPeriodicalDone(
        startDate: String,
        endDate: String
    ): Flow<List<DailyDoneSummary>>

    data class DailyDoneSummary(
        val date: String,
        val subject: String,
        val totalSeconds: Int
    )

    @Query(
        """
    SELECT date, SUM(seconds) as totalSeconds
    FROM day_done
    GROUP BY date
    ORDER BY totalSeconds DESC
    LIMIT 1
    """
    )
    fun getMostStudiedDay(): Flow<DayMaxDone?>

    data class DayMaxDone(
        val date: String,
        val totalSeconds: Int
    )

    @Query(
        """
    SELECT DISTINCT date
    FROM day_done
    WHERE seconds > 0
    ORDER BY date DESC
    """
    )
    suspend fun getStudyDatesDesc(): List<String>
}
