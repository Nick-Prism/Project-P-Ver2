package com.example.collegeeventplanner.navigation

import androidx.annotation.StringRes
import com.example.collegeeventplanner.R

sealed class Screens(val route: String, @StringRes val title: Int) {
    object Home : Screens("home", R.string.app_name)
    object EventDetails : Screens("eventDetails/{eventId}", R.string.event_details) {
        fun createRoute(eventId: String) = "eventDetails/$eventId"
    }
    object EventRegistration : Screens("eventRegistration/{eventId}", R.string.event_registration) {
        fun createRoute(eventId: String) = "eventRegistration/$eventId"
    }
    object RegistrationConfirmation : Screens(
        "registrationConfirmation/{eventTitle}",
        R.string.registration_confirmation
    ) {
        fun createRoute(eventTitle: String) = "registrationConfirmation/$eventTitle"
    }
    object OrganizerDashboard : Screens("organizerDashboard", R.string.organizer_dashboard)
    object CreateEvent : Screens("createEvent", R.string.create_event)
    object EditEvent : Screens("editEvent/{eventId}", R.string.edit_event) {
        fun createRoute(eventId: String) = "editEvent/$eventId"
    }
    object QrScanner : Screens("qrScanner", R.string.qr_scanner)
}