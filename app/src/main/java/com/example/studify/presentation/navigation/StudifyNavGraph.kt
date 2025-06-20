package com.example.studify.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studify.presentation.home.HomeScreen
import com.example.studify.presentation.home.ProfileScreen
import com.example.studify.presentation.login.CalendarSyncScreen
import com.example.studify.presentation.login.LoginScreen
import com.example.studify.presentation.onboarding.OnboardingScreen
import com.example.studify.presentation.plan.PlanScreen
import com.example.studify.presentation.splash.SplashRoute
import com.example.studify.presentation.timer.TimeScreen
import com.example.studify.presentation.viewmodel.OnboardingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

fun hasCalendarPermission(account: GoogleSignInAccount?, context: android.content.Context): Boolean {
    // 실제로는 더 정교한 권한 체크가 필요하지만, 현재는 account가 null이 아니면 true로 가정
    return account != null
}

@Composable
fun StudifyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val account = GoogleSignIn.getLastSignedInAccount(context)

    val startDestination = when {
        account == null -> Screen.Login.route               // 로그인 안됨 → 로그인 화면
        !hasCalendarPermission(account, context) -> Screen.CalendarSync.route // 권한 없으면 권한 요청 화면
        else -> Screen.Home.route                            // 모두 충족 시 홈 화면
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(Screen.Splash.route) { SplashRoute(navController) }
        composable(route = Screen.Onboarding.route) {
            val vm: OnboardingViewModel = hiltViewModel()
            OnboardingScreen(navController = navController)
    //            onFinish = {
     //               vm.setSeen()
     //               navController.navigate(Screen.Login.route) {
     //                   popUpTo(Screen.Onboarding.route) {
     //                      inclusive = true
    //                    }
    //                }
    //            }
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(route = Screen.CalendarSync.route) {
            CalendarSyncScreen(navController)
        }
        composable(route = Screen.Home.route) {
            if (account != null) {
                HomeScreen(navController = navController, account = account)
            } else {
            Text("Google 계정이 로그인되지 않았습니다.")
        }
        }
        composable(route = Screen.Plan.route) {
            PlanScreen(navController)
        }
        composable(route = Screen.Timer.route) {
            TimeScreen(navController)
        }
        composable(route = Screen.Stat.route) {
            Text("Statistics Screen")
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
