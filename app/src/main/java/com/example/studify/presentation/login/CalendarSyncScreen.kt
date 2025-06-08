package com.example.studify.presentation.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.studify.presentation.navigation.Screen
import com.example.studify.util.CalendarServiceHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.calendar.CalendarScopes

@SuppressLint("ContextCastToActivity")
@Composable
fun CalendarSyncScreen(navController: NavHostController) {
    val context = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()

    // 1) GoogleSignInOptions 구성: 기본 로그인(이메일) + Calendar 이벤트 권한 요청
    val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(CalendarScopes.CALENDAR_EVENTS))
            // .requestIdToken(...) 으로 Firebase와 연동하는 용도로 ID 토큰을 얻고 싶다면, .requestIdToken(serverClientId) 추가
            .build()

    // 2) GoogleSignInClient 생성
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // 3) ActivityResultLauncher 준비: 구글 로그인 인텐트 결과를 받는다
    var isSyncing by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                isSyncing = false

                if (result.resultCode == Activity.RESULT_OK) {
                    // 인텐트 결과에서 GoogleSignInAccount 트리거
                    val data: Intent? = result.data
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(data)

                    try {
                        val account = task.getResult(ApiException::class.java)
                        // account != null 이면, Calendar 권한 승인 성공

                        // CalenderServiceHelper를 호출하여 Calendar 클라이언트 생성 테스트
                        try {
                            val calendarService =
                                CalendarServiceHelper.getCalendarService(context, account!!)
                            Log.d("CalendarSyncScreen", "Calendar client 생성 성공: $calendarService")
                        } catch (t: Throwable) {
                            Log.e("CalendarSyncScreen", "Calendar client 생성 실패", t)
                        }

                        // *예시: 간단히 Home으로 네비게이트*
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.CalendarSync.route) { inclusive = true }
                        }
                    } catch (e: ApiException) {
                        // 사용자 취소 혹은 권한 거부
                        // TODO: Snackbar나 Toast로 “권한이 필요합니다” 안내
                        Log.e("CalendarSyncScreen", "GoogleSignInAccount 가져오기 실패", e)
                    }
                } else {
                    // RESULT_CANCELED (권한 허용 팝업에서 취소한 경우 등)
                    // TODO: 사용자에게 안내 (Snackbar, Toast 등)
                    Log.d("CalendarSyncScreen", "사용자가 Calendar 권한 요청을 취소했습니다.")
                }
            }
        )

    // 4) UI 배치
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSyncing) {
            Text("캘린더 권한을 요청 중…")
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Studify 캘린더 연동을 위해 Google 권한이 필요합니다.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    isSyncing = true
                    // GoogleSignIn 화면(권한 다이얼로그) 실행
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                }) {
                    Text("Google Calendar 권한 허용하기")
                }
            }
        }
    }
}
