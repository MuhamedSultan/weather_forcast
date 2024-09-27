package com.example.weatherforcast.db

import com.example.weatherforcast.pojo.alerts.Alerts
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun addLocationToFavourite(weatherResponse: WeatherResponse)
    suspend fun getFavouritePlaces(): Flow<List<WeatherResponse>>
    suspend fun deleteLocationToFavourite(weatherResponse: WeatherResponse)
    suspend fun addAlert(alert: Alerts)
    suspend fun getAlert(): Flow<List<Alerts>>
    suspend fun deleteAlert(alert: Alerts)
}