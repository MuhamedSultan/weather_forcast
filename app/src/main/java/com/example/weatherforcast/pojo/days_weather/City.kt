package com.example.weatherforcast.pojo.days_weather

data class City(
    val coord: Coord,
    val country: String?=null,
    val id: Int?=null,
    val name: String?=null,
    val population: Int?=null,
    val sunrise: Int?=null,
    val sunset: Int?=null,
    val timezone: Int?=null
)