package com.example.testyandexmapkit.repository

import com.example.testyandexmapkit.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationRepository {

    private val _location = MutableStateFlow<LatLng>(LatLng(0.0, 0.0))
    val location: StateFlow<LatLng> = _location

    private val _errorFlow = MutableStateFlow<List<String>>(emptyList())
    val errorFlow: Flow<List<String>> = _errorFlow


    suspend fun reportError(errorMessage: String) {
        _errorFlow.value += errorMessage
    }

    suspend fun removeError(errorMessage: String) {
        _errorFlow.value = _errorFlow.value.filterNot { it == errorMessage }
    }

    suspend fun updateLocation(newLocation: LatLng) {
        _location.value = newLocation
    }

}