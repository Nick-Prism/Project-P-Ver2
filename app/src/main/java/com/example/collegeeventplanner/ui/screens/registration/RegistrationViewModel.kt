package com.example.collegeeventplanner.ui.screens.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeeventplanner.domain.repository.RegistrationRepository
import com.example.collegeeventplanner.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository,
    private val qrCodeGenerator: QrCodeGenerator
) : ViewModel() {

    private val _state = MutableStateFlow(RegistrationState())
    val state = _state.asStateFlow()

    fun onNameChanged(name: String) {
        _state.value = _state.value.copy(name = name)
        validateForm()
    }

    fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email, emailError = null)
        validateForm()
    }

    fun onStudentIdChanged(studentId: String) {
        _state.value = _state.value.copy(studentId = studentId)
        validateForm()
    }

    fun register() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val result = registrationRepository.registerForEvent(
                eventId = state.value.eventId,
                name = state.value.name,
                email = state.value.email,
                studentId = state.value.studentId
            )

            when (result) {
                is Resource.Success -> {
                    val qrCodeData = generateQrCodeData(
                        eventId = state.value.eventId,
                        registrationId = result.data ?: ""
                    )
                    _state.value = _state.value.copy(
                        isLoading = false,
                        qrCodeBitmap = qrCodeGenerator.generateQrCodeBitmap(qrCodeData),
                        isRegistrationComplete = true
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun generateQrCodeData(eventId: String, registrationId: String): String {
        return "event:$eventId|reg:$registrationId|timestamp:${System.currentTimeMillis()}"
    }

    private fun validateForm() {
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(state.value.email).matches()
        _state.value = _state.value.copy(
            isFormValid = state.value.name.isNotBlank() &&
                    state.value.email.isNotBlank() &&
                    state.value.studentId.isNotBlank() &&
                    emailValid,
            emailError = if (state.value.email.isNotBlank() && !emailValid) {
                "Invalid email format"
            } else null
        )
    }
}

data class RegistrationState(
    val eventId: String = "",
    val name: String = "",
    val email: String = "",
    val studentId: String = "",
    val emailError: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationComplete: Boolean = false,
    val qrCodeBitmap: Any? = null
)