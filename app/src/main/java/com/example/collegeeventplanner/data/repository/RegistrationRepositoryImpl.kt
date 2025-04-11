package com.example.collegeeventplanner.data.repository

import com.example.collegeeventplanner.domain.repository.RegistrationRepository
import com.example.collegeeventplanner.util.Resource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RegistrationRepository {

    private val registrationsCollection
        get() = firestore.collection("registrations")

    private val attendanceCollection
        get() = firestore.collection("attendance")

    override suspend fun registerForEvent(
        eventId: String,
        name: String,
        email: String,
        studentId: String
    ): Resource<String> {
        return try {
            val registrationData = hashMapOf(
                "eventId" to eventId,
                "name" to name,
                "email" to email,
                "studentId" to studentId,
                "timestamp" to System.currentTimeMillis(),
                "hasAttended" to false
            )

            val document = registrationsCollection.add(registrationData).await()
            Resource.Success(document.id)
        } catch (e: Exception) {
            Resource.Error("Registration failed: ${e.message}")
        }
    }

    override suspend fun getRegistration(registrationId: String): Resource<RegistrationRepository.Registration> {
        return try {
            val document = registrationsCollection.document(registrationId).get().await()
            document.toObject<RegistrationRepository.Registration>()?.let { registration ->
                Resource.Success(registration.copy(id = document.id))
            } ?: Resource.Error("Registration not found")
        } catch (e: Exception) {
            Resource.Error("Failed to get registration: ${e.message}")
        }
    }

    override suspend fun validateQrCode(qrCodeData: String): Resource<Boolean> {
        return try {
            // Parse QR code data and validate
            val parts = qrCodeData.split("|")
            if (parts.size != 3) return Resource.Error("Invalid QR code format")
            
            val registrationId = parts[1].substringAfter("reg:")
            val registration = getRegistration(registrationId)
            
            when (registration) {
                is Resource.Success -> {
                    if (registration.data.hasAttended) {
                        Resource.Error("Registration already scanned")
                    } else {
                        Resource.Success(true)
                    }
                }
                is Resource.Error -> registration
            }
        } catch (e: Exception) {
            Resource.Error("QR validation failed: ${e.message}")
        }
    }

    override suspend fun markAttendance(registrationId: String): Resource<Unit> {
        return try {
            registrationsCollection.document(registrationId)
                .update("hasAttended", true)
                .await()
            
            attendanceCollection.document(registrationId)
                .set(hashMapOf(
                    "registrationId" to registrationId,
                    "timestamp" to FieldValue.serverTimestamp()
                ))
                .await()
                
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to mark attendance: ${e.message}")
        }
    }

    override fun observeRegistrations(eventId: String): Flow<Resource<List<RegistrationRepository.Registration>>> = callbackFlow {
        val listener = registrationsCollection
            .whereEqualTo("eventId", eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error listening to registrations"))
                    return@addSnapshotListener
                }

                val registrations = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject<RegistrationRepository.Registration>()?.copy(id = doc.id)
                } ?: emptyList()
                trySend(Resource.Success(registrations))
            }

        awaitClose { listener.remove() }
    }

    override fun observeAttendance(eventId: String): Flow<Resource<Int>> = callbackFlow {
        val listener = registrationsCollection
            .whereEqualTo("eventId", eventId)
            .whereEqualTo("hasAttended", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error listening to attendance"))
                    return@addSnapshotListener
                }

                val count = snapshot?.size() ?: 0
                trySend(Resource.Success(count))
            }

        awaitClose { listener.remove() }
    }
}