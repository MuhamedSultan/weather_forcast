package com.example.weatherforcast.favourite.repo

import com.example.weatherforcast.db.LocalDataSource
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.flow.Flow

class FavouriteRepositoryImpl(private val remoteDataSource: RemoteDataSource,private val localDataSource: LocalDataSource):FavouriteRepository {
    override suspend fun getCurrentWeather(lat: Double, lon: Double) : Flow<Result<WeatherResponse?>> {
        return remoteDataSource.getCurrentWeather(lat,lon)
    }
    override suspend fun getDaysWeather(lat: Double, lon: Double): Flow<Result<DaysWeatherResponse?>> {
        return remoteDataSource.getDaysWeather(lat,lon)
    }

    override suspend fun addLocationToFavourite(weatherResponse: WeatherResponse) {
      localDataSource.addLocationToFavourite(weatherResponse)
    }

    override  suspend fun getFavouritePlaces(): Flow<List<WeatherResponse>> {
        return localDataSource.getFavouritePlaces()
    }

    override suspend fun deleteLocationToFavourite(weatherResponse: WeatherResponse) {
        localDataSource.deleteLocationToFavourite(weatherResponse)
    }
}