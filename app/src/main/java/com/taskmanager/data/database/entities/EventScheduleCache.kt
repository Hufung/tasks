package com.taskmanager.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_schedule_cache")
data class EventScheduleCache(
    @PrimaryKey val id: Int = 1,
    val jsonData: String,
    val lastFetched: Long
)
