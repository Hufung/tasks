package com.taskmanager.ui.timetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: TimetableViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timetable") },
                actions = {
                    IconButton(onClick = { viewModel.loadTimetable() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (uiState.error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Class selector
            if (uiState.availableClasses.isNotEmpty()) {
                var expandedClass by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expandedClass,
                    onExpandedChange = { expandedClass = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedClass,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Class") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClass) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedClass,
                        onDismissRequest = { expandedClass = false }
                    ) {
                        uiState.availableClasses.forEach { className ->
                            DropdownMenuItem(
                                text = { Text(className) },
                                onClick = {
                                    viewModel.selectClass(className)
                                    expandedClass = false
                                }
                            )
                        }
                    }
                }
            }

            // Day selector
            if (uiState.availableDays.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = uiState.availableDays.indexOf(uiState.selectedDay).coerceAtLeast(0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    uiState.availableDays.forEach { day ->
                        Tab(
                            selected = uiState.selectedDay == day,
                            onClick = { viewModel.selectDay(day) },
                            text = { Text("Day $day") }
                        )
                    }
                }
            }

            // Timetable content
            val periods = uiState.timetable?.classes?.get(uiState.selectedClass)?.days?.get(uiState.selectedDay)

            if (periods != null && periods.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(periods) { index, period ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Period ${index + 1}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = period.subject,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Text(
                                    text = period.venue,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else if (!uiState.isLoading && uiState.error == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No timetable data available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
