package com.sios.tech.plantreminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sios.tech.plantreminderapp.presentation.plants.PlantsScreen
import com.sios.tech.plantreminderapp.ui.theme.PlantReminderAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantReminderAppTheme {
                PlantsScreen()
            }
        }
    }
}