package com.sios.tech.plantreminderapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BroadcastReceiver that handles plant watering reminder alarms.
 *
 * This receiver is triggered by the AlarmManager when a plant's watering schedule time is reached.
 * It uses Hilt for dependency injection to access the notification service.
 *
 * The receiver expects two extras in the intent:
 * - EXTRA_PLANT_ID: The ID of the plant that needs watering
 * - EXTRA_PLANT_NAME: The name of the plant to display in the notification
 */
@AndroidEntryPoint
class WateringReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: PlantNotificationService

    /**
     * Called when a plant watering reminder alarm is triggered.
     *
     * @param context The Context in which the receiver is running
     * @param intent The Intent being received, containing plant details
     */
    override fun onReceive(context: Context, intent: Intent) {
        val plantId = intent.getLongExtra(EXTRA_PLANT_ID, -1)
        val plantName = intent.getStringExtra(EXTRA_PLANT_NAME) ?: return

        if (plantId != -1L) {
            notificationService.showWateringNotification(plantId.toInt(), plantName)
        }
    }

    companion object {
        /** Intent extra key for the plant ID */
        const val EXTRA_PLANT_ID = "extra_plant_id"
        
        /** Intent extra key for the plant name */
        const val EXTRA_PLANT_NAME = "extra_plant_name"
    }
}
