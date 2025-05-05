package com.sios.tech.plantreminderapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sios.tech.plantreminderapp.MainActivity
import com.sios.tech.plantreminderapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service responsible for creating and showing notifications for plant watering reminders.
 *
 * This class handles:
 * - Creating and managing notification channels (required for Android O and above)
 * - Building and displaying notifications with proper styling and actions
 * - Managing notification click behavior
 *
 * @property context The application context used for accessing system services
 * @property notificationManager The Android NotificationManager service
 */
@Singleton
class PlantNotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    /**
     * Creates a notification channel for plant watering reminders.
     * This is required for Android O (API 26) and above.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Plant Watering Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for plant watering schedules"
                enableLights(true)
                setShowBadge(true)
                lockscreenVisibility = NotificationManager.IMPORTANCE_HIGH
            }
            try {
                notificationManager.createNotificationChannel(channel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Shows a notification for a plant that needs watering.
     *
     * The notification includes:
     * - A title and description
     * - A small icon
     * - High priority to ensure visibility
     * - An intent to open the app when clicked
     *
     * @param plantId The ID of the plant that needs watering
     * @param plantName The name of the plant to display in the notification
     */
    fun showWateringNotification(plantId: Int, plantName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            plantId,
            intent,
            pendingIntentFlags
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Water your plant!")
            .setContentText("It's time to water $plantName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(plantId, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val CHANNEL_ID = "plant_watering_reminders"
    }
}
