package com.taskmanager.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.data.database.entities.TaskWithDetails
import com.taskmanager.data.network.models.DaySchedule
import com.taskmanager.data.network.models.EventScheduleResponse
import com.taskmanager.data.repository.EventScheduleRepository
import com.taskmanager.data.repository.PreferencesRepository
import com.taskmanager.data.repository.TaskRepository
import com.taskmanager.utils.DateParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    val tasksForMonth: List<TaskWithDetails> = emptyList(),
    val eventSchedule: EventScheduleResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val studentGroup: String = "S1"
)

data class DayActivity(
    val slotName: String,
    val activities: List<String>,
    val remarks: List<String>
)

class CalendarViewModel(
    private val taskRepository: TaskRepository,
    private val eventScheduleRepository: EventScheduleRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeStudentGroup()
    }

    private fun observeStudentGroup() {
        viewModelScope.launch {
            preferencesRepository.studentGroup.collect { group ->
                _uiState.update { it.copy(studentGroup = group) }
            }
        }
    }

    private fun loadData() {
        loadEventSchedule()
        loadTasksForMonth()
    }

    fun loadEventSchedule(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            eventScheduleRepository.getEventSchedule(forceRefresh).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.update { it.copy(eventSchedule = response, isLoading = false) }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                error = exception.message ?: "Failed to load event schedule",
                                isLoading = false
                            )
                        }
                    }
                )
            }
        }
    }

    private fun loadTasksForMonth() {
        viewModelScope.launch {
            val currentMonth = _uiState.value.currentMonth
            val startOfMonth = currentMonth.atDay(1)
            val endOfMonth = currentMonth.atEndOfMonth().plusDays(1)

            val startTimestamp = DateParser.localDateToTimestamp(startOfMonth)
            val endTimestamp = DateParser.localDateToTimestamp(endOfMonth)

            taskRepository.getTasksForMonth(startTimestamp, endTimestamp).collect { tasks ->
                _uiState.update { it.copy(tasksForMonth = tasks) }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun changeMonth(yearMonth: YearMonth) {
        _uiState.update { it.copy(currentMonth = yearMonth) }
        loadTasksForMonth()
    }

    fun getTasksForDate(date: LocalDate): List<TaskWithDetails> {
        return _uiState.value.tasksForMonth.filter { task ->
            task.task.dueDate?.let { dueDate ->
                DateParser.timestampToLocalDate(dueDate) == date
            } ?: false
        }
    }

    fun getEventScheduleForDate(date: LocalDate): DaySchedule? {
        val dateString = DateParser.formatToApiDate(date)
        return _uiState.value.eventSchedule?.rows?.get(dateString)
    }

    fun getActivitiesForDate(date: LocalDate): List<DayActivity> {
        val schedule = getEventScheduleForDate(date) ?: return emptyList()
        val studentGroup = _uiState.value.studentGroup

        return schedule.slots.map { (slotKey, timeSlot) ->
            val activities = timeSlot.getActivitiesForGroup(studentGroup) + timeSlot.otherActivities
            DayActivity(
                slotName = timeSlot.fullName,
                activities = activities.filter { it.isNotBlank() },
                remarks = timeSlot.remarks.filter { it.isNotBlank() }
            )
        }.filter { it.activities.isNotEmpty() || it.remarks.isNotEmpty() }
    }

    fun hasEventsOrTasks(date: LocalDate): Boolean {
        return getTasksForDate(date).isNotEmpty() || getEventScheduleForDate(date) != null
    }
}
