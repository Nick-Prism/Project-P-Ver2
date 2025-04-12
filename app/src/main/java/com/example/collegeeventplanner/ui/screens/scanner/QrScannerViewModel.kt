package com.example.collegeeventplanner.ui.screens.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeeventplanner.domain.repository.RegistrationRepository
import com.example.collegeeventplanner.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
            _state.update { 
                it.copy(
                    isLoading = true,
                    error = null,
                    scanSuccess = false,
                    scannedData = qrCodeData
                )
            }

            validateAndProcessQrCode(qrCodeData)
        }
    }

    private suspend fun validateAndProcessQrCode(qrCodeData: String) {
        when (val validationResult = registrationRepository.validateQrCode(qrCodeData)) {
            is Resource.Success -> {
                if (validationResult.data == true) {
                    processValidQrCode(qrCodeData)
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Invalid QR code format or already scanned"
                        )
                    }
                }
            }
            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = validationResult.message ?: "QR code validation failed"
                    )
                }
            }
        }
    }

    private suspend fun processValidQrCode(qrCodeData: String) {
        try {
            val parts = qrCodeData.split("|")
            if (parts.size != 3 || !parts[1].startsWith("reg:")) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Invalid QR code format"
                    )
                }
                return
            }

            val registrationId = parts[1].substringAfter("reg:")
            when (val result = registrationRepository.markAttendance(registrationId)) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            scanSuccess = true,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Attendance marking failed"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Error processing QR code: ${e.message ?: "Unknown error"}"
                )
            }
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