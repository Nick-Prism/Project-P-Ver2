package com.example.collegeeventplanner.domain.repository

import com.example.collegeeventplanner.domain.model.Event
import com.example.collegeeventplanner.util.Resource
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun getUpcomingEvents(): Resource<List<Event>>
    suspend fun getEventById(eventId: String): Resource<Event>
    suspend fun createEvent(event: Event): Resource<String> // Returns event ID
    suspend fun updateEvent(event: Event): Resource<Unit>
    suspend fun deleteEvent(eventId: String): Resource<Unit>
    
    // Real-time updates
    fun observeEvents(): Flow<Resource<List<Event>>>
    fun observeEvent(eventId: String): Flow<Resource<Event>>
    
    // Registration specific
    suspend fun registerForEvent(eventId: String, userId: String): Resource<Unit>
    suspend fun getEventRegistrations(eventId: String): Resource<List<String>> // List of user IDs
    suspend fun getRegisteredEvents(userId: String): Resource<List<Event>>
}