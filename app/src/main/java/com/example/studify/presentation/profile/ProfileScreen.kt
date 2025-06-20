package com.example.studify.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
//    vm: ProfileViewModel = hiltViewModel()
) {
    // TODO – 뷰모델 연동해 실제 사용자 정보/스위치 상태 가져오기
    var studyAlarm by remember { mutableStateOf(true) }
    val calendarLinked = true // 예시

    Scaffold(topBar = {
        TopAppBar(title = { Text("프로필") })
    }) { inner ->
        Column(
            modifier =
                Modifier
                    .padding(inner)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
        ) {
            // --------- 사용자 ---------
            Spacer(Modifier.height(12.dp))
            ListItem(
                headlineContent = { Text("형진 님") },
                supportingContent = { Text("account@gmail.com") },
                leadingContent = {
                    Icon(
                        // temp profile image
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            )

            // --------- 환경설정 ---------
            Spacer(Modifier.height(16.dp))
            Text("학습 환경 설정", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("공부 알림", Modifier.weight(1f))
                Switch(checked = studyAlarm, onCheckedChange = { studyAlarm = it })
            }

            Spacer(Modifier.height(12.dp))
            Text("Google 캘린더 연동", style = MaterialTheme.typography.bodyMedium)
            Text(
                if (calendarLinked) "연동됨" else "미연동",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // --------- 앱 설정 ---------
            Spacer(Modifier.height(24.dp))
            Text("앱 설정", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            TextButton(onClick = { /* TODO vm.logout() */ }) {
                Text("로그아웃", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.weight(1f))
            Text(
                "앱 버전: v1.0.0\n문의: feedback@example.com",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
