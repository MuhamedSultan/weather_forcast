package com.example.weatherforcast.pojo.days_weather

data class State(
    val clouds: Clouds?=null,
    val dt: Int?=null,
    val dt_txt: String?=null,
    val main: Main?=null,
    val pop: Double?=null,
    val rain: Rain?,
    val sys: Sys?=null,
    val visibility: Int?=null,
    val weather: List<Weather>?=null,
    val wind: Wind?=null
)