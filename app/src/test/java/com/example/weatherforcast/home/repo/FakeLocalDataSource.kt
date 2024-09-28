package com.example.weatherforcast.home.repo
import com.example.weatherforcast.db.LocalDataSource
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource : LocalDataSource {

    private var storedCurrentWeather: WeatherResponse? = null
    private var storedDaysWeather: DaysWeatherResponse? = null

    override suspend fun addCurrentWeather(weatherResponse: WeatherResponse) {
        storedCurrentWeather = weatherResponse
    }

    override suspend fun addDaysWeather(daysWeatherResponse: DaysWeatherResponse) {
        storedDaysWeather = daysWeatherResponse
    }

    override suspend fun getCurrentWeather(): Flow<WeatherResponse> {
        return flowOf(storedCurrentWeather!!)
    }

    override suspend fun getDaysWeather(): Flow<DaysWeatherResponse> {
        return flowOf(storedDaysWeather!!)
    }

    override suspend fun addLocationToFavourite(weatherResponse: WeatherResponse) {
    }

    override suspend fun getFavouritePlaces(): Flow<List<WeatherResponse>> {
        return flowOf(emptyList())
    }

    override suspend fun deleteLocationToFavourite(weatherResponse: WeatherResponse) {
    }

    override suspend fun addAlert(alert: com.example.weatherforcast.pojo.alerts.Alerts) {
    }

    override suspend fun getAlert(): Flow<List<com.example.weatherforcast.pojo.alerts.Alerts>> {
        return flowOf(emptyList())
    }

    override suspend fun deleteAlert(alert: com.example.weatherforcast.pojo.alerts.Alerts) {
    }

    fun getStoredWeather(): WeatherResponse? {
        return storedCurrentWeather
    }
    fun getStoredDaysWeather(): DaysWeatherResponse? {
        return storedDaysWeather
    }
}
