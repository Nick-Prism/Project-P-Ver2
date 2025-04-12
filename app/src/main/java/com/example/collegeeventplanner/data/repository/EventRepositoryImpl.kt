package com.example.collegeeventplanner.data.repository

import com.example.collegeeventplanner.domain.model.Event
import com.example.collegeeventplanner.domain.repository.EventRepository
import com.example.collegeeventplanner.util.Resource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : EventRepository {

    private val eventsCollection = firestore.collection("events")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override suspend fun getUpcomingEvents(): Resource<List<Event>> {
        return try {
            val currentDate = dateFormat.format(Date())
            val snapshot = eventsCollection
                .whereGreaterThanOrEqualTo("date", currentDate)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .await()
            
            val events = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Event>()?.copy(id = doc.id)
            }
            Resource.Success(events)
        } catch (e: Exception) {
            Resource.Error("Failed to fetch events: ${e.message}", e)
        }
    }

    override fun observeEvents(): Flow<Resource<List<Event>>> = callbackFlow {
        val listener = eventsCollection
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error listening to events"))
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<Event>()?.copy(id = doc.id)
                } ?: emptyList()
                trySend(Resource.Success(events))
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getEventById(eventId: String): Resource<Event> {
        return try {
            val document = eventsCollection.document(eventId).get().await()
            document.toObject<Event>()?.let {
                Resource.Success(it.copy(id = document.id))
            } ?: Resource.Error("Event not found")
        } catch (e: Exception) {
            Resource.Error("Failed to get event: ${e.message}", e)
        }
    }

    override suspend fun createEvent(event: Event): Resource<String> {
        return try {
            val document = eventsCollection.add(event).await()
            Resource.Success(document.id)
        } catch (e: Exception) {
            Resource.Error("Failed to create event: ${e.message}", e)
        }
    }

    override suspend fun updateEvent(event: Event): Resource<Unit> {
        return try {
            require(event.id.isNotEmpty()) { "Event ID cannot be empty" }
            eventsCollection.document(event.id)
                .set(event.copy(updatedAt = Date()))
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to update event: ${e.message}", e)
        }
    }

    override suspend fun deleteEvent(eventId: String): Resource<Unit> {
        return try {
            eventsCollection.document(eventId).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to delete event: ${e.message}", e)
        }
    }

    override fun observeEvent(eventId: String): Flow<Resource<Event>> = callbackFlow {
        val listener = eventsCollection.document(eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error listening to event"))
                    return@addSnapshotListener
                }

                snapshot?.toObject<Event>()?.let { event ->
                    trySend(Resource.Success(event.copy(id = snapshot.id)))
                } ?: trySend(Resource.Error("Event not found"))
            }

        awaitClose { listener.remove() }
    }

    override suspend fun registerForEvent(eventId: String, userId: String): Resource<Unit> {
        return try {
            eventsCollection.document(eventId)
                .update("participants", FieldValue.arrayUnion(userId))
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to register for event: ${e.message}", e)
        }
    }

    override suspend fun getEventRegistrations(eventId: String): Resource<List<String>> {
        return try {
            val document = eventsCollection.document(eventId).get().await()
            val participants = document.get("participants") as? List<String> ?: emptyList()
            Resource.Success(participants)
        } catch (e: Exception) {
            Resource.Error("Failed to get registrations: ${e.message}", e)
        }
    }

    override suspend fun getRegisteredEvents(userId: String): Resource<List<Event>> {
        return try {
            val snapshot = eventsCollection
                .whereArrayContains("participants", userId)
                .get()
                .await()
            
            val events = snapshot.documents.mapNotNull { doc ->
                doc.toObject<Event>()?.copy(id = doc.id)
            }
            Resource.Success(events)
        } catch (e: Exception) {
            Resource.Error("Failed to fetch registered events: ${e.message}", e)
        }
    }
}