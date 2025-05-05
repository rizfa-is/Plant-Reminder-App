package com.sios.tech.plantreminderapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sios.tech.plantreminderapp.presentation.plants.PlantsScreen
import com.sios.tech.plantreminderapp.presentation.weather.WeatherScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Plants.route
    ) {
        composable(Screen.Plants.route) {
            PlantsScreen(
                onNavigateToWeather = {
                    navController.navigate(Screen.Weather.route)
                }
            )
        }
        
        composable(Screen.Weather.route) {
            WeatherScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
