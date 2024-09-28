package com.example.weatherforcast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforcast.pojo.alerts.Alerts
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse


@Database(entities = [WeatherResponse::class,DaysWeatherResponse::class, Alerts::class], version = 2)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null

        @Synchronized
        fun getInstance(context: Context): WeatherDatabase? {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java, "weather_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }
}
