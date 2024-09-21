package com.example.weatherforcast.pojo.current_weather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("main_table")
data class Main(
    @PrimaryKey
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double
)