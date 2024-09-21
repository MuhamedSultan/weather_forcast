package com.example.weatherforcast.favourite.view

import com.example.weatherforcast.pojo.current_weather.WeatherResponse

interface OnFavouriteClick {
    fun onItemFavouriteClick(weatherResponse: WeatherResponse)
    fun onDeleteItemFavouriteClick(weatherResponse: WeatherResponse)
}