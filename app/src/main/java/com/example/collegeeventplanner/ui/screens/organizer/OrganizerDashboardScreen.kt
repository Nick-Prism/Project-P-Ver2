package com.example.collegeeventplanner.ui.screens.organizer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.collegeeventplanner.ui.components.AppTopBar
import com.example.collegeeventplanner.ui.components.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerDashboardScreen(
    navController: NavController,
    viewModel: OrganizerDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Organizer Dashboard",
                navController = navController,
                showBackButton = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("createEvent") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Event"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Events",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                state.error != null -> {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                state.events.isEmpty() -> {
                    Text(
                        text = "No events found",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.events) { event ->
                            EventCard(
                                event = event,
                                onCardClick = { 
                                    navController.navigate("eventDetails/${event.id}") 
                                },
                                showOrganizerActions = true,
                                onEditClick = { 
                                    navController.navigate("editEvent/${event.id}") 
                                },
                                onDeleteClick = { viewModel.deleteEvent(event.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}