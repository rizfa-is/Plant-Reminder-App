package com.sios.tech.plantreminderapp.presentation.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantDialog(
    initialName: String = "",
    initialSchedule: Date = Date(),
    initialNotes: String = "",
    onDismiss: () -> Unit,
    onConfirm: (name: String, schedule: Date, notes: String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(initialName) }
    var notes by rememberSaveable { mutableStateOf(initialNotes) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val calendar = remember { Calendar.getInstance().apply { time = initialSchedule } }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialName.isEmpty()) "Add Plant" else "Edit Plant") },
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
                    label = { Text("Plant Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Watering Date")
                }

                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Watering Time")
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = { showDatePicker = false }
                            ) {
                                Text("OK")
                            }
                        }
                    ) {
                        DatePicker(
                            state = rememberDatePickerState(
                                initialSelectedDateMillis = calendar.timeInMillis
                            ).apply {
                                selectedDateMillis?.let { millis ->
                                    val newCalendar = Calendar.getInstance().apply {
                                        timeInMillis = millis
                                    }
                                    calendar.set(Calendar.YEAR, newCalendar.get(Calendar.YEAR))
                                    calendar.set(Calendar.MONTH, newCalendar.get(Calendar.MONTH))
                                    calendar.set(Calendar.DAY_OF_MONTH, newCalendar.get(Calendar.DAY_OF_MONTH))
                                }
                            },
                            showModeToggle = false,
                            title = { Text("Select Watering Date") }
                        )
                    }
                }

                if (showTimePicker) {
                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        title = { Text("Select Time") },
                        text = {
                            TimePicker(
                                state = rememberTimePickerState(
                                    initialHour = calendar.get(Calendar.HOUR_OF_DAY),
                                    initialMinute = calendar.get(Calendar.MINUTE)
                                ).apply {
                                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                                    calendar.set(Calendar.MINUTE, minute)
                                },
                                modifier = Modifier.padding(16.dp)
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = { showTimePicker = false }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showTimePicker = false }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, calendar.time, notes)
                    }
                }
            ) {
                Text(if (initialName.isEmpty()) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
