package com.example.studify.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studify.presentation.navigation.StudifyNavGraph
import com.example.studify.presentation.ui.component.BottomNavBar
import com.example.studify.presentation.ui.component.BottomNavItem
import com.example.studify.presentation.ui.theme.StudifyTheme
import com.example.studify.presentation.util.RememberAuthSignedIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudifyTheme {
                val navController = rememberNavController()

                val navEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navEntry?.destination?.route

                val signedIn = RememberAuthSignedIn()

                val showBottomBar = signedIn && currentRoute in BottomNavItem.items.map { it.screen.route }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    StudifyNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
