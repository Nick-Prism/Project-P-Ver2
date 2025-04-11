package com.example.collegeeventplanner.data.repository

import com.example.collegeeventplanner.domain.model.Event
import com.example.collegeeventplanner.domain.repository.EventRepository
import com.example.collegeeventplanner.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : EventRepository {

    private val eventsCollection = firestore.collection("events")

    override suspend fun getUpcomingEvents(): Resource<List<Event>> {
        return try {
            val snapshot = eventsCollection
                .whereGreaterThanOrEqualTo("date", getCurrentDate())
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

    private fun getCurrentDate(): String {
        return Date().toString() // TODO: Format properly
    }

    // Other methods will be implemented similarly
}