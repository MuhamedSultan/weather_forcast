package com.example.weatherforcast.favourite.view

import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse

interface OnFavouriteClick {
    fun onItemFavouriteClick(weatherResponse: WeatherResponse)
    fun onDeleteItemFavouriteClick(weatherResponse: WeatherResponse)
}