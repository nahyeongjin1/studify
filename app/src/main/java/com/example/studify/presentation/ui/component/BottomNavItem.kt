package com.example.studify.presentation.ui.component

import androidx.annotation.DrawableRes
import com.example.studify.R
import com.example.studify.presentation.navigation.Screen

sealed class BottomNavItem(
    val screen: Screen,
    @DrawableRes val iconRes: Int,
    val label: String,
) {
    data object Home : BottomNavItem(Screen.Home, R.drawable.ic_home, "Home")

    data object Plan : BottomNavItem(Screen.Plan, R.drawable.ic_plan, "Plan")

    data object Timer : BottomNavItem(Screen.Timer, R.drawable.ic_timer, "Timer")

    data object Stat : BottomNavItem(Screen.Stat, R.drawable.ic_stat, "Stat")

    companion object {
        val items = listOf(Home, Plan, Timer, Stat)
    }
}
