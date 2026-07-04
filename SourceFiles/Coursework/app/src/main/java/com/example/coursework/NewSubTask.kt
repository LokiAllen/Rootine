
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
import androidx.compose.material.icons.filled.ArrowBack
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

//NewSubTask composable is used to create a new subtask, the taskId is from the parent task
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSubTask(navController: NavController,taskIdString: String, taskViewModel: TaskViewModel = viewModel(),subTaskViewModel: SubTaskViewModel = viewModel()) {
    // Initialize all variables that will be used to store the data for the new task and load data from the viewmodels
    val taskId = taskIdString.toInt()
    val task by taskViewModel.getTaskById(taskId).observeAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Initialize importance and stage with null values initially
    var importance by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()
    var datePickerDialogOpen by remember { mutableStateOf(false) }

    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    val hours = Array(24) { it.toString().padStart(2, '0') } // "00" to "23"
    val minutes = Array(60) { it.toString().padStart(2, '0') } // "00" to "59"
    var selectedDateText by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Task Information", style = MaterialTheme.typography.titleLarge)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                // This button will bring the user back the the list of subtasks
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
                // Only show the Time pickers if a date is entered
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

                        // Perform different operations based on if goal time is specified
                        if (hourInt == null || minuteInt == null) {
                            saveNewSubTaskToDatabase(taskId,title,description,0, subTaskViewModel)
                        } else {
                            // Safe to proceed with valid hour and minute
                            calendar.set(Calendar.HOUR_OF_DAY, hourInt)
                            calendar.set(Calendar.MINUTE, minuteInt)
                            val goalTimeLong = calendar.timeInMillis
                            saveNewSubTaskToDatabase(taskId,title,description,goalTimeLong, subTaskViewModel)
                        }
                        navController.navigate("TasksSubTasks/$taskId")
                    }
                ) {
                    Text("Save SubTask")
                }
                Spacer(modifier = Modifier.width(8.dp)) // Adds space between the two buttons
            }
        }
    )
}

// Save a subtask, do not specify the taskID which will create a new subTask

@OptIn(DelicateCoroutinesApi::class)
fun saveNewSubTaskToDatabase(taskId: Int,title: String, description:String, goalTime:Long, subTaskViewModel: SubTaskViewModel){
    GlobalScope.launch {
        subTaskViewModel.upsertSubTask(
            SubTask(
                taskId = taskId,
                title = title,
                description = description,
                goalTime = goalTime,
                completeTime = 0
            )
        )
    }
}
