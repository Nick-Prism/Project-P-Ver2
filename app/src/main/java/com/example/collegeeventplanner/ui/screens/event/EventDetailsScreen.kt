package com.example.collegeeventplanner.ui.screens.event

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.collegeeventplanner.ui.components.EventCard
import com.example.collegeeventplanner.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    navController: NavController,
    viewModel: EventDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadEventDetails()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.isOrganizer) {
                FloatingActionButton(
                    onClick = { navController.navigate("editEvent/${state.event.id}") },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Event")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                EventCard(
                    event = state.event,
                    onCardClick = {},
                    showOrganizerActions = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = state.event.description,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Date & Time",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${DateTimeUtils.formatDateForDisplay(state.event.date)} at ${DateTimeUtils.formatTimeForDisplay(state.event.time)}",
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = state.event.location,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (state.event.maxParticipants != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Max Participants",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = state.event.maxParticipants.toString(),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}