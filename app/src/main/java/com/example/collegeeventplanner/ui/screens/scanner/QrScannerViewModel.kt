package com.example.collegeeventplanner.ui.screens.scanner

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
class QrScannerViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QrScannerState())
    val state = _state.asStateFlow()

    fun onQrCodeScanned(qrCodeData: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                scanSuccess = false
            )

            // Step 1: Validate QR code
            when (val validationResult = registrationRepository.validateQrCode(qrCodeData)) {
                is Resource.Success -> {
                    if (validationResult.data) {
                        // Step 2: Mark attendance if valid
                        markAttendance(qrCodeData)
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Invalid QR code"
                        )
                    }
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = validationResult.message ?: "Validation failed"
                    )
                }
            }
        }
    }

    private suspend fun markAttendance(qrCodeData: String) {
        try {
            // Parse QR code data (format: "event:eventId|reg:registrationId|timestamp:time")
            val parts = qrCodeData.split("|")
            if (parts.size != 3) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Invalid QR code format"
                )
                return
            }

            val registrationId = parts[1].substringAfter("reg:")
            when (val result = registrationRepository.markAttendance(registrationId)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        scanSuccess = true,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to mark attendance"
                    )
                }
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = "Error processing QR code: ${e.message}"
            )
        }
    }

    fun resetState() {
        _state.value = QrScannerState()
    }
}

data class QrScannerState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val scanSuccess: Boolean = false,
    val scannedData: String? = null
)