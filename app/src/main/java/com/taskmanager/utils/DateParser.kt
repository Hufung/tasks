package com.taskmanager.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateParser {
    private val apiDateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy")

    fun parseApiDate(dateString: String): LocalDate? {
        return try {
            LocalDate.parse(dateString, apiDateFormatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun formatToApiDate(date: LocalDate): String {
        return date.format(apiDateFormatter)
    }

    fun localDateToTimestamp(date: LocalDate): Long {
        return date.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC) * 1000
    }

    fun timestampToLocalDate(timestamp: Long): LocalDate {
        return java.time.Instant.ofEpochMilli(timestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
    }
}
