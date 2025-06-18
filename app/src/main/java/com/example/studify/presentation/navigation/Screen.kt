package com.example.studify.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")

    object Onboarding : Screen("onboarding")

    object Login : Screen("login")

    object CalendarSync : Screen("calendar_sync")

    object Home : Screen("home")

    object Plan : Screen("plan")

    object Timer : Screen("timer")

    object Stat : Screen("stat")

    object Profile : Screen("profile") // 우상단 아이콘 통해 접근
}
