package com.taskmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.taskmanager.data.database.entities.Subtask

@Composable
fun SubtaskItem(
    subtask: Subtask,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = { onToggle(!subtask.isCompleted) },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = if (subtask.isCompleted) {
                    Icons.Filled.CheckCircle
                } else {
                    Icons.Outlined.Circle
                },
                contentDescription = if (subtask.isCompleted) "Completed" else "Not completed",
                tint = if (subtask.isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Text(
            text = subtask.title,
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = if (subtask.isCompleted) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            },
            color = if (subtask.isCompleted) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
