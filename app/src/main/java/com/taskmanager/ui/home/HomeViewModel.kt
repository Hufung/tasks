package com.taskmanager.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.data.database.entities.Priority
import com.taskmanager.data.database.entities.TaskWithDetails
import com.taskmanager.data.network.models.EventScheduleResponse
import com.taskmanager.data.repository.EventScheduleRepository
import com.taskmanager.data.repository.TaskRepository
import com.taskmanager.utils.DateParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val repository: TaskRepository,
    private val eventScheduleRepository: EventScheduleRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTagId = MutableStateFlow<Long?>(null)
    val selectedTagId: StateFlow<Long?> = _selectedTagId.asStateFlow()

    private val _filterCompleted = MutableStateFlow(false)
    val filterCompleted: StateFlow<Boolean> = _filterCompleted.asStateFlow()

    private val _sortBy = MutableStateFlow(SortOption.CREATED_DATE)
    val sortBy: StateFlow<SortOption> = _sortBy.asStateFlow()

    private val _eventSchedule = MutableStateFlow<EventScheduleResponse?>(null)
    val eventSchedule: StateFlow<EventScheduleResponse?> = _eventSchedule.asStateFlow()

    val todayCycleDay: StateFlow<String?> = _eventSchedule.map { schedule ->
        val today = LocalDate.now()
        val dateString = DateParser.formatToApiDate(today)
        schedule?.rows?.get(dateString)?.cycleDay
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        loadEventSchedule()
    }

    private fun loadEventSchedule() {
        viewModelScope.launch {
            eventScheduleRepository.getEventSchedule().collect { result ->
                result.onSuccess { response ->
                    _eventSchedule.value = response
                }
            }
        }
    }

    val allTags = repository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<TaskWithDetails>> = combine(
        repository.getAllTasksWithDetails(),
        _searchQuery,
        _selectedTagId,
        _filterCompleted,
        _sortBy
    ) { tasks, query, tagId, showCompleted, sort ->
        tasks
            .filter { taskWithDetails ->
                val matchesSearch = if (query.isBlank()) {
                    true
                } else {
                    taskWithDetails.task.title.contains(query, ignoreCase = true) ||
                            taskWithDetails.task.description.contains(query, ignoreCase = true)
                }

                val matchesTag = if (tagId == null) {
                    true
                } else {
                    taskWithDetails.tags.any { it.id == tagId }
                }

                val matchesCompletion = if (showCompleted) {
                    taskWithDetails.task.isCompleted
                } else {
                    !taskWithDetails.task.isCompleted
                }

                matchesSearch && matchesTag && matchesCompletion
            }
            .sortedWith(getSortComparator(sort))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTag(tagId: Long?) {
        _selectedTagId.value = tagId
    }

    fun setFilterCompleted(showCompleted: Boolean) {
        _filterCompleted.value = showCompleted
    }

    fun setSortBy(sortOption: SortOption) {
        _sortBy.value = sortOption
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

    private fun getSortComparator(sortOption: SortOption): Comparator<TaskWithDetails> {
        return when (sortOption) {
            SortOption.CREATED_DATE -> compareByDescending { it.task.createdAt }
            SortOption.DUE_DATE -> compareBy<TaskWithDetails> { it.task.dueDate ?: Long.MAX_VALUE }
            SortOption.PRIORITY -> compareByDescending { it.task.priority }
            SortOption.TITLE -> compareBy { it.task.title }
        }
    }
}

enum class SortOption {
    CREATED_DATE,
    DUE_DATE,
    PRIORITY,
    TITLE
}
