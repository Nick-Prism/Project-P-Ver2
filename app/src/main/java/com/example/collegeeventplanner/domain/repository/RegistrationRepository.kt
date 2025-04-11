package com.example.collegeeventplanner.domain.repository

import com.example.collegeeventplanner.util.Resource
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
    suspend fun registerForEvent(
        eventId: String,
        name: String,
        email: String,
        studentId: String
    ): Resource<String> // Returns registration ID

    suspend fun getRegistration(registrationId: String): Resource<Registration>
    suspend fun validateQrCode(qrCodeData: String): Resource<Boolean>
    suspend fun markAttendance(registrationId: String): Resource<Unit>
    
    fun observeRegistrations(eventId: String): Flow<Resource<List<Registration>>>
    fun observeAttendance(eventId: String): Flow<Resource<Int>> // Count of attendees

    data class Registration(
        val id: String,
        val eventId: String,
        val userId: String?,
        val name: String,
        val email: String,
        val studentId: String,
        val qrCodeData: String,
        val timestamp: Long,
        val hasAttended: Boolean = false
    ) {
        companion object {
            fun empty() = Registration(
                id = "",
                eventId = "",
                userId = null,
                name = "",
                email = "",
                studentId = "",
                qrCodeData = "",
                timestamp = 0L
            )
        }
    }
}