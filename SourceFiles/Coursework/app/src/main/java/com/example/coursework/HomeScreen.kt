package com.example.coursework

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coursework.database.task.TaskViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.coursework.database.task.Task
import java.time.Duration
import java.time.Instant

// The homeScreen composable will display upcoming tasks, allowing the user to navigate through the system and create a new task
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, taskViewModel: TaskViewModel = viewModel()) {
    // Observing LiveData from the ViewModel
    val tasks = taskViewModel.getNonComplete.observeAsState(listOf()).value
    Scaffold (
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = {
                    navController.navigate("newTask")
                },
                icon = { Icon(Icons.Filled.Add, "New Task") },
                text = { Text(text = "New Task") },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Upcoming Tasks", style = MaterialTheme.typography.headlineMedium)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) {innerPadding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks.size) { index ->
                // Pass the index to TaskItem
                TaskItem(
                    task = tasks[index],
                    onClick = {
                        navController.navigate("TasksSubTasks/${tasks[index].taskId}")
                    },
                    colourImportance = tasks[index].importance // Now passing the index
                )
            }
        }
    }
}


// Task item is used in a lazylist to display databased on its time till completion, title description as well as assigning different colours for different importances
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(task: Task, onClick: () -> Unit, colourImportance: Int) {
    val backgroundColour = when (colourImportance) {
        2 -> MaterialTheme.colorScheme.primaryContainer // High importance
        1 -> MaterialTheme.colorScheme.secondaryContainer // Medium importance
        0 -> MaterialTheme.colorScheme.tertiaryContainer // Low importance
        else -> MaterialTheme.colorScheme.errorContainer // Default case if needed
    }
    val textColour = when (colourImportance) {
        2 -> MaterialTheme.colorScheme.onPrimaryContainer
        1 -> MaterialTheme.colorScheme.onSecondaryContainer
        0 -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }
    // Check if the task is completed based on completeTime
    val isTaskCompleted = task.completeTime != 0L
    // Initialize goalTimeInstant based on whether task.goalTime is meaningful
    val goalTimeInstant = if (task.goalTime != null && task.goalTime != 0L) {
        Instant.ofEpochMilli(task.goalTime)
    } else {
        null
    }

    // Calculate time left only if goalTimeInstant is not null and task is not completed
    val timeLeftString = if (!isTaskCompleted) {
        goalTimeInstant?.let {
            val currentTime = Instant.now()
            val timeLeftDuration = Duration.between(currentTime, it)
            if (!timeLeftDuration.isNegative) {
                val daysLeft = timeLeftDuration.toDays()
                val hoursLeft = timeLeftDuration.minusDays(daysLeft).toHours()
                val minutesLeft = timeLeftDuration.minusDays(daysLeft).minusHours(hoursLeft).toMinutes()
                "$daysLeft days, $hoursLeft hours, $minutesLeft minutes left"
            } else {
                "Goal time passed"
            }
        } ?: "No deadline"
    } else {
        "" // No text is displayed if the task is completed
    }


    Card(
        Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColour,
            contentColor = textColour
        )
    ){
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
        ){
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = timeLeftString,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
