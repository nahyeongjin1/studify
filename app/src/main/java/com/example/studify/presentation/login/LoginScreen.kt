package com.example.studify.presentation.login

import android.annotation.SuppressLint
import android.app.Activity
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
        authClient.beginAutoSignIn()?.let { resp ->
            val result = authClient.firebaseAuthWith(resp)
            if (result.isSuccess) {
                // Firebase Auth에 로그인만 완료 -> 캘린더 권한 요청 화면으로 이동
                autoSignInDone = true
                userSignedIn = true
            } else {
                // 실패한 케이스 (ex. NoCredentialException) -> 버튼으로 재시도 가능
                autoSignInDone = true
            }
        } ?: run {
            autoSignInDone = true
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
        if (!autoSignInDone) {
            Text("로그인 중...")
        } else if (userSignedIn) {
            // 자동 로그인 성공 후 -> 캘린더 권한 요청 버튼만 보여주기
            Button(onClick = {
                // [캘린더 동기화 -> 승인] flow를 CalendarSyncScreen에서 처리하도록
                navController.navigate(Screen.CalendarSync.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }) {
                Text("Google Calendar 동기화하기")
            }
        } else {
            // One-Tap 실패 / 사용자가 아직 로그인 안 한 경우 -> 로그인 버튼만 노출
            Button(onClick = {
                scope.launch {
                    // 수동 계정 선택 강제 One-Tap
                    val resp = authClient.beginUserSelect()
                    if (resp == null) {
                        // 사용자 취소 or NoCredentialException
                        // TODO: Toast/snackbar로 "Google 계정을 선택해주세요" 안내
                        return@launch
                    }
                    authClient.firebaseAuthWith(resp).onSuccess {
                        userSignedIn = true
                    }
                }
            }) {
                Text("Google 계정으로 로그인")
            }
        }
    }
}
