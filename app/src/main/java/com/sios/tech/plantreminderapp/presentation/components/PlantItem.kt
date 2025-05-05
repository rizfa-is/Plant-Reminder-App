package com.sios.tech.plantreminderapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sios.tech.plantreminderapp.domain.model.Plant
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PlantItem(
    plant: Plant,
    onTestNotification: (Plant) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Box {
                    // Debug button
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Debug Options")
                    }
                    
                    // Debug menu
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Test Notification") },
                            onClick = {
                                onTestNotification(plant)
                                showMenu = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Next watering: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(plant.wateringSchedule)}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (plant.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plant.notes,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
