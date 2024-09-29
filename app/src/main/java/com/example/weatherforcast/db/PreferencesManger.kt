package com.example.weatherforcast.db

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveSelectedOption(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getSelectedOption(key: String?, defaultValue: String? = "en"): String? {
        val value = sharedPreferences.getString(key, defaultValue)
        return value
    }



    fun saveTemperatureUnit(value: String) {
        editor.putString("temperature_unit", value)
        editor.apply()
    }

    fun getTemperatureUnit(defaultValue: String = "Celsius"): String? {
        return sharedPreferences.getString("temperature_unit", defaultValue)
    }

    fun saveWindSpeedUnit(value: String) {
        editor.putString("wind_speed_unit", value)
        editor.apply()
    }

    fun getWindSpeedUnit(defaultValue: String = "Km/h"): String? {
        return sharedPreferences.getString("wind_speed_unit", defaultValue)
    }

    fun saveLocationOption(value: String){
        editor.putString(KEY_LOCATION,value)
        editor.apply()
    }
    fun getSavedLocation(defaultValue: String="Gps"):String?{
        return sharedPreferences.getString(KEY_LOCATION,defaultValue)
    }


    fun saveSelectedLocation(lat: String, lon: String) {
        editor.putString("latitude", lat)
        editor.putString("longitude", lon)
        editor.apply()
    }

    fun getSavedLatitude(defaultValue: String = "0.0"): String? {
        return sharedPreferences.getString("latitude", defaultValue)
    }

    fun getSavedLongitude(defaultValue: String = "0.0"): String? {
        return sharedPreferences.getString("longitude", defaultValue)
    }

    fun saveMapSelected(value: Boolean) {
        editor.putBoolean(KEY_MAP_SELECTED, value)
        editor.apply()
    }

    fun isMapSelected(): Boolean {
        return sharedPreferences.getBoolean(KEY_MAP_SELECTED, false)
    }


    companion object {
        private const val PREF_NAME = "user_settings"
        const val KEY_LANGUAGE = "language_key"
        const val KEY_TEMP_UNIT = "temperature_unit"
        const val KEY_WIND_SPEED_UNIT = "wind_speed_unit"
        const val KEY_LOCATION = "location_key"
        const val KEY_MAP_SELECTED = "map_selected"

    }
}
