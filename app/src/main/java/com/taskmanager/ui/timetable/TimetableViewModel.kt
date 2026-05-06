package com.taskmanager.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.data.network.models.TimetableResponse
import com.taskmanager.data.repository.PreferencesRepository
import com.taskmanager.data.repository.TimetableRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TimetableUiState(
    val timetable: TimetableResponse? = null,
    val selectedClass: String = "1A",
    val selectedDay: String = "A",
    val isLoading: Boolean = false,
    val error: String? = null,
    val availableClasses: List<String> = emptyList(),
    val availableDays: List<String> = emptyList()
)

class TimetableViewModel(
    private val timetableRepository: TimetableRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimetableUiState())
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    init {
        loadTimetable()
        observeStudentClass()
    }

    private fun observeStudentClass() {
        viewModelScope.launch {
            preferencesRepository.studentClass.collect { studentClass ->
                _uiState.update { it.copy(selectedClass = studentClass) }
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

    fun selectClass(className: String) {
        _uiState.update { it.copy(selectedClass = className) }
    }

    fun selectDay(day: String) {
        _uiState.update { it.copy(selectedDay = day) }
    }
}
