package com.taskmanager.data.repository

import com.taskmanager.data.database.dao.SubtaskDao
import com.taskmanager.data.database.dao.TagDao
import com.taskmanager.data.database.dao.TaskDao
import com.taskmanager.data.database.entities.*
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao,
    private val tagDao: TagDao
) {
    fun getAllTasksWithDetails(): Flow<List<TaskWithDetails>> {
        return taskDao.getAllTasksWithDetails()
    }

    fun getTaskWithDetailsById(taskId: Long): Flow<TaskWithDetails?> {
        return taskDao.getTaskWithDetailsById(taskId)
    }

    fun getTasksByTag(tagId: Long): Flow<List<TaskWithDetails>> {
        return taskDao.getTasksByTag(tagId)
    }

    fun getAllTags(): Flow<List<Tag>> {
        return tagDao.getAllTags()
    }

    fun getTasksByDateRange(startTimestamp: Long, endTimestamp: Long): Flow<List<TaskWithDetails>> {
        return taskDao.getTasksByDateRange(startTimestamp, endTimestamp)
    }

    fun getTasksForMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<TaskWithDetails>> {
        return taskDao.getTasksForMonth(startOfMonth, endOfMonth)
    }

    suspend fun insertTask(
        task: Task,
        subtasks: List<Subtask>,
        tagIds: List<Long>
    ): Long {
        val taskId = taskDao.insertTask(task)

        if (subtasks.isNotEmpty()) {
            val subtasksWithTaskId = subtasks.mapIndexed { index, subtask ->
                subtask.copy(taskId = taskId, orderIndex = index)
            }
            subtaskDao.insertSubtasks(subtasksWithTaskId)
        }

        tagIds.forEach { tagId ->
            tagDao.insertTaskTagCrossRef(TaskTagCrossRef(taskId, tagId))
        }

        return taskId
    }

    suspend fun updateTask(
        task: Task,
        subtasks: List<Subtask>,
        tagIds: List<Long>
    ) {
        taskDao.updateTask(task)

        subtaskDao.deleteSubtasksByTaskId(task.id)
        if (subtasks.isNotEmpty()) {
            val subtasksWithTaskId = subtasks.mapIndexed { index, subtask ->
                subtask.copy(taskId = task.id, orderIndex = index)
            }
            subtaskDao.insertSubtasks(subtasksWithTaskId)
        }

        tagDao.deleteTaskTagCrossRefsByTaskId(task.id)
        tagIds.forEach { tagId ->
            tagDao.insertTaskTagCrossRef(TaskTagCrossRef(task.id, tagId))
        }
    }

    suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun toggleSubtaskCompletion(subtaskId: Long, isCompleted: Boolean) {
        subtaskDao.updateSubtaskCompletion(subtaskId, isCompleted)
    }

    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        taskDao.updateTaskCompletion(taskId, isCompleted)
    }

    suspend fun insertTag(tag: Tag): Long {
        return tagDao.insertTag(tag)
    }

    suspend fun getOrCreateTag(name: String, color: String): Long {
        val existingTag = tagDao.getTagByName(name)
        return existingTag?.id ?: tagDao.insertTag(Tag(name = name, color = color))
    }
}
