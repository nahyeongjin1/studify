package com.example.studify.auth

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
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
            credentialManager.getCredential(activity, request).also {
                Log.d("GoogleAuthUiClient", "자동 로그인 응답: $it")
            }
        } catch (e: GetCredentialException) {
            Log.d("GoogleAuthUiClient", "자동 로그인 예외: ${e.message}")
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
            val cred = resp.credential
            val googleCred =
                when (cred) {
                    is GoogleIdTokenCredential -> cred
                    is CustomCredential -> {
                        if (cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            GoogleIdTokenCredential.createFrom(cred.data)
                        } else {
                            throw IllegalArgumentException("Unsupported credential type: ${cred.type}")
                        }
                    }
                    else -> throw IllegalArgumentException("Unsupported credential class: ${cred::class.simpleName}")
                }
            val idToken = googleCred.idToken
            require(!idToken.isNullOrBlank()) { "idToken이 비어있음" }

            val firebaseCred = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(firebaseCred).await()
            Log.d("GoogleAuthUiClient", "Firebase 인증 성공")
            success(Unit)
        } catch (t: Throwable) {
            failure(t)
        }

    val currentUserUid: String?
        get() = firebaseAuth.currentUser?.uid

    private fun buildGoogleIdRequest(autoSelect: Boolean): GetCredentialRequest {
        val googleIdOpt =
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(autoSelect) // autoSelect=true → filter=false
                .setServerClientId(activity.getString(R.string.default_web_client_id))
                .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOpt)
            .build()
    }
}
