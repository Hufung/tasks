package com.taskmanager

import android.app.Application
import com.taskmanager.data.database.TaskDatabase
import com.taskmanager.data.network.NetworkModule
import com.taskmanager.data.repository.EventScheduleRepository
import com.taskmanager.data.repository.PreferencesRepository
import com.taskmanager.data.repository.TaskRepository
import com.taskmanager.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskManagerApplication : Application() {
    private val database by lazy { TaskDatabase.getDatabase(this) }

    val repository by lazy {
        TaskRepository(
            database.taskDao(),
            database.subtaskDao(),
            database.tagDao()
        )
    }

    val eventScheduleRepository by lazy {
        EventScheduleRepository(
            NetworkModule.eventScheduleApiService,
            database.eventScheduleCacheDao(),
            this
        )
    }

    val preferencesRepository by lazy {
        PreferencesRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
        initializeDefaultTags()
    }

    private fun initializeDefaultTags() {
        CoroutineScope(Dispatchers.IO).launch {
            Constants.DEFAULT_TAGS.forEach { (name, color) ->
                repository.getOrCreateTag(name, color)
            }
        }
    }
}
