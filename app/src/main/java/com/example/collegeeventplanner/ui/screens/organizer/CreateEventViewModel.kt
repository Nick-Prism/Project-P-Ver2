package com.example.collegeeventplanner.ui.screens.organizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegeeventplanner.domain.model.Event
import com.example.collegeeventplanner.domain.repository.EventRepository
import com.example.collegeeventplanner.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateEventState())
    val state = _state.asStateFlow()

    fun onTitleChanged(title: String) {
        _state.value = _state.value.copy(
            title = title,
            titleError = if (title.isBlank()) "Title cannot be empty" else null
        )
        validateForm()
    }

    fun onDescriptionChanged(description: String) {
        _state.value = _state.value.copy(
            description = description,
            descriptionError = if (description.isBlank()) "Description cannot be empty" else null
        )
        validateForm()
    }

    fun onDateChanged(date: String) {
        _state.value = _state.value.copy(
            date = date,
            dateError = if (date.isBlank()) "Date cannot be empty" else null
        )
        validateForm()
    }

    fun onTimeChanged(time: String) {
        _state.value = _state.value.copy(
            time = time,
            timeError = if (time.isBlank()) "Time cannot be empty" else null
        )
        validateForm()
    }

    fun onLocationChanged(location: String) {
        _state.value = _state.value.copy(
            location = location,
            locationError = if (location.isBlank()) "Location cannot be empty" else null
        )
        validateForm()
    }

    fun onMaxParticipantsChanged(maxParticipants: Int?) {
        _state.value = _state.value.copy(maxParticipants = maxParticipants)
    }

    fun onRegistrationDeadlineChanged(deadline: String) {
        _state.value = _state.value.copy(registrationDeadline = deadline)
    }

    fun createEvent() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val event = Event(
                title = state.value.title,
                description = state.value.description,
                date = state.value.date,
                time = state.value.time,
                location = state.value.location,
                maxParticipants = state.value.maxParticipants,
                registrationDeadline = state.value.registrationDeadline
            )

            when (val result = eventRepository.createEvent(event)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isEventCreated = true,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to create event"
                    )
                }
            }
        }
    }

    private fun validateForm() {
        _state.value = _state.value.copy(
            isFormValid = state.value.title.isNotBlank() &&
                    state.value.description.isNotBlank() &&
                    state.value.date.isNotBlank() &&
                    state.value.time.isNotBlank() &&
                    state.value.location.isNotBlank()
        )
    }
}

data class CreateEventState(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val maxParticipants: Int? = null,
    val registrationDeadline: String? = null,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null,
    val locationError: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEventCreated: Boolean = false
)