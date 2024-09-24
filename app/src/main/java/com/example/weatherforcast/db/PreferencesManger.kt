package com.example.weatherforcast.db

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun saveSelectedOption(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getSelectedOption(key: String?, defaultValue: String? = "en"): String? {
        return sharedPreferences.getString(key, defaultValue)
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


    companion object {
        private const val PREF_NAME = "user_settings"
        const val KEY_LANGUAGE = "language_key"
        const val KEY_TEMP_UNIT = "temperature_unit"
        const val KEY_WIND_SPEED_UNIT = "wind_speed_unit"
    }
}
