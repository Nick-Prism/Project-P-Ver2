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
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditEventState())
    val state = _state.asStateFlow()

    fun loadEventData(eventId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, eventId = eventId)
            when (val result = eventRepository.getEventById(eventId)) {
                is Resource.Success -> {
                    val event = result.data ?: Event.empty()
                    _state.value = _state.value.copy(
                        title = event.title,
                        description = event.description,
                        date = event.date,
                        time = event.time,
                        location = event.location,
                        maxParticipants = event.maxParticipants,
                        registrationDeadline = event.registrationDeadline,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to load event data"
                    )
                }
            }
        }
    }

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
        _state.value = _state.value.copy(registrationDeadline = deadline.ifBlank { null })
    }

    fun updateEvent() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val updatedEvent = Event(
                id = state.value.eventId,
                title = state.value.title,
                description = state.value.description,
                date = state.value.date,
                time = state.value.time,
                location = state.value.location,
                maxParticipants = state.value.maxParticipants,
                registrationDeadline = state.value.registrationDeadline
            )

            when (val result = eventRepository.updateEvent(updatedEvent)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isEventUpdated = true,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to update event"
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

data class EditEventState(
    val eventId: String = "",
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
    val isEventUpdated: Boolean = false
)