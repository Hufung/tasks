package com.taskmanager.ui.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taskmanager.data.repository.TaskRepository

class AddTaskViewModelFactory(
    private val repository: TaskRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTaskViewModel::class.java)) {
            return AddTaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
