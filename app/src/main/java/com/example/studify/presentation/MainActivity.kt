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
import com.example.studify.presentation.components.BottomNavigationBar
import com.example.studify.presentation.navigation.StudifyNavGraph
import com.example.studify.presentation.ui.theme.StudifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudifyTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                Scaffold(modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // onboarding이나 login 화면이 아니면 보여줌
                        if (currentRoute !in listOf("onboarding", "login")) {
                            BottomNavigationBar(navController)
                        }
                    }) { innerPadding ->
                    StudifyNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
