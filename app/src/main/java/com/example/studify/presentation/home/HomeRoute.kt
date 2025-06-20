package com.example.studify.presentation.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.studify.presentation.navigation.Screen

@Composable
fun HomeRoute(navController: NavHostController) {
    HomeScreen(
        onEditClick = { navController.navigate(Screen.Plan.route) },
        onProfileClick = { navController.navigate(Screen.Profile.route) }
    )
}
