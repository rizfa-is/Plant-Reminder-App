package com.sios.tech.plantreminderapp.presentation.components

import androidx.compose.runtime.compositionLocalOf
import com.sios.tech.plantreminderapp.notification.PlantNotificationService

/**
 * CompositionLocal for providing the PlantNotificationService throughout the Compose UI hierarchy.
 *
 * This allows any composable in the hierarchy to access the notification service without explicit
 * parameter passing. The service must be provided at a higher level in the composition using
 * CompositionLocalProvider.
 *
 * Example usage in a composable:
 * ```
 * val notificationService = LocalNotificationService.current
 * notificationService.showWateringNotification(plantId, plantName)
 * ```
 *
 * The error message "No NotificationService provided" will be thrown if the service is accessed
 * without being provided in a parent composable.
 *
 * @see PlantNotificationService
 * @see androidx.compose.runtime.CompositionLocalProvider
 */
val LocalNotificationService = compositionLocalOf<PlantNotificationService> {
    error("No NotificationService provided")
}
