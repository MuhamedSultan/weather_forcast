package com.example.weatherforcast.db

import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse

interface LocalDataSource {

    suspend fun addLocationToFavourite(weatherResponse: WeatherResponse)
    suspend fun getFavouritePlaces():List<WeatherResponse>
    suspend fun deleteLocationToFavourite(weatherResponse: WeatherResponse)

}