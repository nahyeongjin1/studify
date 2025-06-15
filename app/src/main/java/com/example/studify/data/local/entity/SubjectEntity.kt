package com.example.studify.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.studify.data.local.db.CategoryType

@Entity(
    tableName = "subjects",
    indices = [Index("planId")],
    foreignKeys = [
        ForeignKey(
            entity = StudyPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubjectEntity(
    val planId: Long,
    val name: String,
    val credits: Int,
    val importance: Int = 5,
    val category: CategoryType = CategoryType.Major,
    val examDate: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
)
