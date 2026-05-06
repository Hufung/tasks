package com.taskmanager.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taskmanager.data.database.TaskDatabase
import com.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("TASK_ID", -1)
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Task Reminder"

        if (taskId != -1L) {
            val database = TaskDatabase.getDatabase(context)
            val repository = TaskRepository(
                database.taskDao(),
                database.subtaskDao(),
                database.tagDao(),
                context
            )

            CoroutineScope(Dispatchers.IO).launch {
                repository.getTaskWithDetailsById(taskId).collect { taskWithDetails ->
                    taskWithDetails?.let {
                        val notificationHelper = NotificationHelper(context)
                        notificationHelper.showTaskReminder(
                            taskId,
                            it.task.title,
                            it.task.description
                        )
                    }
                }
            }
        }
    }
}
