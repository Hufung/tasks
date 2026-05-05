package com.taskmanager.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun isOverdue(dueDate: Long?): Boolean {
        if (dueDate == null) return false
        return dueDate < System.currentTimeMillis()
    }

    fun isDueToday(dueDate: Long?): Boolean {
        if (dueDate == null) return false
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val tomorrow = Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.DAY_OF_YEAR, 1)
        }
        return dueDate >= today.timeInMillis && dueDate < tomorrow.timeInMillis
    }

    fun getDaysUntilDue(dueDate: Long?): Int {
        if (dueDate == null) return Int.MAX_VALUE
        val diff = dueDate - System.currentTimeMillis()
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }
}
