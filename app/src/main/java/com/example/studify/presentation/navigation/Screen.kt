package com.example.studify.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")

    object Onboarding : Screen("onboarding")

    object Login : Screen("login")

    object CalendarSync : Screen("calendar_sync")

    object Home : Screen("home") // 1

    object Plan : Screen("plan") // 2

    object Timer : Screen("timer") // 3

    object Stat : Screen("stat") // 4

    object Profile : Screen("profile") // 우상단 아이콘 통해 접근
}
