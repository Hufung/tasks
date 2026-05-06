package com.taskmanager.ui.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taskmanager.ui.calendar.components.CalendarDayCell
import com.taskmanager.ui.calendar.components.DayDetailsCard
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToTaskDetail: (Long) -> Unit,
    onNavigateToAddTask: (LocalDate) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val calendarState = rememberSelectableCalendarState(
        initialMonth = uiState.currentMonth,
        initialSelection = listOf(uiState.selectedDate),
        initialSelectionMode = SelectionMode.Single
    )

    LaunchedEffect(calendarState.monthState.currentMonth) {
        viewModel.changeMonth(calendarState.monthState.currentMonth)
    }

    LaunchedEffect(calendarState.selectionState.selection) {
        calendarState.selectionState.selection.firstOrNull()?.let { date ->
            viewModel.selectDate(date)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                actions = {
                    IconButton(onClick = { viewModel.loadEventSchedule(forceRefresh = true) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddTask(uiState.selectedDate) },
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

            SelectableCalendar(
                calendarState = calendarState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 8.dp),
                dayContent = { dayState ->
                    val date = dayState.date
                    val isSelected = dayState.isFromCurrentMonth &&
                                    calendarState.selectionState.isDateSelected(date)
                    val isToday = date == LocalDate.now()
                    val hasEvents = viewModel.hasEventsOrTasks(date)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable(enabled = dayState.isFromCurrentMonth) {
                                if (dayState.isFromCurrentMonth) {
                                    calendarState.selectionState.onDateSelected(date)
                                }
                            }
                    ) {
                        CalendarDayCell(
                            date = date,
                            isSelected = isSelected,
                            isToday = isToday,
                            hasEvents = hasEvents && dayState.isFromCurrentMonth
                        )
                    }
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val tasksForDate = viewModel.getTasksForDate(uiState.selectedDate)
                    val activitiesForDate = viewModel.getActivitiesForDate(uiState.selectedDate)

                    DayDetailsCard(
                        date = uiState.selectedDate,
                        tasks = tasksForDate,
                        activities = activitiesForDate,
                        onTaskClick = onNavigateToTaskDetail
                    )
                }
            }
        }
    }
}
