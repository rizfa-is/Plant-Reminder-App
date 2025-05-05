package com.sios.tech.plantreminderapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val wateringSchedule: Date,
    val notes: String,
    val lastWatered: Date?
)
