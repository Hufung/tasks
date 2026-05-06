package com.taskmanager.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class TimeSlot(
    val fullName: String,
    val remarks: List<String> = emptyList(),
    val S1: List<String> = emptyList(),
    val S2: List<String> = emptyList(),
    val S3: List<String> = emptyList(),
    val S4: List<String> = emptyList(),
    val S5: List<String> = emptyList(),
    val S6: List<String> = emptyList(),
    val otherActivities: List<String> = emptyList()
) {
    fun getActivitiesForGroup(group: String): List<String> {
        return when (group) {
            "S1" -> S1
            "S2" -> S2
            "S3" -> S3
            "S4" -> S4
            "S5" -> S5
            "S6" -> S6
            else -> emptyList()
        }
    }
}
