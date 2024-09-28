package com.example.weatherforcast.home.repo

import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteDataSource : RemoteDataSource {

    private var fail: Boolean = false
    private var currentWeather: WeatherResponse? = null
    private var daysWeather: DaysWeatherResponse? = null

    fun setShouldFail(shouldFail: Boolean) {
        this.fail = shouldFail
    }

    fun setCurrentWeather(weatherResponse: WeatherResponse?) {
        this.currentWeather = weatherResponse
    }

    fun setDaysWeather(daysWeatherResponse: DaysWeatherResponse) {
        this.daysWeather = daysWeatherResponse
    }

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double
    ): Flow<Result<WeatherResponse?>> {
        return if (fail) {
            flowOf(Result.Error("Failed to fetch current weather"))
        } else {
            flowOf(Result.Success(currentWeather))
        }
    }

    override suspend fun getDaysWeather(lat: Double, lon: Double): Flow<Result<DaysWeatherResponse?>> {
        return if (fail) {
            flowOf(Result.Error("Failed to fetch days weather"))
        } else {
            flowOf(Result.Success(daysWeather))
        }
    }
}
