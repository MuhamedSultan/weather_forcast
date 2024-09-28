package com.example.weatherforcast.favourite.repo

import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {

    suspend fun getCurrentWeather(lat:Double,lon:Double): Flow<Result<WeatherResponse?>>
    suspend fun getDaysWeather(lat:Double,lon:Double): Flow<Result<DaysWeatherResponse?>>
    suspend fun addLocationToFavourite(weatherResponse: WeatherResponse)
   suspend fun getFavouritePlaces():Flow<List<WeatherResponse>>
    suspend fun deleteLocationToFavourite(weatherResponse: WeatherResponse)

}