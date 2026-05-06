package com.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taskmanager.ui.addtask.AddTaskScreen
import com.taskmanager.ui.addtask.AddTaskViewModel
import com.taskmanager.ui.addtask.AddTaskViewModelFactory
import com.taskmanager.ui.calendar.CalendarScreen
import com.taskmanager.ui.calendar.CalendarViewModel
import com.taskmanager.ui.calendar.CalendarViewModelFactory
import com.taskmanager.ui.components.BottomNavigationBar
import com.taskmanager.ui.home.HomeScreen
import com.taskmanager.ui.home.HomeViewModel
import com.taskmanager.ui.home.HomeViewModelFactory
import com.taskmanager.ui.settings.SettingsScreen
import com.taskmanager.ui.settings.SettingsViewModel
import com.taskmanager.ui.settings.SettingsViewModelFactory
import com.taskmanager.ui.taskdetail.TaskDetailScreen
import com.taskmanager.ui.taskdetail.TaskDetailViewModel
import com.taskmanager.ui.taskdetail.TaskDetailViewModelFactory
import com.taskmanager.ui.theme.TaskManagerTheme
import com.taskmanager.utils.DateParser
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskManagerApp()
                }
            }
        }
    }
}

@Composable
fun TaskManagerApp() {
    val navController = rememberNavController()
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as TaskManagerApplication
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf("home", "calendar", "add_task")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(application.repository)
                )
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToAddTask = { navController.navigate("add_task") },
                    onNavigateToTaskDetail = { taskId ->
                        navController.navigate("task_detail/$taskId")
                    }
                )
            }

            composable("calendar") {
                val viewModel: CalendarViewModel = viewModel(
                    factory = CalendarViewModelFactory(
                        application.repository,
                        application.eventScheduleRepository,
                        application.preferencesRepository
                    )
                )
                CalendarScreen(
                    viewModel = viewModel,
                    onNavigateToTaskDetail = { taskId ->
                        navController.navigate("task_detail/$taskId")
                    },
                    onNavigateToAddTask = { date ->
                        val timestamp = DateParser.localDateToTimestamp(date)
                        navController.navigate("add_task_with_date/$timestamp")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }

            composable("add_task") {
                val viewModel: AddTaskViewModel = viewModel(
                    factory = AddTaskViewModelFactory(application.repository)
                )
                AddTaskScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "add_task_with_date/{timestamp}",
                arguments = listOf(navArgument("timestamp") { type = NavType.LongType })
            ) { backStackEntry ->
                val timestamp = backStackEntry.arguments?.getLong("timestamp") ?: return@composable
                val viewModel: AddTaskViewModel = viewModel(
                    factory = AddTaskViewModelFactory(application.repository)
                )
                AddTaskScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    prefilledDueDate = timestamp
                )
            }

            composable(
                route = "edit_task/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
                val viewModel: AddTaskViewModel = viewModel(
                    factory = AddTaskViewModelFactory(application.repository)
                )
                AddTaskScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    taskId = taskId
                )
            }

            composable(
                route = "task_detail/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.LongType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
                val viewModel: TaskDetailViewModel = viewModel(
                    factory = TaskDetailViewModelFactory(application.repository)
                )
                TaskDetailScreen(
                    viewModel = viewModel,
                    taskId = taskId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate("edit_task/$id")
                    }
                )
            }

            composable("settings") {
                val viewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(application.preferencesRepository)
                )
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
