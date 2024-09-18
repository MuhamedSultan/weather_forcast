package com.example.weatherforcast.home.repo

import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.utils.Result

class HomeRepositoryImpl(private val remoteDataSource: RemoteDataSource):HomeRepository {
    override suspend fun getCurrentWeather(lat: Double, lon: Double):Result<WeatherResponse> {
       return remoteDataSource.getCurrentWeather(lat,lon)
    }
}