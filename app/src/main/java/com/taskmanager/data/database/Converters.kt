package com.taskmanager.data.database

import androidx.room.TypeConverter
import com.taskmanager.data.database.entities.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }
}
