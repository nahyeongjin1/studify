package com.example.studify.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "홈", Icons.Default.Home)
    object Plan : BottomNavItem("plan", "계획", Icons.Filled.EditCalendar)
    object Timer : BottomNavItem("timer", "타이머", Icons.Filled.Timer)
    object Stat : BottomNavItem("stat", "통계", Icons.Filled.BarChart)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Plan,
    BottomNavItem.Timer,
    BottomNavItem.Stat
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // 중복 방지
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
