package com.taskmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.taskmanager.data.database.entities.Priority
import com.taskmanager.ui.theme.*

@Composable
fun PriorityIndicator(
    priority: Priority,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val (color, label) = when (priority) {
        Priority.LOW -> PriorityLow to "Low"
        Priority.MEDIUM -> PriorityMedium to "Medium"
        Priority.HIGH -> PriorityHigh to "High"
        Priority.CRITICAL -> PriorityCritical to "Critical"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = MaterialTheme.shapes.small,
            color = color
        ) {}

        if (showLabel) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

fun getPriorityColor(priority: Priority): Color {
    return when (priority) {
        Priority.LOW -> PriorityLow
        Priority.MEDIUM -> PriorityMedium
        Priority.HIGH -> PriorityHigh
        Priority.CRITICAL -> PriorityCritical
    }
}
