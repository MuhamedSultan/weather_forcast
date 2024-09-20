package com.example.weatherforcast.home.repo

import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.utils.Result

interface HomeRepository {
    suspend fun getCurrentWeather(lat:Double,lon:Double):Result<WeatherResponse>
    suspend fun getDaysWeather(lat:Double,lon:Double):Result<DaysWeatherResponse>

}