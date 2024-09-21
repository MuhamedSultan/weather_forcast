package com.example.weatherforcast.favourite.repo

import com.example.weatherforcast.db.LocalDataSource
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.utils.Result

class FavouriteRepositoryImpl(private val remoteDataSource: RemoteDataSource,private val localDataSource: LocalDataSource):FavouriteRepository {
    override suspend fun getCurrentWeather(lat: Double, lon: Double) : Result<WeatherResponse> {
        return remoteDataSource.getCurrentWeather(lat,lon)
    }

    override suspend fun addLocationToFavourite(weatherResponse: WeatherResponse) {
      localDataSource.addLocationToFavourite(weatherResponse)
    }

    override  suspend fun getFavouritePlaces(): List<WeatherResponse> {
        return localDataSource.getFavouritePlaces()
    }
}