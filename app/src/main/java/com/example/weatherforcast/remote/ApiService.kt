package com.example.weatherforcast.remote

import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = API_KEY
    ): Response<WeatherResponse>


    @GET("data/2.5/forecast")
    suspend fun getDaysWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = API_KEY
    ):Response<DaysWeatherResponse>
}
