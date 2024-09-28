package com.example.weatherforcast.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.weatherforcast.pojo.current_weather.Clouds
import com.example.weatherforcast.pojo.current_weather.Coord
import com.example.weatherforcast.pojo.current_weather.Main
import com.example.weatherforcast.pojo.current_weather.Rain
import com.example.weatherforcast.pojo.current_weather.Sys
import com.example.weatherforcast.pojo.current_weather.Weather
import com.example.weatherforcast.pojo.current_weather.Wind
import com.example.weatherforcast.pojo.days_weather.City
import com.example.weatherforcast.pojo.days_weather.State

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromCoord(coord: Coord?): String {
        return gson.toJson(coord)
    }

    @TypeConverter
    fun toCoord(coordString: String?): Coord {
        val type = object : TypeToken<Coord>() {}.type
        return gson.fromJson(coordString, type)
    }

    @TypeConverter
    fun fromClouds(clouds: Clouds?): String {
        return gson.toJson(clouds)
    }

    @TypeConverter
    fun toClouds(cloudsString: String?): Clouds {
        val type = object : TypeToken<Clouds>() {}.type
        return gson.fromJson(cloudsString, type)
    }

    @TypeConverter
    fun fromMain(main: Main?): String {
        return gson.toJson(main)
    }

    @TypeConverter
    fun toMain(mainString: String?): Main {
        val type = object : TypeToken<Main>() {}.type
        return gson.fromJson(mainString, type)
    }

    @TypeConverter
    fun fromRain(rain: Rain?): String {
        return gson.toJson(rain)
    }

    @TypeConverter
    fun toRain(rainString: String?): Rain? {
        val type = object : TypeToken<Rain>() {}.type
        return gson.fromJson(rainString, type)
    }

    @TypeConverter
    fun fromSys(sys: Sys?): String {
        return gson.toJson(sys)
    }

    @TypeConverter
    fun toSys(sysString: String?): Sys {
        val type = object : TypeToken<Sys>() {}.type
        return gson.fromJson(sysString, type)
    }

    @TypeConverter
    fun fromWeatherList(weather: List<Weather>?): String {
        return gson.toJson(weather)
    }

    @TypeConverter
    fun toWeatherList(weatherString: String?): List<Weather> {
        val type = object : TypeToken<List<Weather>>() {}.type
        return gson.fromJson(weatherString, type)
    }

    @TypeConverter
    fun fromWind(wind: Wind?): String {
        return gson.toJson(wind)
    }

    @TypeConverter
    fun toWind(windString: String?): Wind {
        val type = object : TypeToken<Wind>() {}.type
        return gson.fromJson(windString, type)
    }

    @TypeConverter
    fun fromCity(city: City): String {
        return gson.toJson(city)
    }

    @TypeConverter
    fun toCity(cityString: String?): City {
        val type = object : TypeToken<City>() {}.type
        return gson.fromJson(cityString, type)
    }

    @TypeConverter
    fun fromStateList(list: List<State>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStateList(stateString: String?): List<State> {
        val type = object : TypeToken<List<State>>() {}.type
        return gson.fromJson(stateString, type)
    }



}
