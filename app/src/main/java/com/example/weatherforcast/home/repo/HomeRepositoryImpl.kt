package com.example.weatherforcast.home.repo

import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.flow.Flow

class HomeRepositoryImpl(private val remoteDataSource: RemoteDataSource):HomeRepository {
    override suspend fun getCurrentWeather(lat: Double, lon: Double):Flow<Result<WeatherResponse>> {
       return remoteDataSource.getCurrentWeather(lat,lon)
    }

    override suspend fun getDaysWeather(lat: Double, lon: Double): Flow<Result<DaysWeatherResponse>> {
        return remoteDataSource.getDaysWeather(lat,lon)
    }
}