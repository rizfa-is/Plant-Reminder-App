package com.sios.tech.plantreminderapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.sios.tech.plantreminderapp.notification.PlantNotificationService
import com.sios.tech.plantreminderapp.presentation.components.LocalNotificationService
import com.sios.tech.plantreminderapp.presentation.navigation.Navigation
import com.sios.tech.plantreminderapp.ui.theme.PlantReminderAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Permission launcher for requesting notification permissions.
     *
     * This launcher handles the runtime permission request for notifications
     * on Android 13+ devices. It provides user feedback through toast messages
     * based on whether the permission was granted or denied.
     */
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(
                this,
                "Notification permission granted",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                "Notification permission denied. You won't receive watering reminders.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.entries.all { it.value }
        if (locationGranted) {
            Toast.makeText(
                this,
                "Location permission granted",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                "Location permission denied. You won't receive weather update.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @Inject
    lateinit var notificationService: PlantNotificationService

    /**
     * Initializes the activity and sets up the Compose UI.
     *
     * This method:
     * 1. Enables edge-to-edge display
     * 2. Requests notification permissions if needed
     * 3. Sets up the Compose content with CompositionLocalProvider
     *
     * The NotificationService is provided to the entire Compose hierarchy using
     * CompositionLocalProvider, making it available through LocalNotificationService.current
     * in any child composable.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestLocationPermission()
        requestNotificationPermission()
        setContent {
            CompositionLocalProvider(LocalNotificationService provides notificationService) {
                PlantReminderAppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        Navigation(navController)
                    }
                }
            }
        }
    }

    /**
     * Requests notification permission for Android 13+ devices.
     *
     * This method checks if:
     * 1. The device is running Android 13 or higher
     * 2. The notification permission is already granted
     * 3. Permission needs to be requested
     *
     * If permission is needed, it launches the permission request using
     * the requestPermissionLauncher.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(
                        this,
                        "Notification permission already granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    requestNotificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }
    }

    private fun requestLocationPermission() {
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ) {
            Toast.makeText(
                this,
                "Location permission already granted",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}