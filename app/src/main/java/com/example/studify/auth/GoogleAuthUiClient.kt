package com.example.studify.auth

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.example.studify.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class GoogleAuthUiClient(
    private val activity: Activity,
    private val credentialManager: CredentialManager = CredentialManager.create(activity),
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    // ---------- One Tap: 자동 ----------
    suspend fun beginAutoSignIn(): GetCredentialResponse? =
        try {
            val request = buildGoogleIdRequest(autoSelect = true)
            credentialManager.getCredential(activity, request)
        } catch (e: GetCredentialException) {
            null
        }

    // ---------- 계정 선택 강제 ----------
    suspend fun beginUserSelect(): GetCredentialResponse? =
        try {
            val googleIdOpt =
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false) // 모든 Google 계정 표시
                    .setServerClientId(activity.getString(R.string.default_web_client_id))
                    .build()

            val request =
                GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOpt)
                    .build()

            credentialManager.getCredential(activity, request)
        } catch (e: GetCredentialException) {
            null
        }

    // ---------- Firebase Auth 연동 ----------
    suspend fun firebaseAuthWith(resp: GetCredentialResponse): Result<Unit> =
        try {
            val googleCred = resp.credential as GoogleIdTokenCredential
            val idToken = googleCred.idToken
            val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(firebaseCred).await()
            success(Unit)
        } catch (t: Throwable) {
            failure(t)
        }

    val currentUserUid: String?
        get() = firebaseAuth.currentUser?.uid

    private fun buildGoogleIdRequest(autoSelect: Boolean): GetCredentialRequest {
        val googleIdOpt =
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(!autoSelect) // autoSelect=true → filter=false
                .setServerClientId(activity.getString(R.string.default_web_client_id))
                .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOpt)
            .build()
    }
}
