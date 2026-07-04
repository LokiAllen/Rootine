package com.example.coursework

import NewSubTask
import NewTask
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coursework.ui.theme.CourseworkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.navigation.compose.currentBackStackEntryAsState

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isDarkModeEnabled = ThemePreference.isDarkModeEnabled(this)
        setContent {
            CourseworkTheme(useDarkTheme = isDarkModeEnabled) { // Use the colour scheme for the app
                val navController = rememberNavController()

                Scaffold(
                    // Add the NavigationBar to Scaffold's bottomBar parameter
                    bottomBar = { AppNavigationBar(navController) },
                ) { innerPadding ->
                    // Define the NavHost which will be used to traverse the entire network of pages
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(navController = navController, startDestination = "HomeScreen") {
                            composable("HomeScreen") { HomeScreen(navController) }
                            composable("NewTask") { NewTask(navController) }
                            composable("CompletedTasks") { CompletedTasks(navController)}
                            composable("Settings") { Settings(navController)}
                            composable("TaskInfo/{taskId}") { backStackEntry ->
                                // Retrieve the taskId argument as a String and pass it to TaskInfo
                                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                                TaskInfo(navController, taskId)
                            }
                            composable("TasksSubTasks/{taskId}") { backStackEntry ->
                                // Retrieve the taskId argument as a String and pass it to TaskInfo
                                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                                TasksSubTasks(navController, taskId)
                            }
                            composable("NewSubTask/{taskId}") { backStackEntry ->
                                // Retrieve the taskId argument as a String and pass it to TaskInfo
                                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                                NewSubTask(navController, taskId)
                            }
                            composable("SubTaskInfo/{taskId}/{subTaskId}") { backStackEntry ->
                                // Retrieve both taskId and subTaskId arguments
                                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                                val subTaskId = backStackEntry.arguments?.getString("subTaskId") ?: ""
                                SubTaskInfo(navController, taskId, subTaskId)
                            }
                        }
                    }
                }
            }
        }
    }
}

// The navigation bar will appear on all screens allowing the user to quickly return to their completed tasks, options or upcoming tasks
@Composable
fun AppNavigationBar(navController: NavController) {
    val items = listOf("Upcoming Tasks", "CompletedTasks","Settings",
        ) // Define navigation items to appear on the nav bar
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar (
    ){
        items.forEachIndexed { index, item ->
            val selected = when (item) {
                "Upcoming Tasks" -> currentRoute == "HomeScreen"
                "CompletedTasks" -> currentRoute == "CompletedTasks"
                "Settings" -> currentRoute == "Settings"
                else -> false
            }
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                label = { Text(item) },
                selected = selected,
                onClick = {
                    when (item) {
                        "Upcoming Tasks" -> navController.navigate("HomeScreen")
                        "CompletedTasks" -> navController.navigate("CompletedTasks")
                        "Settings" -> navController.navigate("Settings")
                    }
                }

            )
        }
    }
}
