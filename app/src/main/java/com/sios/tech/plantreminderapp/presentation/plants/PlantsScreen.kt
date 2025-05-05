package com.sios.tech.plantreminderapp.presentation.plants

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sios.tech.plantreminderapp.presentation.components.PlantItem
import java.util.Calendar
import java.util.Date

@Composable
fun PlantsScreen(
    viewModel: PlantsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.plants) { plant ->
                    PlantItem(plant = plant)
                }
            }

            if (showAddDialog) {
                AddPlantDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { name, schedule, notes ->
                        viewModel.onEvent(PlantsEvent.AddPlant(
                            name = name,
                            wateringSchedule = schedule,
                            notes = notes
                        ))
                        showAddDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AddPlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Date, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Plant") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Plant Name") }
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            name,
                            Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 7) }.time, // Default schedule
                            notes
                        )
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
