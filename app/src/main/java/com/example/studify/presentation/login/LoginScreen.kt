package com.example.studify.presentation.login

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.navigation.NavHostController
import com.example.studify.auth.GoogleAuthUiClient
import com.example.studify.presentation.navigation.Screen
import kotlinx.coroutines.launch

@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current as Activity
    val scope = rememberCoroutineScope()

    // Auth helper instance
    val authClient =
        remember { // Composition 당 한 번
            GoogleAuthUiClient(
                activity = context,
                credentialManager = CredentialManager.create(context)
            )
        }

    // 자동 One Tap
    var autoSignInDone by remember { mutableStateOf(false) }
    var userSignedIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Log.d("LoginScreen", ">> beginAutoSignIn() 시도")
        val resp = authClient.beginAutoSignIn()
        Log.d("LoginScreen", ">> beginAutoSignIn() 결과: $resp")
        resp?.let { resp ->
            val result = authClient.firebaseAuthWith(resp)
            Log.d("LoginScreen", ">> firebaseAuthWith 결과: $result")
            if (result.isSuccess) {
                // Firebase Auth에 로그인만 완료 -> 캘린더 권한 요청 화면으로 이동
                Log.d("LoginScreen", ">> 자동 로그인 성공 -> userSignedIn = true")
                userSignedIn = true
            } else {
                // 실패한 케이스 (ex. NoCredentialException) -> 버튼으로 재시도 가능
                Log.d("LoginScreen", ">> 자동 로그인 실패: ${result.exceptionOrNull()}")
                autoSignInDone = true
            }
        } ?: run {
            Log.d("LoginScreen", ">> beginAutoSignIn() == null, 자동 로그인 스킵 -> autoSignInDone = true")
            autoSignInDone = true
        }
    }

    LaunchedEffect(userSignedIn) {
        if (userSignedIn) {
            Log.d("LoginScreen", ">> userSignedIn = true, CalendarSync로 navigate 시도")
            navController.navigate(Screen.CalendarSync.route) {
                popUpTo(Screen.Login.route) {
                    inclusive = true
                }
            }
        }
    }

    // UI
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            // 아직 자동 로그인이 완료되지 않았을 때
            !autoSignInDone -> {
                Text("로그인 중...")
            }
            // 자동 로그인 실패 -> 수동 로그인 버튼 노출
            else -> {
                Button(onClick = {
                    scope.launch {
                        Log.d("LoginScreen", ">> 수동 로그인 버튼 클릭, beginUserSelect() 호출")
                        // 수동 계정 선택 강제 One-Tap
                        val resp = authClient.beginUserSelect()
                        Log.d("LoginScreen", "beginUserSelect() 결과: $resp")
                        if (resp == null) {
                            Log.d("LoginScreen", "사용자가 취소했거나 NoCredential, 토스트 표시")
                            // 사용자 취소 or NoCredentialException
                            Toast.makeText(context, "Google 계정을 선택해주세요.", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        val loginResult = authClient.firebaseAuthWith(resp)
                        Log.d("LoginScreen", "수동 로그인 결과: $loginResult")
                        loginResult.onSuccess {
                            Log.d("LoginScreen", "수동 로그인 성공 -> userSignedIn = true")
                            userSignedIn = true
                        }.onFailure {
                            Log.d("LoginScreen", "수동 로그인 실패: $it")
                            Toast.makeText(context, "로그인에 실패했습니다: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Google 계정으로 로그인")
                }
            }
        }
    }
}
