package com.example.weatherforcast.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.WeatherResponse

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLocationToFavourite(weatherResponse: WeatherResponse)

    @Query("SELECT * FROM weather_table")
   suspend fun getFavouritePlaces():List<WeatherResponse>

   @Delete
   suspend fun deleteLocationFromFavourite(weatherResponse: WeatherResponse)
}