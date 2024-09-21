package com.example.weatherforcast.favourite.repo

import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.utils.Result

interface FavouriteRepository {

    suspend fun getCurrentWeather(lat:Double,lon:Double):Result<WeatherResponse>
    suspend fun getDaysWeather(lat:Double,lon:Double):Result<DaysWeatherResponse>
    suspend fun addLocationToFavourite(weatherResponse: WeatherResponse)
   suspend fun getFavouritePlaces():List<WeatherResponse>
    suspend fun deleteLocationToFavourite(weatherResponse: WeatherResponse)

}