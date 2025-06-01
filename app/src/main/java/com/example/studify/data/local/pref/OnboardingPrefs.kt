package com.example.studify.data.local.pref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class OnboardingPrefs @Inject constructor(
    @ApplicationContext private val ctx: Context
) {

    private object Keys {
        val SEEN = booleanPreferencesKey("onboarding_seen")
    }

    val seenFlow: Flow<Boolean> = ctx.dataStore.data.map { prefs ->
        prefs[Keys.SEEN] == true
    }

    suspend fun setSeen() {
        ctx.dataStore.edit { prefs ->
            prefs[Keys.SEEN] = true
        }
    }
}
