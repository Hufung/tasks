package com.taskmanager.ui.addtask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.data.database.entities.Priority
import com.taskmanager.data.database.entities.Subtask
import com.taskmanager.data.database.entities.Tag
import com.taskmanager.data.database.entities.Task
import com.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddTaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _priority = MutableStateFlow(Priority.MEDIUM)
    val priority: StateFlow<Priority> = _priority.asStateFlow()

    private val _dueDate = MutableStateFlow<Long?>(null)
    val dueDate: StateFlow<Long?> = _dueDate.asStateFlow()

    private val _reminderTime = MutableStateFlow<Long?>(null)
    val reminderTime: StateFlow<Long?> = _reminderTime.asStateFlow()

    private val _subtasks = MutableStateFlow<List<String>>(emptyList())
    val subtasks: StateFlow<List<String>> = _subtasks.asStateFlow()

    private val _selectedTags = MutableStateFlow<List<Tag>>(emptyList())
    val selectedTags: StateFlow<List<Tag>> = _selectedTags.asStateFlow()

    val allTags = repository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTitle(value: String) {
        _title.value = value
    }

    fun setDescription(value: String) {
        _description.value = value
    }

    fun setPriority(value: Priority) {
        _priority.value = value
    }

    fun setDueDate(value: Long?) {
        _dueDate.value = value
    }

    fun setReminderTime(value: Long?) {
        _reminderTime.value = value
    }

    fun addSubtask(subtaskTitle: String) {
        if (subtaskTitle.isNotBlank()) {
            _subtasks.value = _subtasks.value + subtaskTitle
        }
    }

    fun removeSubtask(index: Int) {
        _subtasks.value = _subtasks.value.filterIndexed { i, _ -> i != index }
    }

    fun toggleTag(tag: Tag) {
        _selectedTags.value = if (_selectedTags.value.contains(tag)) {
            _selectedTags.value - tag
        } else {
            _selectedTags.value + tag
        }
    }

    suspend fun saveTask(): Boolean {
        if (_title.value.isBlank()) return false

        val task = Task(
            title = _title.value,
            description = _description.value,
            priority = _priority.value,
            dueDate = _dueDate.value,
            reminderTime = _reminderTime.value
        )

        val subtaskEntities = _subtasks.value.map { title ->
            Subtask(taskId = 0, title = title, orderIndex = 0)
        }

        val tagIds = _selectedTags.value.map { it.id }

        repository.insertTask(task, subtaskEntities, tagIds)
        return true
    }

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            repository.getTaskWithDetailsById(taskId).firstOrNull()?.let { taskWithDetails ->
                _title.value = taskWithDetails.task.title
                _description.value = taskWithDetails.task.description
                _priority.value = taskWithDetails.task.priority
                _dueDate.value = taskWithDetails.task.dueDate
                _reminderTime.value = taskWithDetails.task.reminderTime
                _subtasks.value = taskWithDetails.subtasks.map { it.title }
                _selectedTags.value = taskWithDetails.tags
            }
        }
    }

    suspend fun updateTask(taskId: Long): Boolean {
        if (_title.value.isBlank()) return false

        val task = Task(
            id = taskId,
            title = _title.value,
            description = _description.value,
            priority = _priority.value,
            dueDate = _dueDate.value,
            reminderTime = _reminderTime.value,
            updatedAt = System.currentTimeMillis()
        )

        val subtaskEntities = _subtasks.value.map { title ->
            Subtask(taskId = taskId, title = title, orderIndex = 0)
        }

        val tagIds = _selectedTags.value.map { it.id }

        repository.updateTask(task, subtaskEntities, tagIds)
        return true
    }
}
