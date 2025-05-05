package com.sios.tech.plantreminderapp.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sios.tech.plantreminderapp.domain.model.Plant
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles scheduling and cancellation of plant watering reminders using Android's AlarmManager.
 *
 * This class is responsible for:
 * - Scheduling exact alarms for plant watering notifications
 * - Canceling existing alarms when needed
 * - Ensuring alarms persist through device restarts
 *
 * @property context The application context used for accessing system services
 * @property alarmManager The Android AlarmManager service for scheduling alarms
 */
@Singleton
class PlantAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules a watering reminder for a specific plant.
     *
     * Uses setExactAndAllowWhileIdle to ensure the alarm triggers at the exact time,
     * even if the device is in Doze mode.
     *
     * @param plant The plant for which to schedule the watering reminder
     */
    fun scheduleWateringReminder(plant: Plant) {
        val intent = Intent(context, WateringReminderReceiver::class.java).apply {
            putExtra(WateringReminderReceiver.EXTRA_PLANT_ID, plant.id)
            putExtra(WateringReminderReceiver.EXTRA_PLANT_NAME, plant.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plant.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            plant.wateringSchedule.time,
            pendingIntent
        )
    }

    /**
     * Cancels an existing watering reminder for a specific plant.
     *
     * @param plantId The ID of the plant whose reminder should be canceled
     */
    fun cancelWateringReminder(plantId: Long) {
        val intent = Intent(context, WateringReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            plantId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}
