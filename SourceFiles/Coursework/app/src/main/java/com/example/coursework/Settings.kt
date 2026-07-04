package com.example.coursework

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coursework.database.task.TaskViewModel


// The settings page allows the user to toggle between light and darkmode within the app
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Settings(navController: NavController,context: Context = LocalContext.current) {

    // State for Dark Mode toggle
    var isDarkModeEnabled by remember { mutableStateOf(ThemePreference.isDarkModeEnabled(context)) }

    val activity = (LocalContext.current as? Activity)

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center
            ) {
                // Toggle for Dark Mode
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Dark Mode", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            isDarkModeEnabled = it
                            // Save the preference when changed
                            ThemePreference.setDarkModeEnabled(context, isDarkModeEnabled)
                            // Seamlessly restart the app with the changes saved
                            activity?.let {
                                it.finish()
                                val restartIntent = it.intent
                                it.startActivity(restartIntent)
                                it.overridePendingTransition(0, 0)
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    )
}
