package com.example.weatherforcast.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class MapsViewModel : ViewModel() {

    private val _searchQueryFlow = MutableSharedFlow<LatLng>()
    val searchQueryFlow: SharedFlow<LatLng> = _searchQueryFlow

    suspend fun emitSearchLocation(latLng: LatLng) {
        _searchQueryFlow.emit(latLng)
    }
}
