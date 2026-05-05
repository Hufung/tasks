package com.taskmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taskmanager.ui.addtask.AddTaskScreen
import com.taskmanager.ui.addtask.AddTaskViewModel
import com.taskmanager.ui.home.HomeScreen
import com.taskmanager.ui.home.HomeViewModel
import com.taskmanager.ui.taskdetail.TaskDetailScreen
import com.taskmanager.ui.taskdetail.TaskDetailViewModel
import com.taskmanager.ui.theme.TaskManagerTheme

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

    NavHost(
        navController = navController,
        startDestination = "home"
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
    }
}
