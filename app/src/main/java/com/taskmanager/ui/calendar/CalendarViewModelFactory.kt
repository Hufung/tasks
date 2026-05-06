package com.taskmanager.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taskmanager.data.repository.EventScheduleRepository
import com.taskmanager.data.repository.PreferencesRepository
import com.taskmanager.data.repository.TaskRepository

class CalendarViewModelFactory(
    private val taskRepository: TaskRepository,
    private val eventScheduleRepository: EventScheduleRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(taskRepository, eventScheduleRepository, preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
