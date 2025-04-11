package com.example.collegeeventplanner.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.collegeeventplanner.ui.screens.event.EventDetailsScreen
import com.example.collegeeventplanner.ui.screens.home.HomeScreen
import com.example.collegeeventplanner.ui.screens.organizer.CreateEventScreen
import com.example.collegeeventplanner.ui.screens.organizer.EditEventScreen
import com.example.collegeeventplanner.ui.screens.organizer.OrganizerDashboardScreen
import com.example.collegeeventplanner.ui.screens.registration.EventRegistrationScreen
import com.example.collegeeventplanner.ui.screens.registration.RegistrationConfirmationScreen
import com.example.collegeeventplanner.ui.screens.scanner.QrScannerScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route
    ) {
        composable(Screens.Home.route) {
            HomeScreen(navController)
        }
        composable(Screens.EventDetails.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailsScreen(navController, eventId)
        }
        composable(Screens.EventRegistration.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventRegistrationScreen(navController, eventId)
        }
        composable(Screens.RegistrationConfirmation.route) { backStackEntry ->
            val eventTitle = backStackEntry.arguments?.getString("eventTitle") ?: ""
            RegistrationConfirmationScreen(navController, eventTitle)
        }
        composable(Screens.OrganizerDashboard.route) {
            OrganizerDashboardScreen(navController)
        }
        composable(Screens.CreateEvent.route) {
            CreateEventScreen(navController)
        }
        composable(Screens.EditEvent.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EditEventScreen(navController, eventId)
        }
        composable(Screens.QrScanner.route) {
            QrScannerScreen(navController)
        }
    }
}