package com.taskmanager.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TagChip(
    tagName: String,
    tagColor: String,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null
) {
    val color = try {
        Color(android.graphics.Color.parseColor(tagColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    SuggestionChip(
        onClick = { onRemove?.invoke() },
        label = {
            Text(
                text = tagName,
                style = MaterialTheme.typography.labelSmall
            )
        },
        modifier = modifier.padding(horizontal = 4.dp),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = color.copy(alpha = 0.2f),
            labelColor = color
        )
    )
}
