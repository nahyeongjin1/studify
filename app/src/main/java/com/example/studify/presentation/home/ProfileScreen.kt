package com.example.studify.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studify.presentation.navigation.Screen
import com.example.studify.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val email by viewModel.email.collectAsState()
    val isNotificationOn by viewModel.isNotificationOn.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("프로필") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccountCircle,
                    modifier = Modifier.size(64.dp),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(userName, fontSize = 20.sp)
                    Text(email, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("학습 환경 설정", fontWeight = FontWeight.Bold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("공부 알림")
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = isNotificationOn,
                    onCheckedChange = { viewModel.setNotification(it) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Google 캘린더 연동")
            Text("연동됨", color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))
            Text("앱 설정", fontWeight = FontWeight.Bold)

            Text(
                "로그아웃",
                color = Color.Red,
                modifier = Modifier.clickable {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true } // 뒤로가기 방지
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("앱 버전: v1.0.0")
            Text("문의: feedback@example.com")
        }
    }
}
