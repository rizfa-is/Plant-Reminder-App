package com.sios.tech.plantreminderapp.presentation.navigation

sealed class Screen(val route: String) {
    object Plants : Screen("plants")
    object Weather : Screen("weather")
}
