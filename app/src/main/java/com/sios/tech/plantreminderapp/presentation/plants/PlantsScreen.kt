package com.sios.tech.plantreminderapp.presentation.plants

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sios.tech.plantreminderapp.domain.model.Plant
import com.sios.tech.plantreminderapp.presentation.components.AddPlantDialog
import com.sios.tech.plantreminderapp.presentation.components.LocalNotificationService
import com.sios.tech.plantreminderapp.presentation.components.PlantItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantsScreen(
    viewModel: PlantsViewModel = hiltViewModel(),
    onNavigateToWeather: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val notificationService = LocalNotificationService.current
    val currentWeather by viewModel.currentWeather.collectAsState(null)
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedPlant by remember { mutableStateOf<Plant?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Plants") },
                actions = {
                    IconButton(onClick = onNavigateToWeather) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Check Weather"
                        )
                    }
                }
            )
        },
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
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .padding(
                            top = padding.calculateTopPadding(),
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 8.dp
                        )
                ) {
                    currentWeather?.let { result ->
                        when  {
                            result.isSuccess -> {
                                viewModel.onEvent(PlantsEvent.SuccessGetLocation)

                                val weather = result.getOrNull()!!
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = weather.location,
                                            style = MaterialTheme.typography.titleLarge
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Row {

                                            Column {

                                                AsyncImage(
                                                    model = weather.iconUrl,
                                                    contentDescription = weather.condition,
                                                    modifier = Modifier.size(64.dp)
                                                )

                                                Text(
                                                    text = "${weather.temperature}°C",
                                                    style = MaterialTheme.typography.headlineLarge
                                                )

                                                Text(
                                                    text = weather.condition,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(16.dp))

                                            Row (
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "Humidity",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        text = "${weather.humidity}%",
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }

                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "Wind Speed",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        text = "${weather.windSpeed} km/h",
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            result.isFailure -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.padding(16.dp),
                                        text = "Failed to load weather: ${result.exceptionOrNull()?.message}",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            else -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Text(
                                        modifier = Modifier.padding(16.dp),
                                        text = "Loading weather...",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                LazyColumn(
                    contentPadding = PaddingValues(16.dp)
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
