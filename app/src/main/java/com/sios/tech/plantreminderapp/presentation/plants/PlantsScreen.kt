package com.sios.tech.plantreminderapp.presentation.plants

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sios.tech.plantreminderapp.domain.model.Plant
import com.sios.tech.plantreminderapp.presentation.components.AddPlantDialog
import com.sios.tech.plantreminderapp.presentation.components.LocalNotificationService
import com.sios.tech.plantreminderapp.presentation.components.PlantItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PlantsScreen(
    viewModel: PlantsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val notificationService = LocalNotificationService.current
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedPlant by remember { mutableStateOf<Plant?>(null) }

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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.plants) { plant ->
                    PlantItem(
                        plant = plant,
                        onTestNotification = { testPlant ->
                            scope.launch {
                                try {
                                    // Trigger notification immediately for testing
                                    notificationService.showWateringNotification(
                                        plantId = testPlant.id.toInt(),
                                        plantName = testPlant.name
                                    )
                                } catch (e: Exception) {
                                    // Log the error
                                    e.printStackTrace()
                                }
                            }
                        },
                        onEditPlant = { plantToEdit ->
                            selectedPlant = plantToEdit
                            showEditDialog = true
                        },
                        onDeletePlant = { plantToDelete ->
                            viewModel.onEvent(PlantsEvent.DeletePlant(plantToDelete))
                            Toast.makeText(context, "Plant deleted", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
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
                        Toast.makeText(context, "Plant added", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (showEditDialog && selectedPlant != null) {
                AddPlantDialog(
                    initialName = selectedPlant!!.name,
                    initialSchedule = selectedPlant!!.wateringSchedule,
                    initialNotes = selectedPlant!!.notes,
                    onDismiss = { 
                        showEditDialog = false
                        selectedPlant = null
                    },
                    onConfirm = { name, schedule, notes ->
                        viewModel.onEvent(PlantsEvent.UpdatePlant(
                            plant = selectedPlant!!,
                            name = name,
                            wateringSchedule = schedule,
                            notes = notes
                        ))
                        showEditDialog = false
                        selectedPlant = null
                        Toast.makeText(context, "Plant updated", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
