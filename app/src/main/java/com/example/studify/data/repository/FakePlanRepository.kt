package com.example.studify.data.repository

import com.example.studify.data.local.dao.PlanDao
import com.example.studify.data.local.dao.PlanWithSubjects
import com.example.studify.data.local.dao.SubjectDao
import com.example.studify.data.local.entity.StudyPlanEntity
import com.example.studify.data.local.entity.SubjectEntity
import com.example.studify.domain.repository.PlanRepository
import com.example.studify.domain.repository.SubjectInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePlanRepository
    @Inject
    constructor(
        private val planDao: PlanDao,
        private val subjectDao: SubjectDao
    ) : PlanRepository {
        init {
            CoroutineScope(Dispatchers.IO).launch { // or CoroutineScope(Dispatchers.IO)
                planDao.upsert(StudyPlanEntity())
                planDao.upsert(StudyPlanEntity())
            }
        }

        override fun observePlans(): Flow<List<StudyPlanEntity>> = planDao.observePlans()

        override fun observePlansWithSubjects(): Flow<List<PlanWithSubjects>> = planDao.observePlansWithSubjects()

        override suspend fun createPlanLocal(subjects: List<SubjectInput>) {
            val planId = planDao.upsert(StudyPlanEntity())
            subjects.forEach { s ->
                subjectDao.upsert(
                    SubjectEntity(
                        planId = planId,
                        name = s.subject,
                        credits = s.credits,
                        importance = s.importance,
                        category = s.category,
                        examDate = s.examDate.toString()
                    )
                )
            }
        }

        override suspend fun createPlanWithLLM(inputs: List<SubjectInput>) {
            // 간단한 더미 계획 2개 저장
            planDao.upsert(StudyPlanEntity())
            planDao.upsert(StudyPlanEntity())
        }

        override suspend fun deletePlan(id: Long) {
            // TODO
        }
    }
