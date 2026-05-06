package com.taskmanager.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taskmanager.data.repository.EventScheduleRepository
import com.taskmanager.data.repository.TaskRepository

class HomeViewModelFactory(
    private val repository: TaskRepository,
    private val eventScheduleRepository: EventScheduleRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, eventScheduleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
