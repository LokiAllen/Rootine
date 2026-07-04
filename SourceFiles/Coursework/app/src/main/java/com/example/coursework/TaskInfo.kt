package com.example.coursework

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coursework.database.task.TaskViewModel
import com.example.coursework.database.task.Task
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// The TaskInfo composable allows the user to edit and view the information of a previously created task
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInfo(navController: NavController, taskIdString: String, taskViewModel: TaskViewModel = viewModel()) {
    // Initialize all variables that will be used to store the data for the new task and load data from the viewmodels
    val taskId = taskIdString.toInt()
    val task by taskViewModel.getTaskById(taskId).observeAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Initialize importance and stage with null values initially
    var importance by remember { mutableStateOf("0") }
    var stage by remember { mutableStateOf("0") }

    val datePickerState = rememberDatePickerState()
    var datePickerDialogOpen by remember { mutableStateOf(false) }
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    val hours = Array(24) { it.toString().padStart(2, '0') }
    val minutes = Array(60) { it.toString().padStart(2, '0') }
    var selectedDateText by remember { mutableStateOf("") }

    // Define the arrays for the imporances and stages
    val importances = arrayOf(
        "Low",
        "Medium",
        "High"
    )
    val stages = arrayOf(
        "NotStarted",
        "InProgress",
        "Completed"
    )

    //Load the data from the viewmodel into the previously created variables
    LaunchedEffect(task) {
        task?.let {
            title = it.title
            description = it.description
            // Directly use the importance and stage values from the task
            importance = importances[it.importance]
            stage = stages[it.stage]
            // Perform a check on goalTime, as it is not necessary for goalTime to be set, otherwise set up variables
            if (it.goalTime != 0L) {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis =
                        it.goalTime!!
                }
                val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                selectedDateText = dateFormatter.format(calendar.time)

                // Set hour and minute based on the task's goalTime
                hour = calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
                minute = calendar.get(Calendar.MINUTE).toString().padStart(2, '0')
            }

        }
    }

    // Create a scaffold to structure the page

    Scaffold(
        // Create a title bar for the page

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Task Information", style = MaterialTheme.typography.titleLarge)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                // Define a navigation button to go back to the subtasks of the task
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("TasksSubTasks/$taskIdString") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        // Next is to layout the editable fields showing the data that is stored by the task
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                var importanceExpanded by remember { mutableStateOf(false) }
                var stageExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = importanceExpanded,
                    onExpandedChange = { importanceExpanded = it },
                ) {
                    OutlinedTextField(
                        value = importance,
                        onValueChange = {},
                        label = {Text("Priority")},
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = importanceExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = importanceExpanded,
                        onDismissRequest = { importanceExpanded = false },
                    ) {
                        importances.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    importance = selectionOption
                                    importanceExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                ExposedDropdownMenuBox(
                    expanded = stageExpanded,
                    onExpandedChange = { stageExpanded = it },
                ) {
                    OutlinedTextField(
                        value = stage,
                        onValueChange = {},
                        label = {Text("Progress")},
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stageExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = stageExpanded,
                        onDismissRequest = { stageExpanded = false },
                    ) {
                        stages.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    stage = selectionOption
                                    stageExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                Button(onClick = { datePickerDialogOpen = true }) { Text("Select Goal Date") }
                if (datePickerDialogOpen) {
                    DatePickerDialog(
                        onDismissRequest = { datePickerDialogOpen = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                                    selectedDateText = dateFormatter.format(Date(datePickerState.selectedDateMillis ?: 0))
                                    datePickerDialogOpen = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { datePickerDialogOpen = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                // If the user has a date assigned to the task, show the boxes to edit it
                if (selectedDateText.isNotEmpty()) {
                    Text("Selected Date: $selectedDateText")

                    var hourExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = hourExpanded,
                        onExpandedChange = { hourExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = hour,
                            onValueChange = {},
                            label = { Text("Hour") },
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hourExpanded) },
                        )
                        ExposedDropdownMenu(
                            expanded = hourExpanded,
                            onDismissRequest = { hourExpanded = false },
                        ) {
                            hours.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        hour = selectionOption
                                        hourExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    var minuteExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = minuteExpanded,
                        onExpandedChange = { minuteExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = minute,
                            onValueChange = {},
                            label = { Text("Minute") },
                            modifier = Modifier.menuAnchor(),
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = minuteExpanded) },
                        )
                        ExposedDropdownMenu(
                            expanded = minuteExpanded,
                            onDismissRequest = { minuteExpanded = false },
                        ) {
                            minutes.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        minute = selectionOption
                                        minuteExpanded = false
                                    }
                                )
                            }
                        }
                    }


                }


            }
        },

        // In this bottom bar the Delete and save buttons are found
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // This centers the buttons with space around them
            ) {
                Button(
                    modifier = Modifier.weight(1f), // This makes the button expand to fill the Row equally
                    onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis ?: 0L
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = selectedDateMillis
                        }

                        // Convert hour and minute to Int safely
                        val hourInt = hour.toIntOrNull()
                        val minuteInt = minute.toIntOrNull()

                        if (hourInt == null || minuteInt == null) {
                            saveToDatabase(taskId,title,description,0,importances.indexOf(importance),stages.indexOf(stage), taskViewModel)
                        } else {
                            // Safe to proceed with valid hour and minute
                            calendar.set(Calendar.HOUR_OF_DAY, hourInt)
                            calendar.set(Calendar.MINUTE, minuteInt)
                            val goalTimeLong = calendar.timeInMillis
                            saveToDatabase(taskId,title,description,goalTimeLong,importances.indexOf(importance),stages.indexOf(stage), taskViewModel)
                        }
                        navController.navigate("TasksSubTasks/$taskId")
                    }
                ) {
                    Text("Save SubTask")
                }
                Spacer(modifier = Modifier.width(8.dp)) // Adds space between the two buttons
                Button(
                    modifier = Modifier.weight(1f), // This also makes the button expand to fill the Row equally
                    onClick = {
                        taskViewModel.deleteTask(task!!)
                        navController.navigate("HomeScreen")
                    }) {
                    Text("Delete Task")
                }
            }
        }
    )
}

// This function lets the user Save to database and define the primary key used

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(DelicateCoroutinesApi::class)
fun saveToDatabase(taskId: Int, title: String, description:String, goalTime:Long, importance:Int, stage:Int, taskViewModel: TaskViewModel){
    GlobalScope.launch {
        val completeTime = if (stage == 2) System.currentTimeMillis() else 0
        taskViewModel.upsertTask(
            Task(
                taskId = taskId,
                title = title,
                description = description,
                goalTime = goalTime,
                completeTime = completeTime,
                importance = importance,
                stage = stage
            )
        )
    }
}