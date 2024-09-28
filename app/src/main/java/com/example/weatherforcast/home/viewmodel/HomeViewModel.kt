package com.example.weatherforcast.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.home.repo.HomeRepository
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    private val _weatherResult: MutableStateFlow<Result<WeatherResponse>> =
        MutableStateFlow(Result.Loading())
    val weatherResult: StateFlow<Result<WeatherResponse>> = _weatherResult

    private val _daysWeatherResult: MutableStateFlow<Result<DaysWeatherResponse>> =
        MutableStateFlow(Result.Loading())
    val daysWeatherResult: StateFlow<Result<DaysWeatherResponse>> = _daysWeatherResult

    fun getCurrentWeather(lat: Double, lon: Double) = viewModelScope.launch(Dispatchers.IO) {
        homeRepository.getCurrentWeather(lat, lon)
            .catch { e ->
                _weatherResult.value = Result.Error(e.message.toString())
            }
            .collect { result ->
                _weatherResult.value = result
            }
    }

    fun getDaysWeather(lat: Double, lon: Double) = viewModelScope.launch(Dispatchers.IO) {
        homeRepository.getDaysWeather(lat, lon)
            .catch { e ->
                _daysWeatherResult.value = Result.Error(e.message.toString())
            }
            .collect { result ->
                _daysWeatherResult.value = result
            }
    }

    fun addCurrentWeather(weatherResponse: WeatherResponse) = viewModelScope.launch {
        homeRepository.addCurrentWeather(weatherResponse)
    }

    fun addDaysCurrentWeather(daysWeatherResponse: DaysWeatherResponse) = viewModelScope.launch {
        homeRepository.addDaysWeather(daysWeatherResponse)
    }
}
