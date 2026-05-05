package com.taskmanager.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.data.database.entities.TaskWithDetails
import com.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskDetailViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _taskId = MutableStateFlow<Long?>(null)

    val taskWithDetails: StateFlow<TaskWithDetails?> = _taskId
        .filterNotNull()
        .flatMapLatest { taskId ->
            repository.getTaskWithDetailsById(taskId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun loadTask(taskId: Long) {
        _taskId.value = taskId
    }

    fun toggleSubtaskCompletion(subtaskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleSubtaskCompletion(subtaskId, isCompleted)
        }
    }

    fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId, isCompleted)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}
