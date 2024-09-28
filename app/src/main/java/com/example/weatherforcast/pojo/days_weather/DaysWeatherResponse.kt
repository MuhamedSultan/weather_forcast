package com.example.weatherforcast.pojo.days_weather

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.weatherforcast.db.Converters

@Entity(tableName = "days_weather")
@TypeConverters(Converters::class)
data class DaysWeatherResponse(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<State>,
    val message: Int
)