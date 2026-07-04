package com.example.coursework

import android.database.sqlite.SQLiteConstraintException
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
import com.example.coursework.database.subTask.SubTask
import com.example.coursework.database.subTask.SubTaskViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// The SubTaskInfo composable is used to view and change the information of a subtask
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubTaskInfo(navController: NavController,taskIdString: String,subTaskIdString: String, subTaskViewModel: SubTaskViewModel = viewModel()) {
    // Initialize all variables that will be used to store the data for the new task, as well as load info form the database
    val taskId = taskIdString.toInt()
    val subTaskId = subTaskIdString.toInt()
    val subtask by subTaskViewModel.getSubTaskById(subTaskId).observeAsState()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }


    val datePickerState = rememberDatePickerState()
    var datePickerDialogOpen by remember { mutableStateOf(false) }
    var hour by remember { mutableStateOf("0") }
    var minute by remember { mutableStateOf("0") }
    val hours = Array(24) { it.toString().padStart(2, '0') } // "00" to "23"
    val minutes = Array(60) { it.toString().padStart(2, '0') } // "00" to "59"
    var selectedDateText by remember { mutableStateOf("") }

    //Load the data from the viewmodel into the previously created variables
    LaunchedEffect(subtask) {
        subtask?.let {
            title = it.title
            description = it.description
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
                    Text(text = "SubTask Information", style = MaterialTheme.typography.titleLarge)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                // The back button will take the user back to the list of Subtasks
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("TasksSubTasks/$taskIdString") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp) // Additional padding for the content
                    .verticalScroll(rememberScrollState()), // This will make the app more compatible as smaller screens can still view all data by scrolling
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
                Button(onClick = { datePickerDialogOpen = true }) { Text("Select Goal Date") }
                    // The date picker allows the user to pick the date, when used this will also allow the user to select the time
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

        // The bottom bar allows the user to Save the subtask and delete

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
                            saveSubToDatabase(subTaskId, taskId, title, description, 0, 0, subTaskViewModel)
                        } else {
                            // Safe to proceed with valid hour and minute
                            calendar.set(Calendar.HOUR_OF_DAY, hourInt)
                            calendar.set(Calendar.MINUTE, minuteInt)
                            val goalTimeLong = calendar.timeInMillis
                            saveSubToDatabase(subTaskId, taskId, title, description, goalTimeLong, 0, subTaskViewModel)
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
                        subTaskViewModel.deleteSubTask(subtask!!)
                        navController.navigate("TasksSubTasks/$taskId")
                    }) {
                    Text("Delete SubTask")
                }
            }
        }
    )
}

// This allows the system to save a subtask to the database including its ID
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(DelicateCoroutinesApi::class)
fun saveSubToDatabase(subTaskId: Int,taskId: Int, title: String, description:String, goalTime:Long, complete:Int, subTaskViewModel: SubTaskViewModel){
    GlobalScope.launch {
        val completeTime = if (complete == 1) System.currentTimeMillis() else 0
        subTaskViewModel.upsertSubTask(
            SubTask(
                subTaskId = subTaskId,
                taskId = taskId,
                title = title,
                description = description,
                goalTime = goalTime,
                completeTime = completeTime,
            )
        )
    }
}