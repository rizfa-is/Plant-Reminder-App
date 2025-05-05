package com.sios.tech.plantreminderapp.domain.model

import java.util.Date

data class Plant(
    val id: Long = 0,
    val name: String,
    val wateringSchedule: Date,
    val notes: String = "",
    val lastWatered: Date? = null
)
