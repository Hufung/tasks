package com.taskmanager.data.database.dao

import androidx.room.*
import com.taskmanager.data.database.entities.Task
import com.taskmanager.data.database.entities.TaskWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Transaction
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasksWithDetails(): Flow<List<TaskWithDetails>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskWithDetailsById(taskId: Long): Flow<TaskWithDetails?>

    @Transaction
    @Query("""
        SELECT DISTINCT tasks.* FROM tasks
        INNER JOIN task_tag_cross_ref ON tasks.id = task_tag_cross_ref.taskId
        WHERE task_tag_cross_ref.tagId = :tagId
        ORDER BY tasks.createdAt DESC
    """)
    fun getTasksByTag(tagId: Long): Flow<List<TaskWithDetails>>

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted ORDER BY createdAt DESC")
    fun getTasksByCompletionStatus(isCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate <= :timestamp ORDER BY dueDate ASC")
    fun getTasksDueBefore(timestamp: Long): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)
}
