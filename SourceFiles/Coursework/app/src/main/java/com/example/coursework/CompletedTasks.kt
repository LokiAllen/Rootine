package com.example.coursework

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
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

// The completedTasks composable shows all tasks that have been completed
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompletedTasks(navController: NavController, taskViewModel: TaskViewModel = viewModel()) {
    // Observing LiveData from the ViewModel
    val tasks = taskViewModel.getComplete.observeAsState(listOf()).value
    // Define the structure of the page
    Scaffold (
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Completed Tasks", style = MaterialTheme.typography.headlineMedium)
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
                        navController.navigate("com.example.coursework.TaskInfo/${tasks[index].taskId}")
                    },
                    colourImportance = tasks[index].importance
                )
            }
        }
    }
}