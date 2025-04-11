package com.example.collegeeventplanner.ui.screens.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeeventplanner.domain.model.Event
import com.example.collegeeventplanner.domain.repository.EventRepository
import com.example.collegeeventplanner.domain.repository.RegistrationRepository
import com.example.collegeeventplanner.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventRegistrationViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val registrationRepository: RegistrationRepository,
    private val qrCodeGenerator: QrCodeGenerator
) : ViewModel() {

    private val _state = MutableStateFlow(EventRegistrationState())
    val state = _state.asStateFlow()

    fun loadEventDetails(eventId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = eventRepository.getEventById(eventId)) {
                is Resource.Success -> {
                    val event = result.data ?: Event.empty()
                    _state.value = _state.value.copy(
                        eventId = eventId,
                        eventTitle = event.title,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to load event details"
                    )
                }
            }
        }
    }

    fun onNameChanged(name: String) {
        _state.value = _state.value.copy(
            name = name,
            nameError = if (name.isBlank()) "Name cannot be empty" else null
        )
        validateForm()
    }

    fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(
            email = email,
            emailError = if (email.isBlank()) "Email cannot be empty" else null
        )
        validateForm()
    }

    fun onStudentIdChanged(studentId: String) {
        _state.value = _state.value.copy(
            studentId = studentId,
            studentIdError = if (studentId.isBlank()) "Student ID cannot be empty" else null
        )
        validateForm()
    }

    fun registerForEvent() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = registrationRepository.registerForEvent(
                eventId = state.value.eventId,
                name = state.value.name,
                email = state.value.email,
                studentId = state.value.studentId
            )) {
                is Resource.Success -> {
                    val registrationId = result.data ?: ""
                    val qrCodeData = generateQrCodeData(state.value.eventId, registrationId)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isRegistrationComplete = true,
                        qrCodeBitmap = qrCodeGenerator.generateQrCodeBitmap(qrCodeData),
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Registration failed"
                    )
                }
            }
        }
    }

    private fun generateQrCodeData(eventId: String, registrationId: String): String {
        return "event:$eventId|reg:$registrationId|timestamp:${System.currentTimeMillis()}"
    }

    private fun validateForm() {
        _state.value = _state.value.copy(
            isFormValid = state.value.name.isNotBlank() &&
                    state.value.email.isNotBlank() &&
                    state.value.studentId.isNotBlank()
        )
    }
}

data class EventRegistrationState(
    val eventId: String = "",
    val eventTitle: String = "",
    val name: String = "",
    val email: String = "",
    val studentId: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val studentIdError: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationComplete: Boolean = false,
    val qrCodeBitmap: Any? = null
)