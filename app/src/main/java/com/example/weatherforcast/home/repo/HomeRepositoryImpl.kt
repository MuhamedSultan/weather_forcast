package com.example.weatherforcast.home.repo

import android.content.Context
import com.example.weatherforcast.db.LocalDataSource
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.utils.ConnectivityHelper
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


class HomeRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val context: Context // Pass the context to check connectivity
) : HomeRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Flow<Result<WeatherResponse>> = flow {
        if (ConnectivityHelper.isInternetAvailable(context)) {
            // Fetch from remote data source
            val remoteResult = remoteDataSource.getCurrentWeather(lat, lon)
            emitAll(remoteResult)
            // Optionally save to local database
            remoteResult.collect { result ->
                if (result is Result.Success) {
                    localDataSource.addCurrentWeather(result.data!!)
                }
            }
        } else {
            // Fetch from local data source
            emitAll(localDataSource.getCurrentWeather().map { Result.Success(it) })
        }
    }

    override suspend fun getDaysWeather(lat: Double, lon: Double): Flow<Result<DaysWeatherResponse>> = flow {
        if (ConnectivityHelper.isInternetAvailable(context)) {
            // Fetch from remote data source
            val remoteResult = remoteDataSource.getDaysWeather(lat, lon)
            emitAll(remoteResult)
            // Optionally save to local database
            remoteResult.collect { result ->
                if (result is Result.Success) {
                    localDataSource.addDaysWeather(result.data!!)
                }
            }
        } else {
            // Fetch from local data source
            emitAll(localDataSource.getDaysWeather().map { Result.Success(it) })
        }
    }

    override suspend fun addCurrentWeather(weatherResponse: WeatherResponse) {
        localDataSource.addCurrentWeather(weatherResponse)
    }

    override suspend fun addDaysWeather(daysWeatherResponse: DaysWeatherResponse) {
        localDataSource.addDaysWeather(daysWeatherResponse)
    }
}
