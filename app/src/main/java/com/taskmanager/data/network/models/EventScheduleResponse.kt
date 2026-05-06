package com.taskmanager.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class EventScheduleResponse(
    val success: Boolean,
    val rows: Map<String, DaySchedule>
)
