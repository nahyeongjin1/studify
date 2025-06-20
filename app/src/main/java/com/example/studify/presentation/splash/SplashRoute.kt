package com.example.studify.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.studify.presentation.navigation.Screen
import com.example.studify.presentation.viewmodel.SplashViewModel
import com.example.studify.presentation.viewmodel.SplashViewModel.StartDest

@Composable
fun SplashRoute(
    navController: NavHostController,
    vm: SplashViewModel = hiltViewModel()
) {
    val dest by vm.startDestination.collectAsState()
    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(dest) {
        if (dest == StartDest.Pending || navigated) return@LaunchedEffect
        when (dest) {
            StartDest.Onboarding ->
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            StartDest.Login ->
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            StartDest.Home ->
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            else -> {}
        }
        navigated = true
    }
    // 간단한 로고 또는 로딩 인디케이터
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
