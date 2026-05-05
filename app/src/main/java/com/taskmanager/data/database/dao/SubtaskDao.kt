package com.taskmanager.data.database.dao

import androidx.room.*
import com.taskmanager.data.database.entities.Subtask
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtaskDao {
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY orderIndex ASC")
    fun getSubtasksByTaskId(taskId: Long): Flow<List<Subtask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: Subtask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtasks(subtasks: List<Subtask>)

    @Update
    suspend fun updateSubtask(subtask: Subtask)

    @Delete
    suspend fun deleteSubtask(subtask: Subtask)

    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteSubtasksByTaskId(taskId: Long)

    @Query("UPDATE subtasks SET isCompleted = :isCompleted WHERE id = :subtaskId")
    suspend fun updateSubtaskCompletion(subtaskId: Long, isCompleted: Boolean)
}
