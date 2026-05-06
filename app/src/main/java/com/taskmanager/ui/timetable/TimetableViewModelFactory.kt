package com.taskmanager.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taskmanager.data.repository.EventScheduleRepository
import com.taskmanager.data.repository.PreferencesRepository
import com.taskmanager.data.repository.TimetableRepository

class TimetableViewModelFactory(
    private val timetableRepository: TimetableRepository,
    private val preferencesRepository: PreferencesRepository,
    private val eventScheduleRepository: EventScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimetableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimetableViewModel(timetableRepository, preferencesRepository, eventScheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
