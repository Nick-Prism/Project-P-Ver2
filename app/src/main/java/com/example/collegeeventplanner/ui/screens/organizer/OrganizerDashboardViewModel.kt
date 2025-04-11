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
class OrganizerDashboardViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrganizerDashboardState())
    val state = _state.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = eventRepository.getEventsByOrganizer()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        events = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to load events"
                    )
                }
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = eventRepository.deleteEvent(eventId)) {
                is Resource.Success -> {
                    // Refresh the list after successful deletion
                    loadEvents()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to delete event"
                    )
                }
            }
        }
    }

    fun refreshEvents() {
        loadEvents()
    }
}

data class OrganizerDashboardState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val eventToDelete: String? = null
)