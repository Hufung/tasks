package com.taskmanager.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.data.network.models.TimetableResponse
import com.taskmanager.data.repository.EventScheduleRepository
import com.taskmanager.data.repository.PreferencesRepository
import com.taskmanager.data.repository.TimetableRepository
import com.taskmanager.utils.DateParser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TimetableUiState(
    val timetable: TimetableResponse? = null,
    val selectedClass: String = "1A",
    val selectedDay: String = "A",
    val isLoading: Boolean = false,
    val error: String? = null,
    val availableClasses: List<String> = emptyList(),
    val availableDays: List<String> = emptyList(),
    val todayCycleDay: String? = null,
    val tomorrowCycleDay: String? = null,
    val showingTomorrow: Boolean = false
)

class TimetableViewModel(
    private val timetableRepository: TimetableRepository,
    private val preferencesRepository: PreferencesRepository,
    private val eventScheduleRepository: EventScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimetableUiState())
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    init {
        loadTimetable()
        observeStudentClass()
        loadEventSchedule()
    }

    private fun observeStudentClass() {
        viewModelScope.launch {
            preferencesRepository.studentClass.collect { studentClass ->
                _uiState.update { it.copy(selectedClass = studentClass) }
            }
        }
    }

    private fun loadEventSchedule() {
        viewModelScope.launch {
            eventScheduleRepository.getEventSchedule().collect { result ->
                result.onSuccess { response ->
                    val today = LocalDate.now()
                    val tomorrow = today.plusDays(1)

                    val todayDateString = DateParser.formatToApiDate(today)
                    val tomorrowDateString = DateParser.formatToApiDate(tomorrow)

                    val todayCycleDay = response.rows[todayDateString]?.cycleDay
                    val tomorrowCycleDay = response.rows[tomorrowDateString]?.cycleDay

                    _uiState.update {
                        it.copy(
                            todayCycleDay = todayCycleDay,
                            tomorrowCycleDay = tomorrowCycleDay,
                            selectedDay = todayCycleDay?.replace("Day ", "") ?: "A"
                        )
                    }
                }
            }
        }
    }

    fun loadTimetable() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            timetableRepository.getTimetable().collect { result ->
                result.fold(
                    onSuccess = { response ->
                        val classes = response.classes.keys.sorted()
                        val days = response.classes.values.firstOrNull()?.days?.keys?.sorted() ?: emptyList()
                        _uiState.update {
                            it.copy(
                                timetable = response,
                                availableClasses = classes,
                                availableDays = days,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                error = exception.message ?: "Failed to load timetable",
                                isLoading = false
                            )
                        }
                    }
                )
            }
        }
    }

    fun selectDay(day: String) {
        _uiState.update { it.copy(selectedDay = day, showingTomorrow = false) }
    }

    fun showTodayTimetable() {
        val todayCycleDay = _uiState.value.todayCycleDay?.replace("Day ", "") ?: "A"
        _uiState.update { it.copy(selectedDay = todayCycleDay, showingTomorrow = false) }
    }

    fun showTomorrowTimetable() {
        val tomorrowCycleDay = _uiState.value.tomorrowCycleDay?.replace("Day ", "") ?: "A"
        _uiState.update { it.copy(selectedDay = tomorrowCycleDay, showingTomorrow = true) }
    }
}
