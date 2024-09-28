package com.example.weatherforcast.pojo.current_weather

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherforcast.db.Converters

@Entity(tableName = "weather_table")
@TypeConverters(Converters::class)

data class WeatherResponse(
    val base: String,
    val clouds: Clouds,
    val cod: Int,
    val coord: Coord,
    val dt: Int,
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val main: Main,
    val name: String,
    val rain: Rain?,
    val sys: Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<Weather>,
    val wind: Wind,
    var isHome:Boolean=false
)