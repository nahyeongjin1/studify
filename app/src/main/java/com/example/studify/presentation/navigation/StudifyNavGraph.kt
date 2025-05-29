package com.example.studify.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun StudifyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route,
    ) {
        composable(route = Screen.Onboarding.route) {
            Text("Onboarding Screen")
        }
        composable(route = Screen.Login.route) {
            Text("Login Screen")
        }
        composable(route = Screen.Home.route) {
            Text("Home Screen")
        }
        composable(route = Screen.Plan.route) {
            Text("Plan Screen")
        }
        composable(route = Screen.Timer.route) {
            Text("Timer Screen")
        }
        composable(route = Screen.Stat.route) {
            Text("Statistics Screen")
        }
        composable(route = Screen.Profile.route) {
            Text("Profile Screen")
        }
    }
}
