package com.example.weatherforcast.pojo.days_weather

data class DaysWeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<State>,
    val message: Int
)