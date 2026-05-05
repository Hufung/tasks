package com.taskmanager.data.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TaskWithDetails(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subtasks: List<Subtask>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TaskTagCrossRef::class,
            parentColumn = "taskId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
) {
    val completedSubtasksCount: Int
        get() = subtasks.count { it.isCompleted }

    val totalSubtasksCount: Int
        get() = subtasks.size

    val progress: Float
        get() = if (totalSubtasksCount == 0) 0f
                else completedSubtasksCount.toFloat() / totalSubtasksCount.toFloat()
}
