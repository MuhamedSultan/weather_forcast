package com.example.weatherforcast.db

import com.example.weatherforcast.pojo.alerts.Alerts
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val weatherDao: WeatherDao) :LocalDataSource {
    override suspend fun addCurrentWeather(weatherResponse: WeatherResponse) {
        weatherDao.addCurrentWeather(weatherResponse)
    }

    override suspend fun addDaysWeather(daysWeatherResponse: DaysWeatherResponse) {
       weatherDao.addDaysWeather(daysWeatherResponse)
    }

    override suspend fun getCurrentWeather(): Flow<WeatherResponse> {
       return weatherDao.getCurrentWeather()
    }

    override suspend fun getDaysWeather(): Flow<DaysWeatherResponse> {
        return weatherDao.getDaysWeather()
    }

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

   override suspend fun updateWeather(weatherResponse: WeatherResponse){
        weatherDao.updateWeather(weatherResponse)
    }
}