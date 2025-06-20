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
}
