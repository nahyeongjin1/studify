package com.example.studify.data.repository

import com.example.studify.data.local.dao.PlanDao
import com.example.studify.data.local.entity.StudyPlanEntity
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.SubjectInput
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePlanRepository
    @Inject
    constructor(
        private val dao: PlanDao
    ) : PlanRepository {
        override fun observePlans(): Flow<List<StudyPlanEntity>> = dao.observePlans()

        override suspend fun createPlanWithLLM(inputs: List<SubjectInput>) {
            // 간단한 더미 계획 2개 저장
            dao.upsert(StudyPlanEntity())
            dao.upsert(StudyPlanEntity())
        }

        override suspend fun deletePlan(id: Long) {
            // TODO
        }
    }
