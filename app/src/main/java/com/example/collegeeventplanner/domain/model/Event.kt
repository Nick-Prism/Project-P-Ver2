package com.example.collegeeventplanner.domain.model

import java.util.Date

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val date: String, // Formatted date string
    val time: String, // Formatted time string
    val location: String,
    val clubLogoUrl: String,
    val organizerId: String,
    val maxParticipants: Int? = null,
    val registrationDeadline: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    companion object {
        fun empty() = Event(
            id = "",
            title = "",
            description = "",
            date = "",
            time = "",
            location = "",
            clubLogoUrl = "",
            organizerId = ""
        )
    }
}