package com.example.weatherforcast.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.weatherforcast.pojo.alerts.Alerts
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCurrentWeather(weatherResponse: WeatherResponse)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addDaysWeather(daysWeatherResponse: DaysWeatherResponse)

    @Query("SELECT * FROM WEATHER_TABLE")
    fun getCurrentWeather(): Flow<WeatherResponse>

    @Query("SELECT * FROM days_weather")
    fun getDaysWeather(): Flow<DaysWeatherResponse>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocationToFavourite(weatherResponse: WeatherResponse)

    @Query("SELECT * FROM weather_table Where isHome=0")
    fun getFavouritePlaces(): Flow<List<WeatherResponse>>

    @Delete
    suspend fun deleteLocationFromFavourite(weatherResponse: WeatherResponse)


    @Update
    suspend fun updateWeather(weatherResponse: WeatherResponse)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAlert(alert: Alerts)

    @Query("SELECT * FROM NOTIFICATION_TABLE")
    fun getAlert(): Flow<List<Alerts>>

    @Delete
    suspend fun deleteAlert(alert: Alerts)
}