package com.example.testyandexmapkit.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testyandexmapkit.mapToLatLng
import com.example.testyandexmapkit.model.LatLng
import com.example.testyandexmapkit.repository.LocationRepository
import com.yandex.mapkit.location.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

data class LocationViewState(
    val isLoading: Boolean = true,
    val location: LatLng? = null,
    val routePoints: List<LatLng> = emptyList(),
    val error: String? = null
)

sealed class LocationEvent {
    data object StartTracking : LocationEvent()
    data object LoadRoutePoints: LocationEvent()
    data class LocationUpdated(val location: Location) : LocationEvent()
    data class LocationError(val error: String) : LocationEvent()
}

class LocationViewModel(
    private val locationRepository: LocationRepository
) : ViewModel(), KoinComponent {

    private val _viewState = MutableStateFlow(LocationViewState())
    val viewState: StateFlow<LocationViewState> = _viewState

    fun obtainEvent(event: LocationEvent) {
        when (event) {
            is LocationEvent.StartTracking -> startTracking()
            is LocationEvent.LocationUpdated -> updateLocation(event.location)
            is LocationEvent.LocationError -> showError(event.error)
            LocationEvent.LoadRoutePoints -> loadRoutePoints()
        }
    }

    private fun loadRoutePoints() {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(
                routePoints = listOf(
                    LatLng(37.423719, -122.089975),
                    LatLng(37.421602, -122.087106),
                    LatLng(37.423522, -122.087649)
                )
            )
        }
    }

    private fun startTracking() {
        viewModelScope.launch {
            locationRepository.location.collect {
                _viewState.value = _viewState.value.copy(
                    location = it
                )
            }
        }
    }

    private fun updateLocation(location: Location) {
        _viewState.value = LocationViewState(isLoading = false, location = location.mapToLatLng())
    }

    private fun showError(error: String) {
        _viewState.value = LocationViewState(isLoading = false, error = error)
    }

    override fun onCleared() {
        super.onCleared()
    }

}