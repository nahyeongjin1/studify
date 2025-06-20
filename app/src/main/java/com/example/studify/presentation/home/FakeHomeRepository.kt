package com.example.studify.presentation.home

import com.example.studify.domain.model.StudySession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MVP용 임시 Repo
 */
@Singleton
class FakeHomeRepository
    @Inject
    constructor() {
        private val store = MutableStateFlow(emptyList<StudySession>())

        fun observeSessions(date: LocalDate): Flow<List<StudySession>> {
            // date 바뀔 때마다 store의 리스트에서 필터
            return store.asStateFlow().let { flow ->
                combine(flow) { listOf(it.first().filter { s -> s.date == date.toString() }) }
                    .map { it.first() }
            }
        }

        // 초기 더미 3개
        suspend fun seedIfEmpty(today: LocalDate) {
            if (store.value.isNotEmpty()) return
            delay(300)
            val dummy =
                listOf(
                    StudySession(1, "자료구조", today.toString(), "14:00", "16:00", today.plusDays(20).toString()),
                    StudySession(2, "컴네", today.toString(), "16:30", "18:00", today.plusDays(30).toString()),
                    StudySession(3, "OS", today.toString(), "20:00", "22:00", today.plusDays(10).toString())
                )
            store.value = dummy
        }
    }
