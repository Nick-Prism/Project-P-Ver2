package com.example.collegeeventplanner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.collegeeventplanner.util.formatDate
import com.example.collegeeventplanner.util.formatTime
import java.util.*

@Composable
fun FormDateTimePicker(
    dateLabel: String,
    timeLabel: String,
    selectedDate: String,
    selectedTime: String,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            label = { Text(dateLabel) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Select date")
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = selectedTime,
            onValueChange = {},
            label = { Text(timeLabel) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(Icons.Default.Schedule, contentDescription = "Select time")
                }
            }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // In a real implementation, you would get the selected date
                            // from the date picker component
                            val today = Calendar.getInstance()
                            onDateSelected(formatDate(today.time))
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                // Date picker component would be implemented here
                // For now using a placeholder
                Text("Date Picker Placeholder")
            }
        }

        if (showTimePicker) {
            TimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // In a real implementation, you would get the selected time
                            // from the time picker component
                            val now = Calendar.getInstance()
                            onTimeSelected(formatTime(now.time))
                            showTimePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                // Time picker component would be implemented here
                // For now using a placeholder
                Text("Time Picker Placeholder")
            }
        }
    }
}