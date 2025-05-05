package com.sios.tech.plantreminderapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sios.tech.plantreminderapp.domain.model.Plant
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PlantItem(
    plant: Plant,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleMedium
            )
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
