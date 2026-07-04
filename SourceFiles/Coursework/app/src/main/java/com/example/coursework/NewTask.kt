
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coursework.database.task.TaskViewModel
import com.example.coursework.database.task.Task
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursework.saveSubToDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// The NewTask composable is used to create a brand new activity
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTask(navController: NavController, taskViewModel: TaskViewModel = viewModel()) {

    // Initialize all variables that will be used to store the data for the new task
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Initialize importance and stage with null values initially
    var importance by remember { mutableStateOf("Low") }
    var stage by remember { mutableStateOf("NotStarted") }

    val datePickerState = rememberDatePickerState()
    var datePickerDialogOpen by remember { mutableStateOf(false) }
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    val hours = Array(24) { it.toString().padStart(2, '0') } // "00" to "23"
    val minutes = Array(60) { it.toString().padStart(2, '0') } // "00" to "59"
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
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("HomeScreen") }) {
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Allow the page to scroll to accommodate multiple screen sizes
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
                // Initialize variables to store the state of the dropdown lists
                var importanceExpanded by remember { mutableStateOf(false) }
                var stageExpanded by remember { mutableStateOf(false) }

                // Create dropdowns for the user to select the importance and the stage of the task
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
                // Create a date picker and Time picker for the user to specify the date and time that the task is due
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
        // Create a bottom bar to store the Save button

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
                            saveNewToDatabase(title,description,0,importances.indexOf(importance),stages.indexOf(stage), taskViewModel)
                        } else {
                            calendar.set(Calendar.HOUR_OF_DAY, hourInt)
                            calendar.set(Calendar.MINUTE, minuteInt)
                        val goalTimeLong = calendar.timeInMillis
                        saveNewToDatabase(title,description,goalTimeLong,importances.indexOf(importance),stages.indexOf(stage), taskViewModel)
                        }
                        navController.navigate("HomeScreen")
                    }) {
                    Text("Save Task")
                }
                Spacer(modifier = Modifier.width(8.dp)) // Adds space between the two buttons
            }
        }
    )
}

// The saveNewToDatabase function allows the user to save a function to the database without inputting a primary key
@OptIn(DelicateCoroutinesApi::class)
fun saveNewToDatabase(title: String, description:String, goalTime:Long, importance:Int, stage:Int, taskViewModel: TaskViewModel){
    GlobalScope.launch {
        taskViewModel.upsertTask(
            Task(
                title = title,
                description = description,
                goalTime = goalTime,
                completeTime = 0,
                importance = importance,
                stage = stage
            )
        )
    }
}
