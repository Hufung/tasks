package com.taskmanager.data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class TimetableResponse(
    val classes: Map<String, ClassTimetable>
) {
    companion object {
        fun fromMap(map: Map<String, Map<String, List<Period>>>): TimetableResponse {
            val classes = map.mapValues { (_, dayMap) ->
                ClassTimetable(days = dayMap)
            }
            return TimetableResponse(classes)
        }
    }
}

@Serializable
data class ClassTimetable(
    val days: Map<String, List<Period>>
)

@Serializable
data class Period(
    val subject: String,
    val venue: String
)
