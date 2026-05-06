package com.taskmanager.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class DaySchedule(
    val cycle: String,
    val cycleDay: String? = null,
    val slots: Map<String, TimeSlot>
)
