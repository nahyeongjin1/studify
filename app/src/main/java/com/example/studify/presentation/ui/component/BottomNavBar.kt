package com.example.studify.presentation.ui.component

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.studify.presentation.navigation.Screen

@SuppressLint("ContextCastToActivity")
@Composable
fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route
    val bottomRoutes = remember { BottomNavItem.items.map { it.screen.route }.toSet() }
    val activity = LocalContext.current as Activity

    BackHandler(enabled = currentRoute in bottomRoutes) {
        if (currentRoute != Screen.Home.route) {
            navController.popBackStack(Screen.Home.route, inclusive = false)
        } else {
            activity.finish()
        }
    }

    NavigationBar(modifier) {
        BottomNavItem.items.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.screen.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { IconResource(item.iconRes, item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun IconResource(
    @DrawableRes res: Int,
    label: String
) {
    Icon(
        painterResource(res),
        contentDescription = label
    )
}
