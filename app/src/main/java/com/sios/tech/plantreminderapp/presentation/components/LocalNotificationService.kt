package com.sios.tech.plantreminderapp.presentation.components

import androidx.compose.runtime.compositionLocalOf
import com.sios.tech.plantreminderapp.notification.PlantNotificationService

val LocalNotificationService = compositionLocalOf<PlantNotificationService> {
    error("No NotificationService provided")
}
