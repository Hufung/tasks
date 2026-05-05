package com.taskmanager.ui.addtask

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taskmanager.data.database.entities.Priority
import com.taskmanager.ui.components.PriorityIndicator
import com.taskmanager.ui.components.TagChip
import com.taskmanager.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: AddTaskViewModel,
    onNavigateBack: () -> Unit,
    taskId: Long? = null,
    modifier: Modifier = Modifier
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val dueDate by viewModel.dueDate.collectAsState()
    val reminderTime by viewModel.reminderTime.collectAsState()
    val subtasks by viewModel.subtasks.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val allTags by viewModel.allTags.collectAsState()

    val scope = rememberCoroutineScope()
    val isEditMode = taskId != null

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showTagDialog by remember { mutableStateOf(false) }
    var newSubtaskText by remember { mutableStateOf("") }

    LaunchedEffect(taskId) {
        taskId?.let { viewModel.loadTask(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Task" else "Add Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            scope.launch {
                                val success = if (isEditMode && taskId != null) {
                                    viewModel.updateTask(taskId)
                                } else {
                                    viewModel.saveTask()
                                }
                                if (success) onNavigateBack()
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.setTitle(it) },
                    label = { Text("Task Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.setDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }

            item {
                Text("Priority", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { viewModel.setPriority(p) },
                            label = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PriorityIndicator(priority = p, showLabel = true)
                                }
                            }
                        )
                    }
                }
            }

            item {
                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Due Date", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = dueDate?.let { DateUtils.formatDate(it) } ?: "Not set",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Reminder", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = reminderTime?.let { DateUtils.formatDateTime(it) } ?: "Not set",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = reminderTime != null,
                        onCheckedChange = {
                            if (it) {
                                showTimePicker = true
                            } else {
                                viewModel.setReminderTime(null)
                            }
                        }
                    )
                }
            }

            item {
                Text("Tags", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedTags.forEach { tag ->
                        TagChip(
                            tagName = tag.name,
                            tagColor = tag.color,
                            onRemove = { viewModel.toggleTag(tag) }
                        )
                    }
                    OutlinedButton(onClick = { showTagDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Add Tag")
                    }
                }
            }

            item {
                Text("Subtasks", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newSubtaskText,
                        onValueChange = { newSubtaskText = it },
                        placeholder = { Text("Add subtask") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            viewModel.addSubtask(newSubtaskText)
                            newSubtaskText = ""
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }

            itemsIndexed(subtasks) { index, subtask ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• $subtask",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { viewModel.removeSubtask(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }
        }

        if (showTagDialog) {
            AlertDialog(
                onDismissRequest = { showTagDialog = false },
                title = { Text("Select Tags") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allTags.forEach { tag ->
                            FilterChip(
                                selected = selectedTags.contains(tag),
                                onClick = { viewModel.toggleTag(tag) },
                                label = { Text(tag.name) }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTagDialog = false }) {
                        Text("Done")
                    }
                }
            )
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                viewModel.setDueDate(it)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            val timePickerState = rememberTimePickerState()
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("Set Reminder Time") },
                text = {
                    TimePicker(state = timePickerState)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = dueDate ?: System.currentTimeMillis()
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                            }
                            viewModel.setReminderTime(calendar.timeInMillis)
                            showTimePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
