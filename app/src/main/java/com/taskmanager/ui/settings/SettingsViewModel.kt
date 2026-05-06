package com.taskmanager.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmanager.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val studentGroup: StateFlow<String> = preferencesRepository.studentGroup
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "S1"
        )

    val studentClass: StateFlow<String> = preferencesRepository.studentClass
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "1A"
        )

    val theme: StateFlow<String> = preferencesRepository.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "system"
        )

    val selectiveSubjects: StateFlow<String> = preferencesRepository.selectiveSubjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun setStudentGroup(group: String) {
        viewModelScope.launch {
            preferencesRepository.setStudentGroup(group)
        }
    }

    fun setStudentClass(className: String) {
        viewModelScope.launch {
            preferencesRepository.setStudentClass(className)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }

    fun setSelectiveSubjects(subjects: String) {
        viewModelScope.launch {
            preferencesRepository.setSelectiveSubjects(subjects)
        }
    }
}
