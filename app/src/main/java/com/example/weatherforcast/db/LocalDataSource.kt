package com.example.weatherforcast.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforcast.pojo.alerts.Alerts
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun addCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun addDaysWeather(daysWeatherResponse: DaysWeatherResponse)
    suspend fun getCurrentWeather(): Flow<WeatherResponse>
    suspend fun getDaysWeather(): Flow<DaysWeatherResponse>

    suspend fun addLocationToFavourite(weatherResponse: WeatherResponse)
    suspend fun getFavouritePlaces(): Flow<List<WeatherResponse>>
    suspend fun deleteLocationToFavourite(weatherResponse: WeatherResponse)
    suspend fun addAlert(alert: Alerts)
    suspend fun getAlert(): Flow<List<Alerts>>
    suspend fun deleteAlert(alert: Alerts)
}