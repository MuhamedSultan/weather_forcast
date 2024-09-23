package com.example.weatherforcast.db

import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    suspend fun addLocationToFavourite(weatherResponse: WeatherResponse)
    suspend fun getFavouritePlaces():Flow<List<WeatherResponse>>
    suspend fun deleteLocationToFavourite(weatherResponse: WeatherResponse)

}