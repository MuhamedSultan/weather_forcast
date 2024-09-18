package com.example.weatherforcast.remote

import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.utils.Result


interface RemoteDataSource {

    suspend fun getCurrentWeather(lat:Double,lon:Double) :Result<WeatherResponse>
}