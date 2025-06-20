package com.example.studify.presentation.timer

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore 인스턴스 확장 함수로 정의
val Context.dataStore by preferencesDataStore(name = "study_time_prefs")

class StudyTimeDataStore(private val context: Context) {

    // 과목별 공부 시간 저장 (초 단위)
    suspend fun setStudyTime(subject: String, seconds: Int) {
        val key = intPreferencesKey(subject)
        context.dataStore.edit { prefs ->
            prefs[key] = seconds
        }
    }

    // 특정 과목의 공부 시간 불러오기
    fun getStudyTime(subject: String): Flow<Int> {
        val key = intPreferencesKey(subject)
        return context.dataStore.data.map { prefs ->
            prefs[key] ?: 0
        }
    }

    // 여러 과목의 공부 시간을 한 번에 불러오기
    suspend fun getAllStudyTimes(subjects: List<String>): Flow<Map<String, Int>> {
        return context.dataStore.data.map { prefs ->
            subjects.associateWith { subject ->
                prefs[intPreferencesKey(subject)] ?: 0
            }
        }
    }

    // 공부 시간 초기화
    suspend fun clearStudyTimes(subjects: List<String>) {
        context.dataStore.edit { prefs ->
            subjects.forEach { subject ->
                prefs.remove(intPreferencesKey(subject))
            }
        }
    }
}
