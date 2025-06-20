package com.example.studify.presentation.onboarding

//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//
//@Composable
//fun OnboardingScreen(
//    onFinish: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Button(onClick = onFinish) { Text("Finish Onboarding") }
//}

// OnboardingScreen.kt

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studify.presentation.navigation.Screen
import com.example.studify.presentation.viewmodel.OnboardingViewModel

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingPage(Icons.Default.Book, "Welcome to Studify", "당신의 공부를 더 효율적이고, 더 체계적으로 만들어드립니다."),
    OnboardingPage(Icons.Default.Home, "Track Your Progress 2.0", "오늘의 진척도와 성취 통계를\n한눈에 확인하세요."),
    OnboardingPage(Icons.Default.EventNote, "Create Study Plans", "시험 정보를 입력하면\n자동으로 계획을 만들어드려요."),
    OnboardingPage(Icons.Default.Timer, "Focus With a Timer", "공부하고 싶은 과목을 선택해\n공부 시간을 집중해 기록해보세요."),
    OnboardingPage(Icons.Default.BarChart, "Track Your Study Data", "과목별 공부 시간과 집중도를 시각적으로 확인하고\n학습 패턴을 분석해보세요.")
)

@Composable
fun OnboardingScreen(
    navController: NavController,
    vm: OnboardingViewModel = hiltViewModel()
) {
    var pageIndex by rememberSaveable { mutableStateOf(0) }
    val page = onboardingPages[pageIndex]
    val lastIndex = onboardingPages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (pageIndex > 0) {
            IconButton(onClick = { pageIndex-- }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        } else {
            Spacer(modifier = Modifier.height(48.dp))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = page.title, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = page.description, fontSize = 16.sp, textAlign = TextAlign.Center)
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = {
                vm.setSeen()
                navController.navigate(Screen.Login.route){
                    popUpTo(Screen.Onboarding.route) {inclusive = true}
                }
                // TODO: 나중에 Main 화면으로 이동
            }) {
                Text("Skip")
            }
            Button(onClick = {
                if (pageIndex < lastIndex) {
                    pageIndex++
                } else {
                    vm.setSeen()
                    navController.navigate(Screen.Login.route){
                        popUpTo(Screen.Onboarding.route) {inclusive = true}
                    }
                    // TODO: 마지막 페이지 → Main 화면으로 이동
                }
            }) {
                Text(if(pageIndex < lastIndex) "Next" else "Start")
            }
        }
    }
}

