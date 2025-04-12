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
            require(eventId.isNotEmpty()) { "Event ID cannot be empty" }
            require(name.isNotBlank()) { "Name cannot be blank" }
            require(email.isNotBlank()) { "Email cannot be blank" }
            require(studentId.isNotBlank()) { "Student ID cannot be blank" }

            val qrCodeData = generateQrCodeData(eventId, studentId)
            val registrationData = hashMapOf(
                "eventId" to eventId,
                "name" to name,
                "email" to email,
                "studentId" to studentId,
                "qrCodeData" to qrCodeData,
                "timestamp" to FieldValue.serverTimestamp(),
                "hasAttended" to false
            )

            val document = registrationsCollection.add(registrationData).await()
            Resource.Success(document.id)
        } catch (e: IllegalArgumentException) {
            Resource.Error(e.message ?: "Invalid registration data")
        } catch (e: Exception) {
            Resource.Error("Registration failed: ${e.message}", e)
        }
    }

    override suspend fun getRegistration(registrationId: String): Resource<RegistrationRepository.Registration> {
        return try {
            require(registrationId.isNotEmpty()) { "Registration ID cannot be empty" }
            
            val document = registrationsCollection.document(registrationId).get().await()
            document.toObject<RegistrationRepository.Registration>()?.let { registration ->
                Resource.Success(registration.copy(id = document.id))
            } ?: Resource.Error("Registration not found")
        } catch (e: Exception) {
            Resource.Error("Failed to get registration: ${e.message}", e)
        }
    }

    override suspend fun validateQrCode(qrCodeData: String): Resource<Boolean> {
        return try {
            val parts = qrCodeData.split("|")
            if (parts.size != 3 || !parts[0].startsWith("event:") || !parts[1].startsWith("reg:")) {
                return Resource.Error("Invalid QR code format")
            }
            
            val registrationId = parts[1].substringAfter("reg:")
            when (val registration = getRegistration(registrationId)) {
                is Resource.Success -> {
                    if (registration.data.hasAttended) {
                        Resource.Error("Registration already scanned")
                    } else if (registration.data.qrCodeData != qrCodeData) {
                        Resource.Error("QR code doesn't match registration")
                    } else {
                        Resource.Success(true)
                    }
                }
                is Resource.Error -> registration
            }
        } catch (e: Exception) {
            Resource.Error("QR validation failed: ${e.message}", e)
        }
    }

    override suspend fun markAttendance(registrationId: String): Resource<Unit> {
        return try {
            require(registrationId.isNotEmpty()) { "Registration ID cannot be empty" }
            
            registrationsCollection.document(registrationId)
                .update(mapOf(
                    "hasAttended" to true,
                    "attendedAt" to FieldValue.serverTimestamp()
                ))
                .await()
            
            attendanceCollection.document(registrationId)
                .set(mapOf(
                    "registrationId" to registrationId,
                    "timestamp" to FieldValue.serverTimestamp()
                ))
                .await()
                
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Failed to mark attendance: ${e.message}", e)
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

                trySend(Resource.Success(snapshot?.size() ?: 0))
            }

        awaitClose { listener.remove() }
    }

    private fun generateQrCodeData(eventId: String, studentId: String): String {
        return "event:$eventId|reg:$studentId|${System.currentTimeMillis()}"
    }
}