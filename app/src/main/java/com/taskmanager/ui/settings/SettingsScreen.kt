package com.taskmanager.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedGroup by viewModel.studentGroup.collectAsState()
    val selectedClass by viewModel.studentClass.collectAsState()
    val selectedTheme by viewModel.theme.collectAsState()
    val selectiveSubjects by viewModel.selectiveSubjects.collectAsState()

    var showGroupDialog by remember { mutableStateOf(false) }
    var showClassDialog by remember { mutableStateOf(false) }
    var showSubjectsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Student Group Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showGroupDialog = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Student Group",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedGroup,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Student Class Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showClassDialog = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Class",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedClass.takeLast(1),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Full class: $selectedClass",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Selective Subjects Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showSubjectsDialog = true }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selective Subjects (e.g., 1X)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectiveSubjects.isBlank()) "Not set" else selectiveSubjects,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectiveSubjects.isBlank())
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Theme Section
            Column {
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    listOf(
                        "system" to "System Default",
                        "light" to "Light Mode",
                        "dark" to "Dark Mode"
                    ).forEach { (value, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (value == selectedTheme),
                                    onClick = { viewModel.setTheme(value) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (value == selectedTheme),
                                onClick = null
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Student Group Dialog
        if (showGroupDialog) {
            AlertDialog(
                onDismissRequest = { showGroupDialog = false },
                title = { Text("Select Student Group") },
                text = {
                    Column(
                        modifier = Modifier.selectableGroup()
                    ) {
                        listOf("S1", "S2", "S3", "S4", "S5", "S6").forEach { group ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (group == selectedGroup),
                                        onClick = {
                                            viewModel.setStudentGroup(group)
                                            showGroupDialog = false
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (group == selectedGroup),
                                    onClick = null
                                )
                                Text(
                                    text = group,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showGroupDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Student Class Dialog
        if (showClassDialog) {
            AlertDialog(
                onDismissRequest = { showClassDialog = false },
                title = { Text("Select Class") },
                text = {
                    Column(
                        modifier = Modifier.selectableGroup()
                    ) {
                        val classLetters = listOf("A", "B", "C", "D", "E")
                        val currentYear = selectedGroup.removePrefix("S")
                        val currentClassLetter = selectedClass.takeLast(1)

                        classLetters.forEach { letter ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (letter == currentClassLetter),
                                        onClick = {
                                            viewModel.setStudentClass("$currentYear$letter")
                                            showClassDialog = false
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (letter == currentClassLetter),
                                    onClick = null
                                )
                                Text(
                                    text = "Class $letter ($currentYear$letter)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showClassDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        // Selective Subjects Dialog
        if (showSubjectsDialog) {
            var subjectsInput by remember { mutableStateOf(selectiveSubjects) }
            AlertDialog(
                onDismissRequest = { showSubjectsDialog = false },
                title = { Text("Selective Subjects") },
                text = {
                    Column {
                        Text(
                            text = "Enter your selective subjects (e.g., 1X, 2Y)",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = subjectsInput,
                            onValueChange = { subjectsInput = it },
                            label = { Text("Subjects") },
                            placeholder = { Text("e.g., 1X") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.setSelectiveSubjects(subjectsInput)
                            showSubjectsDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubjectsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
