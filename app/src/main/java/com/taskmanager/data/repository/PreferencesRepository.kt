package com.taskmanager.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(private val context: Context) {
    private val studentGroupKey = stringPreferencesKey("student_group")
    private val studentClassKey = stringPreferencesKey("student_class")

    val studentGroup: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[studentGroupKey] ?: "S1"
    }

    val studentClass: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[studentClassKey] ?: "1A"
    }

    suspend fun setStudentGroup(group: String) {
        context.dataStore.edit { preferences ->
            preferences[studentGroupKey] = group
        }
    }

    suspend fun setStudentClass(className: String) {
        context.dataStore.edit { preferences ->
            preferences[studentClassKey] = className
        }
    }
}
