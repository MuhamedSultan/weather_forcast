package com.example.weatherforcast.db

import com.example.weatherforcast.pojo.alerts.Alerts
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val weatherDao: WeatherDao) :LocalDataSource {
    override suspend fun addLocationToFavourite(weatherResponse: WeatherResponse) {
        weatherDao.addLocationToFavourite(weatherResponse)
    }

    override suspend fun getFavouritePlaces(): Flow<List<WeatherResponse>> {
       return weatherDao.getFavouritePlaces()
    }

    override suspend fun deleteLocationToFavourite(weatherResponse: WeatherResponse) {
        weatherDao.deleteLocationFromFavourite(weatherResponse)
    }

    override suspend fun addAlert(alert: Alerts) {
       weatherDao.addAlert(alert)
    }

    override suspend fun getAlert(): Flow<List<Alerts>> {
        return weatherDao.getAlert()
    }

    override suspend fun deleteAlert(alert: Alerts) {
        weatherDao.deleteAlert(alert)
    }
}