package com.example.studify.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studify.presentation.home.HomeRoute
import com.example.studify.presentation.login.CalendarSyncScreen
import com.example.studify.presentation.login.LoginScreen
import com.example.studify.presentation.onboarding.OnboardingScreen
import com.example.studify.presentation.plan.PlanScreen
import com.example.studify.presentation.splash.SplashRoute
import com.example.studify.presentation.stat.StatScreen
import com.example.studify.presentation.viewmodel.OnboardingViewModel

@Composable
fun StudifyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) { SplashRoute(navController) }
        composable(route = Screen.Onboarding.route) {
            val vm: OnboardingViewModel = hiltViewModel()
            OnboardingScreen(
                onFinish = {
                    vm.setSeen()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(route = Screen.CalendarSync.route) {
            CalendarSyncScreen(navController)
        }
        composable(route = Screen.Home.route) {
            HomeRoute(navController)
        }
        composable(route = Screen.Plan.route) {
            PlanScreen(navController)
        }
        composable(route = Screen.Timer.route) {
            Text("Timer Screen")
        }
        composable(route = Screen.Stat.route) {
            StatScreen(navController)
        }
        composable(route = Screen.Profile.route) {
            Text("Profile Screen")
        }
    }
}
