package com.example.studify.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RememberAuthSignedIn(): Boolean {
    val auth = remember { FirebaseAuth.getInstance() }

    val signedIn = remember { mutableStateOf(auth.currentUser != null) }

    DisposableEffect(Unit) {
        val listener =
            FirebaseAuth.AuthStateListener { fb ->
                signedIn.value = fb.currentUser != null
            }
        auth.addAuthStateListener(listener)

        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }
    return signedIn.value
}
