package com.example.weatherforcast.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcast.home.repo.HomeRepository
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    private val _currentWeather: MutableLiveData<WeatherResponse> = MutableLiveData()
    val currentWeather: LiveData<WeatherResponse> = _currentWeather

    private val _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error


    fun getCurrentWeather(lat: Double, lon: Double) =
        viewModelScope.launch(Dispatchers.IO) {
            val result = homeRepository.getCurrentWeather(lat, lon)
            result.onSuccess { weatherResponse ->
                _currentWeather.postValue(weatherResponse)
            }
        }
}