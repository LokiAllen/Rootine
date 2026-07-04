package com.example.coursework

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coursework.database.task.TaskViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import com.example.coursework.database.subTask.SubTask
import com.example.coursework.database.subTask.SubTaskViewModel
import com.example.coursework.database.task.Task
import java.time.Duration
import java.time.Instant

// The TasksSubTasks composable shows a task's subtasks acting similarly to the upcoming tasks page, it allows the user to manage the subtasks on a task
// as well as edit the task using the edit symbol in the corner
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TasksSubTasks(navController: NavController,taskIdString: String, taskViewModel: TaskViewModel = viewModel(), subTaskViewModel: SubTaskViewModel = viewModel()) {
    // Retrieve data from the database
    val subtasks = subTaskViewModel.getSubTasksByTaskId(taskIdString.toInt()).observeAsState(listOf()).value
    val taskId = taskIdString.toInt()
    val task by taskViewModel.getTaskById(taskId).observeAsState()
    Scaffold (
        // Floating button to create a new subtask
        floatingActionButton = {
            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = {
                    navController.navigate("NewSubTask/$taskIdString")
                },
                icon = { Icon(Icons.Filled.Add, "New Task") },
                text = { Text(text = "New SubTask") },
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        // The top bar will show the edit button and the title, as well as a back button which brings the user to the home page
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    task?.let { Text(text = it.title, style = MaterialTheme.typography.headlineMedium) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("HomeScreen") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("TaskInfo/$taskIdString") }) {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Edit Task"
                        )
                    }
                }
            )

        }
    ) {innerPadding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(subtasks.size) { index ->
                // Pass the index to TaskItem
                SubTaskItem(
                    subTask = subtasks[index],
                    onClick = {
                        navController.navigate("SubTaskInfo/${taskIdString}/${subtasks[index].subTaskId}")
                    },
                    colourDone = subtasks[index].completeTime,
                    subTaskViewModel
                )
            }
        }
    }
}

// Subtask item is used to display all the subtasks related to a task

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubTaskItem(subTask: SubTask, onClick: () -> Unit, colourDone: Long, subTaskViewModel: SubTaskViewModel) {
    // Different colours are used if the task is completed or not
    val backgroundColour = if (colourDone == 0L) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val textColour = if (colourDone == 0L) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    // Check if the task is completed based on completeTime
    val isTaskCompleted = subTask.completeTime != 0L
    // Initialize goalTimeInstant based on whether task.goalTime is meaningful
    val goalTimeInstant = if (subTask.goalTime != null && subTask.goalTime != 0L) {
        Instant.ofEpochMilli(subTask.goalTime)
    } else {
        null
    }

    val goalTime = subTask.goalTime ?: 0L
    var isChecked = subTask.completeTime != 0L

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
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(1f) // This makes the Column take up all available space
                ) {
                    Text(
                        text = subTask.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = subTask.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = timeLeftString,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { checked ->
                        isChecked = checked
                        // Update the database with the new completion status
                        saveSubToDatabase(
                            subTaskId = subTask.subTaskId,
                            taskId = subTask.taskId,
                            title = subTask.title,
                            description = subTask.description,
                            goalTime = goalTime,
                            complete = if (checked) 1 else 0,
                            subTaskViewModel = subTaskViewModel
                        )
                    }
                )
            }
        }
    }
}