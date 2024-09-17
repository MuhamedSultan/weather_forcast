package com.example.weatherforcast.home.repo

import com.example.weatherforcast.pojo.current_weather.WeatherResponse

interface HomeRepository {
    suspend fun getCurrentWeather(lat:Double,lon:Double):Result<WeatherResponse>

}