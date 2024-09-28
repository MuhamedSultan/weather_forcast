package com.example.weatherforcast.home

import com.example.weatherforcast.pojo.current_weather.Clouds
import com.example.weatherforcast.pojo.current_weather.Coord
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.Sys
import com.example.weatherforcast.pojo.current_weather.Weather
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.current_weather.Wind
import com.example.weatherforcast.pojo.days_weather.City
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.pojo.days_weather.Rain
import com.example.weatherforcast.pojo.days_weather.State

object ApiWeatherResponse {
    val lat = 30.0
    val lon = 50.0
    val testWeatherResponse = WeatherResponse(
        base = "test",
        clouds = Clouds(all = 0),
        cod = 200,
        coord = Coord(lat, lon),
        dt = 1625070130,
        id = 12345,
        main = Main(temp = 30.0, pressure = 1013, humidity = 50),
        name = "Test City",
        rain = null,
        sys = Sys(type = 1, id = 123, country = "US", sunrise = 1625054934, sunset = 1625110134),
        timezone = -14400,
        visibility = 10000,
        weather = listOf(
            Weather(id = 500, main = "Rain", description = "light rain", icon = "10d")
        ),
        wind = Wind(speed = 5.0, deg = 300, gust = 7.0)
    )

    val testDaysWeatherResponse = DaysWeatherResponse(
        id = 1,
        city = City(id = 1, name = "Test City", coord = com.example.weatherforcast.pojo.days_weather.Coord(30.0, 50.0), country = "US", population = 100000),
        cnt = 7,
        cod = "200",
        list = listOf(State(dt = 1625070130, main = com.example.weatherforcast.pojo.days_weather.Main(temp = 30.0, pressure = 1013, humidity = 50,), rain = Rain(0.0), weather = listOf(com.example.weatherforcast.pojo.days_weather.Weather(id = 500, main = "Rain", description = "light rain", icon = "10d")))),
        message = 0
    )
}