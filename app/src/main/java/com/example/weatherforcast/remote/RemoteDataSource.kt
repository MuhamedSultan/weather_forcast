package com.example.weatherforcast.remote

import com.example.weatherforcast.pojo.current_weather.WeatherResponse

interface RemoteDataSource {

    suspend fun getCurrentWeather(lat:Double,lon:Double) :Result<WeatherResponse>
}