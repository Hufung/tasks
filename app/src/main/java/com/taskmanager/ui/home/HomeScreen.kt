package com.taskmanager.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taskmanager.ui.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val allTags by viewModel.allTags.collectAsState()
    val selectedTagId by viewModel.selectedTagId.collectAsState()
    val filterCompleted by viewModel.filterCompleted.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val todayCycleDay by viewModel.todayCycleDay.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Tasks")
                        todayCycleDay?.let { cycleDay ->
                            Text(
                                text = cycleDay,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showSearchBar) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search tasks...") },
                    singleLine = true
                )
            }

            if (selectedTagId != null) {
                val selectedTag = allTags.find { it.id == selectedTagId }
                selectedTag?.let { tag ->
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.setSelectedTag(null) },
                        label = { Text("Tag: ${tag.name}") },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = !filterCompleted,
                    onClick = { viewModel.setFilterCompleted(false) },
                    label = { Text("Active") }
                )
                FilterChip(
                    selected = filterCompleted,
                    onClick = { viewModel.setFilterCompleted(true) },
                    label = { Text("Completed") }
                )
            }

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = if (filterCompleted) "No completed tasks" else "No tasks yet. Tap + to add one!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks, key = { it.task.id }) { taskWithDetails ->
                        TaskCard(
                            taskWithDetails = taskWithDetails,
                            onClick = { onNavigateToTaskDetail(taskWithDetails.task.id) }
                        )
                    }
                }
            }
        }

        if (showFilterDialog) {
            FilterDialog(
                allTags = allTags,
                selectedTagId = selectedTagId,
                sortBy = sortBy,
                onTagSelected = { viewModel.setSelectedTag(it) },
                onSortByChanged = { viewModel.setSortBy(it) },
                onDismiss = { showFilterDialog = false }
            )
        }
    }
}

@Composable
fun FilterDialog(
    allTags: List<com.taskmanager.data.database.entities.Tag>,
    selectedTagId: Long?,
    sortBy: SortOption,
    onTagSelected: (Long?) -> Unit,
    onSortByChanged: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter & Sort") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Filter by Tag", style = MaterialTheme.typography.titleSmall)
                FilterChip(
                    selected = selectedTagId == null,
                    onClick = { onTagSelected(null) },
                    label = { Text("All Tags") }
                )
                allTags.forEach { tag ->
                    FilterChip(
                        selected = selectedTagId == tag.id,
                        onClick = { onTagSelected(tag.id) },
                        label = { Text(tag.name) }
                    )
                }

                Divider()

                Text("Sort by", style = MaterialTheme.typography.titleSmall)
                SortOption.values().forEach { option ->
                    FilterChip(
                        selected = sortBy == option,
                        onClick = { onSortByChanged(option) },
                        label = { Text(option.name.replace("_", " ")) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
