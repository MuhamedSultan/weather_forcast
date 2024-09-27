package com.example.weatherforcast.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforcast.pojo.alerts.Alerts
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocationToFavourite(weatherResponse: WeatherResponse)

    @Query("SELECT * FROM weather_table")
    fun getFavouritePlaces():Flow<List<WeatherResponse>>

   @Delete
   suspend fun deleteLocationFromFavourite(weatherResponse: WeatherResponse)

   @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun addAlert(alert: Alerts)

   @Query("SELECT * FROM NOTIFICATION_TABLE")
   fun getAlert():Flow<List<Alerts>>

   @Delete
   suspend fun deleteAlert(alert: Alerts)
}